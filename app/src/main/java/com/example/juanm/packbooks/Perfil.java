package com.example.juanm.packbooks;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Perfil extends AppCompatActivity {
    DBHelper dbSQLITE;
    String nombre;
    String apellido;
    String usuario;
    String correo;
    String telefono;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_perfil);
        super.onCreate(savedInstanceState);
        dbSQLITE = new DBHelper(this);
        TextView ETnombre=(TextView) findViewById(R.id.perfil_nombre_TV);
        TextView ETapellido=(TextView) findViewById(R.id.perfil_apellido_TV);
        TextView ETusuario=(TextView) findViewById(R.id.perfil_usuario_TV);
        TextView ETcorreo=(TextView) findViewById(R.id.perfil_correo_TV);
        Cursor res = dbSQLITE.selectVerTodos();
        while (res.moveToNext()){
            nombre=res.getString(1);
            apellido=res.getString(2);
            usuario=res.getString(3);
            correo=res.getString(4);
            telefono=res.getString(5);
        }
        ETnombre.setText("Nombre: "+nombre);
        ETapellido.setText("Apellido: "+apellido);
        ETusuario.setText("Usuario: "+usuario);
        ETcorreo.setText("Email: "+correo);
    }

}
