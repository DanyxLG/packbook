package com.example.juanm.packbooks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by juanm on 17/01/2018.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NOMBRE = "Packbooks.db";
    public static final String TABLA_NOMBRE = "usuarios";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NOMBRE";
    public static final String COL_3 = "APELLIDO";
    public static final String COL_4 = "USUARIO";
    public static final String COL_5 = "CORREO";
    public static final String COL_6 = "TELEFONO";

    public DBHelper(Context context) {
        super(context, DB_NOMBRE, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format
                ("create table %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT, %s TEXT,%s TEXT, %s TEXT,%s TEXT)",
                        TABLA_NOMBRE,COL_2,COL_3,COL_4,COL_5,COL_6));
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",TABLA_NOMBRE));
        onCreate(db);
    }
    public boolean insertar(String nombre, String apellido, String usuario, String correo, String telefono){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,nombre);
        contentValues.put(COL_3,apellido);
        contentValues.put(COL_4,usuario);
        contentValues.put(COL_5,correo);
        contentValues.put(COL_6,telefono);
        long resultado = db.insert(TABLA_NOMBRE,null,contentValues);
        if(resultado == -1)
            return false;
        else
            System.out.print("listo");
        return true;
    }
    public Cursor selectVerTodos(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(String.format("select * from %s",TABLA_NOMBRE),null);
        return  res;
    }
    public void eliminar (){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLA_NOMBRE,null,null);
    }
}
