package com.example.juanm.packbooks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import negocio.recorrido;
import negocio.servidor;
import negocio.singletonDatos;

public class bajarLibros extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String eleccion="Publico";
    String categoria="Todas";
    Spinner categorias;
    String serverURL= servidor.servicio("/libros/buscador");
    ProgressDialog progressDialog;
    EditText parametro;
    DBHelper dbSQLITE;
    ArrayList<String> libros_listado;
    ArrayList<String> links;
    String usuario;
    ListView lista_libros;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bajar_libros);
        categorias=(Spinner)findViewById(R.id.bajar_categoria_SP);
        categorias.setAdapter(ArrayAdapter.createFromResource(bajarLibros.this, R.array.Categorias,android.R.layout.simple_spinner_item));
        dbSQLITE = new DBHelper(this);
        Cursor res = dbSQLITE.selectVerTodos();
        while (res.moveToNext()){
            usuario=res.getString(3);
        }
        lista_libros=(ListView)findViewById(R.id.lista_libros);
    }
    public void Buscar_privados(View v){
        eleccion="Privado";
        Buscar();
    }

    public void Buscar_publicos(View v){
        eleccion="Publico";
        Buscar();
    }
    public void Buscar(){
        categoria=categorias.getSelectedItem().toString();
        progressDialog = new ProgressDialog(bajarLibros.this);
        progressDialog.setMessage("Comprobando...");
        progressDialog.show();
        parametro=(EditText)findViewById(R.id.param_libro);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, serverURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        if((result.toString()).equals("Error 1")){
                            mostrarMensaje("Error", "Error de red");
                            return;
                        }
                        if((result.toString()).equals("Error 2")){
                            mostrarMensaje("Error", "No se ha encontrado nada");
                            return;
                        }
                        JsonParser parser = new JsonParser();
                        JsonElement elementObject = parser.parse(result);
                        JsonArray libros=elementObject.getAsJsonArray();
                        libros_listado = new ArrayList<String>();
                        links=new ArrayList<String>();
                        for(int i=0; i < libros.size(); i++){
                            libros_listado.add(
                                    " Nombre: "+libros.get(i).getAsJsonObject().get("nombre").toString()
                                    +" Autor: "+ libros.get(i).getAsJsonObject().get("autor").toString()
                                    +" Editorial: "+ libros.get(i).getAsJsonObject().get("editorial").toString()
                                    +" Año publicación: "+ libros.get(i).getAsJsonObject().get("fechaPub").toString()
                                    + " Categoría: "+ libros.get(i).getAsJsonObject().get("categoria").toString()
                            );
                            String temporal=libros.get(i).getAsJsonObject().get("libro").toString();
                            temporal=temporal.substring(1, temporal.length()-1);
                            links.add(servidor.servicio(temporal));
                        }
                        ArrayAdapter<String> itemsAdapter =
                                new ArrayAdapter<String>(bajarLibros.this, android.R.layout.simple_list_item_1, libros_listado);
                        lista_libros.setAdapter(itemsAdapter);
                        lista_libros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                                AlertDialog.Builder alertDialogBuilder;
                                alertDialogBuilder=new AlertDialog.Builder(bajarLibros.this);
                                alertDialogBuilder.setTitle("¿Qé desea hacer?");
                                alertDialogBuilder.setMessage("Elija una opción");
                                AlertDialog alertDialog=alertDialogBuilder.create();
                                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Descargar libro",new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Uri uri = Uri.parse(links.get(i));
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                });
                                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Compartir libro", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent sendIntent = new Intent();
                                        sendIntent.setAction(Intent.ACTION_SEND);
                                        sendIntent.putExtra(Intent.EXTRA_TEXT,"Hola amig@, te comparto este libro "+Uri.parse(links.get(i))+", desde packbooks");
                                        sendIntent.setType("text/plain");
                                        startActivity(sendIntent);
                                    }
                                });
                                alertDialog.show();
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                AlertDialog.Builder alertDialogBuilder;
                alertDialogBuilder=new AlertDialog.Builder(bajarLibros.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder.setMessage(error.toString());
                AlertDialog alertDialog=alertDialogBuilder.create();
                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Aceptar",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Buscar();
                    }
                });
                alertDialog.show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params =new HashMap<String, String >();
                params.put("busqueda",parametro.getText().toString());
                params.put("categoria",categoria);
                params.put("por", usuario);
                params.put("tipo", eleccion);
                return params;
            }
        };
        singletonDatos.getInstancia(bajarLibros.this).addToRequest(stringRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void mostrarMensaje(String titulo,String mensaje){
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder=new AlertDialog.Builder(bajarLibros.this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }
}
