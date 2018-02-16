package com.example.juanm.packbooks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import negocio.recorrido;
import negocio.servidor;
import negocio.singletonDatos;

public class Confirmar extends AppCompatActivity {
    String serverURL= servidor.servicio("/users/confirmar");
    ProgressDialog progressDialog;
    EditText correoET;
    EditText tokenET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar);
    }
    public void ConfirmarCuenta(View v){
        progressDialog = new ProgressDialog(Confirmar.this);
        progressDialog.setMessage("Comprobando...");
        progressDialog.show();
        correoET=(EditText)findViewById(R.id.confirmar_correo_ET);
        tokenET=(EditText)findViewById(R.id.confirmar_token_ET);
        EditText[] campos=new EditText[]{correoET,tokenET};
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
                                mostrarMensaje("Error", "Cuenta no existe");
                                return;
                            }
                            if((result.toString()).equals("Error 3")){
                                mostrarMensaje("Error", "Token no válido");
                                return;
                            }
                            if((result.toString()).equals("Error 5")){
                                mostrarMensaje("Error", "Esta cuenta ya se encontraba confirmada");
                                return;
                            }
                            AlertDialog.Builder alertDialogBuilder;
                            alertDialogBuilder=new AlertDialog.Builder(Confirmar.this);
                            alertDialogBuilder.setTitle("Listo");
                            alertDialogBuilder.setMessage("Puede iniciar sesión");
                            AlertDialog alertDialog=alertDialogBuilder.create();
                            alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Aceptar",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                }
                            });
                            alertDialog.show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    mostrarMensaje("Alerta", "Cuenta confirmada, si no pasa nada intente nuevamente");
                    error.printStackTrace();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map <String,String> params =new HashMap<String, String >();
                    params.put("correo",correoET.getText().toString());
                    params.put("token",tokenET.getText().toString());
                    return params;
                }
            };
            singletonDatos.getInstancia(Confirmar.this).addToRequest(stringRequest);
        }else{
            if (progressDialog != null)
                progressDialog.dismiss();
            mostrarMensaje("Error", "Datos necesarios");
        }

    }
    public void mostrarMensaje(String titulo,String mensaje){
        if (progressDialog != null)
            progressDialog.dismiss();
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder=new AlertDialog.Builder(Confirmar.this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }
}
