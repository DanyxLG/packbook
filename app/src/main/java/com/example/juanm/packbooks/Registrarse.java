package com.example.juanm.packbooks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.HashMap;
import java.util.Map;

import negocio.recorrido;
import negocio.servidor;
import negocio.singletonDatos;

public class Registrarse extends AppCompatActivity {
    EditText nombreET;
    EditText apellidoET;
    EditText usernameET;
    EditText correoET;
    EditText telefonoET;
    @Password(min = 8, scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS, message = "La contraseña debe tener de al menos 8 caracteres, un numero y un simbolo ")
    EditText passwordET;
    EditText repPasswordET;
    String serverURL= servidor.servicio("/users/registrarse");
    ProgressDialog progressDialog;
    Button btnRegistrar;
    private String valid_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        mostrarRetorno();
        email_correcto();
    }
    public void mostrarMensaje(String titulo,String mensaje){
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder=new AlertDialog.Builder(Registrarse.this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }
     public void mostrarRetorno(){
       ActionBar barra= getSupportActionBar();
        if(barra!=null)
            barra.setDisplayHomeAsUpEnabled(true);
    }
    public void email_correcto(){
        btnRegistrar = (Button)findViewById(R.id.registrar_crear_BT);
        correoET=(EditText)findViewById(R.id.registrar_email_ET);
        telefonoET=(EditText) findViewById(R.id.registrar_telefono_ET);
        correoET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Is_Valid_Email(correoET);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            public void Is_Valid_Email(EditText edt) {
                if (edt.getText().toString() == null || !isEmailValid(edt.getText().toString()))  {
                    edt.setError("Email incorrecto");
                    valid_email = null;
                    btnRegistrar.setEnabled(false);
                    btnRegistrar.setBackgroundColor(Color.parseColor("#FF0000"));
                }else{
                    valid_email = edt.getText().toString();
                    btnRegistrar.setEnabled(true);
                    btnRegistrar.setBackgroundColor(Color.parseColor("#945400"));
                }
            }
            boolean isEmailValid(CharSequence email) {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
        });
        telefonoET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnRegistrar.setEnabled(true);
                btnRegistrar.setBackgroundColor(Color.parseColor("#945400"));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnRegistrar.setEnabled(true);
                btnRegistrar.setBackgroundColor(Color.parseColor("#945400"));
                if(telefonoET.getText().toString().length()!=10){
                    telefonoET.setError("El teléfono celular debe tener 10 dígitos y empezar en 09");
                    btnRegistrar.setEnabled(false);
                    btnRegistrar.setBackgroundColor(Color.parseColor("#FF0000"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String inicio_telefono=telefonoET.getText().toString().substring(0,2);
                    if(!inicio_telefono.equals("09")){
                        telefonoET.setError("El teléfono celular debe empezar en 09");
                    }
                }catch (Exception e){
                    telefonoET.setError("El teléfono celular debe empezar en 09");
                    btnRegistrar.setEnabled(false);
                    btnRegistrar.setBackgroundColor(Color.parseColor("#FF0000"));
                }
            }
        });
    }
    public void volverLogin(){
        startActivity(new Intent(getApplicationContext(), Login.class));
    }
    public void Registrarse(View v){
        progressDialog = new ProgressDialog(Registrarse.this);
        progressDialog.setMessage("Comprobando...");
        progressDialog.show();

        nombreET=(EditText)findViewById(R.id.registrar_nombre_ET);
        apellidoET=(EditText)findViewById(R.id.registrar_apellido_ET);
        usernameET=(EditText)findViewById(R.id.registrar_username_ET);
        correoET=(EditText)findViewById(R.id.registrar_email_ET);
        telefonoET=(EditText)findViewById(R.id.registrar_telefono_ET);
        passwordET=(EditText)findViewById(R.id.registrar_contrasena_ET);
        repPasswordET=(EditText)findViewById(R.id.registrar_repcontra_Et);

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
                                mostrarMensaje("Error", "Contraseñas no coinciden");
                                return;
                            }
                            if((result.toString()).equals("Error 3")){
                                mostrarMensaje("Error", "Ya existe esta cuenta");
                                return;
                            }
                            if((result.toString()).equals("Error 4")) {
                                mostrarMensaje("Error", "Datos incompletos");
                                return;
                            }
                            AlertDialog.Builder alertDialogBuilder;
                            alertDialogBuilder=new AlertDialog.Builder(Registrarse.this);
                            alertDialogBuilder.setTitle("Listo");
                            alertDialogBuilder.setMessage("Cuenta creada, confirme su email");
                            AlertDialog alertDialog=alertDialogBuilder.create();

                            alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Aceptar",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   volverLogin();
                                }
                            });
                            alertDialog.show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    mostrarMensaje("Error", "Su cuenta se ha creado");
                    error.printStackTrace();
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map <String,String> params =new HashMap<String, String >();
                    params.put("nombre",nombreET.getText().toString());
                    params.put("ape",apellidoET.getText().toString());
                    params.put("username",usernameET.getText().toString());
                    params.put("correo",correoET.getText().toString());
                    params.put("telefono",telefonoET.getText().toString());
                    params.put("password",passwordET.getText().toString());
                    params.put("reppass",repPasswordET.getText().toString());
                    return params;
                }
            };
            singletonDatos.getInstancia(Registrarse.this).addToRequest(stringRequest);
        }else{
            if (progressDialog != null)
                progressDialog.dismiss();
            mostrarMensaje("Error", "Datos necesarios");
        }

    }
}
