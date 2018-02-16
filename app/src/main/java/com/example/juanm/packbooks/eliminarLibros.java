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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import negocio.servidor;
import negocio.singletonDatos;

public class eliminarLibros extends AppCompatActivity {
    DBHelper dbSQLITE;
    String usuario;
    ArrayList<String> libros_listado;
    ArrayList<String> codigos;
    String serverURL= servidor.servicio("/libros/mios");
    ProgressDialog progressDialog;
    ListView lista_libros;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_libros);
        progressDialog = new ProgressDialog(eliminarLibros.this);
        progressDialog.setMessage("Comprobando...");
        progressDialog.show();
        lista_libros=(ListView)findViewById(R.id.Lista_libros_eliminar);
        dbSQLITE = new DBHelper(this);
        Cursor res = dbSQLITE.selectVerTodos();
        while (res.moveToNext()){
            usuario=res.getString(3);
        }
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
                        codigos=new ArrayList<String>();
                        for(int i=0; i < libros.size(); i++){
                            libros_listado.add(
                                    " Nombre: "+libros.get(i).getAsJsonObject().get("nombre").toString()
                                            +" Autor: "+ libros.get(i).getAsJsonObject().get("autor").toString()
                                            +" Editorial: "+ libros.get(i).getAsJsonObject().get("editorial").toString()
                                            +" Año publicación: "+ libros.get(i).getAsJsonObject().get("fechaPub").toString()
                                            + " Categoría: "+ libros.get(i).getAsJsonObject().get("categoria").toString()
                            );
                            codigos.add(libros.get(i).getAsJsonObject().get("id").toString());
                        }
                        ArrayAdapter<String> itemsAdapter =
                                new ArrayAdapter<String>(eliminarLibros.this, android.R.layout.simple_list_item_1, libros_listado);
                        lista_libros.setAdapter(itemsAdapter);
                        lista_libros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, final int posicion, long l) {
                                AlertDialog.Builder alertDialogBuilder;
                                alertDialogBuilder=new AlertDialog.Builder(eliminarLibros.this);
                                alertDialogBuilder.setTitle("Alerta");
                                alertDialogBuilder.setMessage("¿Está seguro de eliminar el libro?");
                                AlertDialog alertDialog=alertDialogBuilder.create();
                                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Aceptar",new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        eliminacionLibros(codigos, posicion);
                                    }
                                });
                                alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancelar",new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mostrarMensaje("Listo", "No se eliminó nada");
                                }
                                });
                                alertDialog.show();
                            }
                        });
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                mostrarMensaje("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params =new HashMap<String, String >();
                params.put("por", usuario);
                return params;
            }
        };
        singletonDatos.getInstancia(eliminarLibros.this).addToRequest(stringRequest);
    }
    public void mostrarMensaje(String titulo,String mensaje){
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder=new AlertDialog.Builder(eliminarLibros.this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }
    public void eliminacionLibros(final ArrayList<String> codigos, final int i){
        progressDialog = new ProgressDialog(eliminarLibros.this);
        progressDialog.setMessage("Eliminando...");
        progressDialog.show();
        String urlEliminacion=servidor.servicio("/libros/eliminacion");
        StringRequest eliminacionRequest=new StringRequest(Request.Method.POST, urlEliminacion,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        if((result.toString()).equals("Error 1")){
                            mostrarMensaje("Error", "Error de red");
                            return;
                        }
                        AlertDialog.Builder alertDialogBuilder;
                        alertDialogBuilder=new AlertDialog.Builder(eliminarLibros.this);
                        alertDialogBuilder.setTitle("Listo");
                        alertDialogBuilder.setMessage("Libro Eliminado con éxito");
                        AlertDialog alertDialog=alertDialogBuilder.create();

                        alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Aceptar",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        });
                        alertDialog.show();
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                mostrarMensaje("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params =new HashMap<String, String >();
                String texto=(codigos.get(i)).substring(1, codigos.get(i).length()-1);
                Toast.makeText(eliminarLibros.this, texto, Toast.LENGTH_SHORT).show();
                params.put("id",texto);
                return params;
            }
        };
        singletonDatos.getInstancia(eliminarLibros.this).addToRequest(eliminacionRequest);
    }
}
