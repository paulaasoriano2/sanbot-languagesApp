package com.example.sanbotapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class VocabularioDbAdapter {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_CATEGORIA = "categoria";
    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_IMAGEN = "imagen";
    public static final String KEY_NIVEL = "nivel";

    private static final String DATABASE_TABLE = "vocabulario";
    private static final String TAG = "VocabularioDbAdapter";

    private final Context mCtx;

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public VocabularioDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public VocabularioDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    // CREAR TABLA VOCABULARIO
    public long createVocabulario(String categoria, String nombre, String imagen, int nivel) {
        long result = -1;

        try {
            if (categoria == null || categoria.isEmpty()) return -1;
            if (nombre == null || nombre.isEmpty()) return -1;
            if (imagen == null || imagen.isEmpty()) return -1;
            if (nivel < 0) return -1;

            ContentValues values = new ContentValues();
            values.put(KEY_CATEGORIA, categoria);
            values.put(KEY_NOMBRE, nombre);
            values.put(KEY_IMAGEN, imagen);
            values.put(KEY_NIVEL, nivel);

            result = mDb.insert(DATABASE_TABLE, null, values);

        } catch (Exception e) {
            Log.e(TAG, "Error al crear vocabulario", e);
        }

        return result;
    }

    // BORRAR VOCABULARIO POR ID
    public boolean deleteVocabulario(long rowId) {
        try {
            if (rowId < 1) return false;

            return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;

        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar vocabulario", e);
            return false;
        }
    }

    public void deleteAllVocabulario() {
        mDb.delete(DATABASE_TABLE, null, null);
    }

    // LEER EL VOCABULARIO COMPLETO
    public Cursor fetchAllVocabulario() {
        return mDb.query(
                DATABASE_TABLE,
                new String[]{KEY_ROWID, KEY_CATEGORIA, KEY_NOMBRE, KEY_IMAGEN, KEY_NIVEL},
                null,
                null,
                null,
                null,
                KEY_NOMBRE
        );
    }

    // LEER VOCABULARIO POR ID
    public Cursor fetchVocabulario(long rowId) throws SQLException {
        Cursor cursor = mDb.query(
                true,
                DATABASE_TABLE,
                new String[]{KEY_ROWID, KEY_CATEGORIA, KEY_NOMBRE, KEY_IMAGEN, KEY_NIVEL},
                KEY_ROWID + "=" + rowId,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    // 🔹 UPDATE
    public boolean updateVocabulario(long rowId, String categoria, String nombre, String imagen, int nivel) {
        boolean result = true;

        try {
            if (rowId < 1) result = false;
            if (categoria == null || categoria.isEmpty()) result = false;
            if (nombre == null || nombre.isEmpty()) result = false;
            if (imagen == null || imagen.isEmpty()) result = false;
            if (nivel < 0) result = false;

        } catch (Exception e) {
            Log.w(TAG, e.getStackTrace().toString());
        }

        if (result) {
            ContentValues values = new ContentValues();
            values.put(KEY_CATEGORIA, categoria);
            values.put(KEY_NOMBRE, nombre);
            values.put(KEY_IMAGEN, imagen);
            values.put(KEY_NIVEL, nivel);

            result = mDb.update(
                    DATABASE_TABLE,
                    values,
                    KEY_ROWID + "=" + rowId,
                    null
            ) > 0;
        }

        return result;
    }

    // FILTRAR POR CATEGORÍA
    public Cursor fetchByCategoria(String categoria) {
        return mDb.query(
                DATABASE_TABLE,
                new String[]{KEY_ROWID, KEY_CATEGORIA, KEY_NOMBRE, KEY_IMAGEN, KEY_NIVEL},
                KEY_CATEGORIA + "=?",
                new String[]{categoria},
                null,
                null,
                KEY_NOMBRE
        );
    }

    // FILTRAR POR NIVEL
    public Cursor fetchByNivel(int nivel) {
        return mDb.query(
                DATABASE_TABLE,
                new String[]{KEY_ROWID, KEY_CATEGORIA, KEY_NOMBRE, KEY_IMAGEN, KEY_NIVEL},
                KEY_NIVEL + "=?",
                new String[]{String.valueOf(nivel)},
                null,
                null,
                KEY_NOMBRE
        );
    }

    // LISTA DE NOMBRES DEL VOCABULARIO (tipo DataModel)
    public ArrayList<String> getNombresVocabulario() {
        ArrayList<String> lista = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = mDb.query(DATABASE_TABLE,
                    new String[]{KEY_NOMBRE},
                    null, null, null, null, KEY_NOMBRE);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    lista.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener nombres", e);
        } finally {
            if (cursor != null) cursor.close();
        }

        return lista;
    }

    // LISTA DE IMÁGENES POR CATEGORÍA
    public ArrayList<String> getImagenesPorCategoria(String categoria) {
        ArrayList<String> lista = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = mDb.query(
                    DATABASE_TABLE,
                    new String[]{KEY_IMAGEN},
                    KEY_CATEGORIA + "=?",
                    new String[]{categoria},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    lista.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener imágenes por categoría", e);
        } finally {
            if (cursor != null) cursor.close();
        }

        return lista;
    }

    // OBTENER EL NOMBRE DE UNA IMAGEN
    public String getNombrePorImagen(String imagen) {
        String nombre = "";
        Cursor cursor = null;

        try {
            cursor = mDb.query(
                    DATABASE_TABLE,
                    new String[]{KEY_NOMBRE},
                    KEY_IMAGEN + "=?",
                    new String[]{imagen},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                nombre = cursor.getString(0);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener nombre por imagen", e);
        } finally {
            if (cursor != null) cursor.close();
        }

        return nombre;
    }
}
/*
EXPLICACIÓN DE LOS PARÁMETROS DE LA FUNCIÓN query():

mDb.query(
    tabla,
    columnas,
    where,
    whereArgs,
    groupBy,
    having,
    orderBy
);

*/

/*
COMO USARLO:

VocabularioDbAdapter db = new VocabularioDbAdapter(this);

db.open();

// Insertar
db.createVocabulario("comida", "manzana", "img_manzana", 1);

// Leer
Cursor cursor = db.fetchAllVocabulario();

if (cursor.moveToFirst()) {
    do {
        String nombre = cursor.getString(2);
        Log.d("DB", nombre);
    } while (cursor.moveToNext());
}

cursor.close();
db.close();

*/