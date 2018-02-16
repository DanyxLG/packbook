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

public class Olvido_contra extends AppCompatActivity {
    String serverURL= servidor.servicio("/users/olvido");
    ProgressDialog progressDialog;
    EditText correoET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvido_contra);
    }
    public void  OlvidoPass(View v){
        progressDialog = new ProgressDialog(Olvido_contra.this);
        progressDialog.setMessage("Comprobando...");
        progressDialog.show();
        correoET=(EditText)findViewById(R.id.olvido_correo_Et);
        EditText[] campos=new EditText[]{correoET};
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
                            AlertDialog.Builder alertDialogBuilder;
                            alertDialogBuilder=new AlertDialog.Builder(Olvido_contra.this);
                            alertDialogBuilder.setTitle("Listo");
                            alertDialogBuilder.setMessage("Proceda a cambiar su contraseña");
                            AlertDialog alertDialog=alertDialogBuilder.create();
                            alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Aceptar",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getApplicationContext(), Recuperar.class));
                                }
                            });
                            alertDialog.show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    mostrarMensaje("Alerta", "Revise su email, si no pasa nada vuelva a intentarlo");
                    error.printStackTrace();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map <String,String> params =new HashMap<String, String >();
                    params.put("correo",correoET.getText().toString());
                    return params;
                }
            };
            singletonDatos.getInstancia(Olvido_contra.this).addToRequest(stringRequest);
        }else{
            if (progressDialog != null)
                progressDialog.dismiss();
            mostrarMensaje("Error", "Datos necesarios");
        }
    }
    public void tengo_codigo(View v){
        startActivity(new Intent(getApplicationContext(), Recuperar.class));
    }
    public void mostrarMensaje(String titulo,String mensaje){
        if (progressDialog != null)
            progressDialog.dismiss();
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder=new AlertDialog.Builder(Olvido_contra.this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }
}
