package com.example.sanbotapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para la tabla "colores"
 */
public class ColoresDbAdapter {

    private SQLiteDatabase db;
    private final DatabaseHelper dbHelper;

    // Columnas
    public static final String KEY_ID = "_id";
    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_ACIERTO = "acierto";
    public static final String KEY_IMAGEN = "imagen";
    public static final String KEY_NIVEL = "nivel";

    public ColoresDbAdapter(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Abrir conexión
    public ColoresDbAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    // Cerrar conexión
    public void close() {
        dbHelper.close();
    }

    /**
     * Insertar un color
     */
    public long insertColor(String nombre, boolean acierto, String imagen, int nivel) {
        ContentValues values = new ContentValues();
        values.put(KEY_NOMBRE, nombre);
        values.put(KEY_ACIERTO, acierto ? 1 : 0); // SQLite no tiene boolean real
        values.put(KEY_IMAGEN, imagen);
        values.put(KEY_NIVEL, nivel);

        return db.insert("colores", null, values);
    }

    /**
     * Obtener todos los colores
     */
    public Cursor fetchAllColors() {
        return db.query("colores",
                new String[]{KEY_ID, KEY_NOMBRE, KEY_ACIERTO, KEY_IMAGEN, KEY_NIVEL},
                null, null, null, null, null);
    }

    /**
     * Obtener colores por nivel
     */
    public Cursor fetchColorsByLevel(int nivel) {
        return db.query("colores",
                new String[]{KEY_ID, KEY_NOMBRE, KEY_ACIERTO, KEY_IMAGEN, KEY_NIVEL},
                KEY_NIVEL + "=?",
                new String[]{String.valueOf(nivel)},
                null, null, null);
    }

    /**
     * Marcar un color como acertado o no
     */
    public boolean updateAcierto(String nombre, boolean acierto) {
        ContentValues values = new ContentValues();
        values.put(KEY_ACIERTO, acierto ? 1 : 0);

        return db.update("colores", values,
                KEY_NOMBRE + "=?",
                new String[]{String.valueOf(nombre)}) > 0;
    }

    /**
     * Obtener un color por nombre
     */
    public Cursor fetchColorByName(String nombre) {
        return db.query("colores",
                new String[]{KEY_ID, KEY_NOMBRE, KEY_ACIERTO, KEY_IMAGEN, KEY_NIVEL},
                KEY_NOMBRE + "=?",
                new String[]{nombre},
                null, null, null);
    }

    /**
     * Convertir Cursor a lista (opcional, útil para UI)
     */
    public List<String> getAllColorNames() {
        List<String> colores = new ArrayList<>();
        Cursor cursor = fetchAllColors();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                colores.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOMBRE)));
            }
            cursor.close();
        }

        return colores;
    }
}