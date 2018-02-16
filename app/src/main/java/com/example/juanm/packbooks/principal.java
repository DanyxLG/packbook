package com.example.juanm.packbooks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class principal extends AppCompatActivity {
    DBHelper dbSQLITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        dbSQLITE = new DBHelper(this);
    }
    @Override
    public void onBackPressed() {

    }

    public void cerrarSesion(View v ){
        dbSQLITE.eliminar();
        startActivity(new Intent(getApplicationContext(),Login.class ));
    }
    public void subir_libro(View v){
        startActivity(new Intent(getApplicationContext(),Subir.class));
    }
    public void verCuenta(View v){
        startActivity(new Intent(getApplicationContext(),Perfil.class));
    }
    public void Acerca_de(View v){
        startActivity(new Intent(getApplicationContext(), Acerca.class));
    }
    public void descargarLibros(View v){
        startActivity(new Intent(getApplicationContext(), bajarLibros.class));
    }
    public void eliminarLibros(View v){
        startActivity(new Intent(getApplicationContext(), eliminarLibros.class));
    }
}
