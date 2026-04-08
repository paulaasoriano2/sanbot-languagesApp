package com.example.sanbotapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *
 */
class DatabaseHelper extends SQLiteOpenHelper {

    private static final int    DATABASE_VERSION = 1;
    private static final String DATABASE_NAME    = "data";
    private static final String TAG              = "DatabaseHelper";

    /**
     * Database creation sql statement ACCIONES --> VOCABULARIO
     * Ejemplo: categoria (comida), nombre (apple), imagen (imagen de manzana), nivel (1)
     */
    private static final String VOCABULARIO = "create table vocabulario ("
            + "_id          integer primary key autoincrement,"
            + "categoria    text    not null,"
            + "nombre       text    unique,"
            + "imagen       text    not null,"
            + "nivel        integer not null"
            + "); ";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VOCABULARIO);

        db.execSQL("INSERT INTO vocabulario (categoria,nombre,imagen,nivel) VALUES ('comida','watermelon','watermelon',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,nombre,imagen,nivel) VALUES ('comida','bread','bread',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,nombre,imagen,nivel) VALUES ('comida','hamburger','hamburger',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,nombre,imagen,nivel) VALUES ('comida','rice','rice',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,nombre,imagen,nivel) VALUES ('comida','pizza','pizza',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,nombre,imagen,nivel) VALUES ('comida','orange','orange',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,nombre,imagen,nivel) VALUES ('comida','cherries','cherries',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,nombre,imagen,nivel) VALUES ('comida','apple','apple',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,nombre,imagen,nivel) VALUES ('comida','pear','pear',1)");

        db.execSQL("INSERT INTO vocabulario (categoria,nombre,imagen,nivel) VALUES ('animales','dog','dog',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,nombre,imagen,nivel) VALUES ('animales','rabbit','rabbit',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,nombre,imagen,nivel) VALUES ('animales','horse','horse',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,nombre,imagen,nivel) VALUES ('animales','cat','cat',1)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS vocabulario");

        onCreate(db);
    }
}
