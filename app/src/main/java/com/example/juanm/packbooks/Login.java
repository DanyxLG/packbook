package com.example.juanm.packbooks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mobsandgeeks.saripaar.annotation.Password;
import java.util.HashMap;
import java.util.Map;

import negocio.recorrido;
import negocio.servidor;
import negocio.singletonDatos;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;



public class Login extends AppCompatActivity {
    DBHelper dbSQLITE;
    public EditText correoET;
    String nombre;
    String apelllido;
    String username;
    String correo;
    String telefono;
    @Password(min = 8, scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS, message = "La contraseña debe tener de al menos 8 caracteres, un numero y un simbolo ")
    EditText passwordET;
    String serverURL= servidor.servicio("/users/login");
    ProgressDialog progressDialog;
    private String valid_email;
    Button BTlogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbSQLITE = new DBHelper(this);
        Cursor res = dbSQLITE.selectVerTodos();
        int valor = res.getCount();
        if (valor > 0)
            startActivity(new Intent(getApplicationContext(), principal.class));

    }

    public void Registrar (View v){startActivity(new Intent(getApplicationContext(), Registrarse.class));}

    public void mostrarMensaje(String titulo,String mensaje){
        if (progressDialog != null)
            progressDialog.dismiss();
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder=new AlertDialog.Builder(Login.this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }

    public void IniciarSesion(View v){
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Comprobando...");
        progressDialog.show();
        correoET=(EditText)findViewById(R.id.login_correoET);
        passwordET=(EditText)findViewById(R.id.login_contrasenaET);
        EditText[] campos=new EditText[]{correoET,passwordET};
        recorrido recor=new recorrido(campos);
        if(recor.Recorrer(campos)){
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
                                mostrarMensaje("Error", "Contraseña incorrecta");
                                return;
                            }
                            if((result.toString()).equals("Error 3")){
                                mostrarMensaje("Error", "Usuario no existe");
                                return;
                            }
                            if((result.toString()).equals("Error 4")){
                                mostrarMensaje("Error", "Cuenta no confirmada");
                                return;
                            }
                            if((result.toString()).equals("Error 5")){
                                mostrarMensaje("Error", "Datos incompletos");
                                return;
                            }
                            //Guarda los valores de datos del usuario en formato tipo JSON
                            JsonParser parser = new JsonParser();
                            JsonElement elementObject = parser.parse(result);
                            nombre = elementObject.getAsJsonObject().get("nombre").getAsString();
                            apelllido = elementObject.getAsJsonObject().get("ape").getAsString();
                            username= elementObject.getAsJsonObject().get("username").getAsString();
                            telefono=elementObject.getAsJsonObject().get("telefono").getAsString();
                            correo=elementObject.getAsJsonObject().get("correo").getAsString();
                            if(correo.isEmpty())
                                mostrarMensaje("Error", "No hay datos");
                            else{
                                boolean estaInsertado =dbSQLITE.insertar(nombre,apelllido,username,correo, telefono);
                                if(estaInsertado){
                                    Toast.makeText(Login.this,"Iniciando sesión",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),principal.class ));
                                }
                                else
                                    mostrarMensaje("Error", "Lo sentimos ocurrió un error");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mostrarMensaje("Error", "El servidor no responde");
                    error.printStackTrace();
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map <String,String> params =new HashMap<String, String >();
                    params.put("username",correoET.getText().toString());
                    params.put("password",passwordET.getText().toString());
                    return params;
                }
            };
            singletonDatos.getInstancia(Login.this).addToRequest(stringRequest);
        }else
            mostrarMensaje("Error", "Datos necesarios");
    }
    public void BTOlvido (View v){
        startActivity(new Intent(getApplicationContext(), Olvido_contra.class));
    }
    public void BTConfirmar (View v){
        startActivity(new Intent(getApplicationContext(), Confirmar.class));
    }
}




