package com.example.juanm.packbooks;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.util.UUID;

import negocio.servidor;


public class Subir extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    Button SelectButton;
    Button UploadButton;
    EditText PdfNameEditText;
    public int PDF_REQ_CODE = 1;
    String PdfNameHolder, PdfPathHolder, PdfID, libro_nombre,
            libro_autor, libro_editorial, libro_publicacion, libro_categoria, libro_por, libro_tipo;
    Uri uri;
    DBHelper dbSQLITE;
    String usuario;
    Spinner spinner;
    Spinner tipo;
    ProgressDialog progressDialog;
    public static final String PDF_UPLOAD_HTTP_URL = servidor.servicio("/libros/subida");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir);
        //1______________________________________________
        spinner=(Spinner)findViewById(R.id.subir_categoria_SP);
        ArrayAdapter adapter= ArrayAdapter.createFromResource(Subir.this, R.array.Categorias,android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        //spinner.setOnItemSelectedListener(this);
        //_______________________________________________
        tipo=(Spinner) findViewById(R.id.subir_tipo_SP);
        ArrayAdapter adapter1=ArrayAdapter.createFromResource(Subir.this, R.array.Tipo,android.R.layout.simple_spinner_item);
        tipo.setAdapter(adapter1);
        //tipo.setOnItemSelectedListener(this);

        dbSQLITE = new DBHelper(this);
        Cursor res = dbSQLITE.selectVerTodos();
        while (res.moveToNext()){ usuario=res.getString(3); }


        AllowRunTimePermission();
        SelectButton = (Button) findViewById(R.id.subir_escoger_BT);
        UploadButton = (Button) findViewById(R.id.subir_subir_BT);
        PdfNameEditText = (EditText) findViewById(R.id.ET_libro);

        SelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Escoger libro"), PDF_REQ_CODE);
            }
        });
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(Subir.this);
                progressDialog.setMessage("Subiendo...");
                progressDialog.show();
                libro_nombre=((EditText)findViewById(R.id.subir_nombreET)).getText().toString();
                libro_autor=((EditText)findViewById(R.id.subir_autor_ET)).getText().toString();
                libro_editorial=((EditText)findViewById(R.id.subir_editorial_ET)).getText().toString();
                libro_publicacion= ((EditText)findViewById(R.id.subir_publicacion_ET)).getText().toString();
                libro_categoria=((EditText)findViewById(R.id.subir_publicacion_ET)).getText().toString();
                libro_por=usuario;
                libro_categoria=((Spinner)findViewById(R.id.subir_categoria_SP)).getSelectedItem().toString();
                libro_tipo=((Spinner)findViewById(R.id.subir_tipo_SP)).getSelectedItem().toString();
                PdfUploadFunction();
            }
        });
    }
    public void AllowRunTimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(Subir.this, Manifest.permission.READ_EXTERNAL_STORAGE))
            Toast.makeText(Subir.this,"Permitir acceso", Toast.LENGTH_LONG).show();
        else
            ActivityCompat.requestPermissions(Subir.this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent fer=data;
        if (requestCode == PDF_REQ_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            SelectButton.setText("Libro seleccionado");
            String[] nombre_libro=(data.getData().toString()).split("/");
            PdfNameEditText.setText(nombre_libro[nombre_libro.length-1]);
        }
    }
    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] Result) {
        switch (RC) {
            case 1:
                if (Result.length > 0 && Result[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(Subir.this,"Permisos correctos", Toast.LENGTH_LONG).show();
                else
                    mostrarMensaje("Error", "No se tiene permisos suficiente");
                break;
        }
    }
    public void PdfUploadFunction() {
        if (progressDialog != null)
            progressDialog.dismiss();
        PdfNameHolder = PdfNameEditText.getText().toString().trim();
        PdfPathHolder = FilePath.getPath(this, uri);
        if (PdfPathHolder == null)
            mostrarMensaje("Error", "Por favor seleccione un libro que no este en la SD");
        else {
            try {
                PdfID = UUID.randomUUID().toString();
                MultipartUploadRequest requerimiento = new MultipartUploadRequest(this, PdfID, PDF_UPLOAD_HTTP_URL)
                        .addFileToUpload(PdfPathHolder, "book")
                        .addParameter("tipo", libro_tipo)
                        .addParameter("nombre", libro_nombre)
                        .addParameter("autor", libro_autor)
                        .addParameter("categoria", libro_categoria)
                        .addParameter("editorial", libro_editorial)
                        .addParameter("por", libro_por)
                        .addParameter("publicacion", libro_publicacion)
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(1);
                requerimiento.startUpload();
                AlertDialog.Builder alertDialogBuilder;
                alertDialogBuilder=new AlertDialog.Builder(Subir.this);
                alertDialogBuilder.setTitle("Listo");
                alertDialogBuilder.setMessage("Su libro será subido en segundo plano");
                AlertDialog alertDialog=alertDialogBuilder.create();
                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Aceptar",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),principal.class ));
                    }
                });
                alertDialog.show();
            } catch (Exception exception) {
                mostrarMensaje("Error",exception.getMessage() );
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void mostrarMensaje(String titulo,String mensaje){
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder=new AlertDialog.Builder(Subir.this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }
}

