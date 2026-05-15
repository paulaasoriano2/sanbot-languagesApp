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
            + "subCategoria text,"
            + "nombre       text    unique,"
            + "imagen       text    not null,"
            + "nivel        integer not null"
            + "); ";

    /**
     * Database creation sql statement ACCIONES --> VOCABULARIO
     * Ejemplo: categoria (comida), nombre (apple), imagen (imagen de manzana), nivel (1)
     */
    private static final String PICTOGRAMAS = "create table pictogramas ("
            + "_id          integer primary key autoincrement,"
            + "categoria    text    not null,"
            + "nombre       text    unique,"
            + "imagen       text    not null,"
            + "nivel        integer not null"
            + "); ";

    private static final String COLORES = "create table colores ("
            + "_id          integer primary key autoincrement,"
            + "nombre       text    unique,"
            + "acierto      boolean     not null,"
            + "imagen       text    not null,"
            + "nivel        integer not null"
            + "); ";


    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VOCABULARIO);
        db.execSQL(PICTOGRAMAS);
        db.execSQL(COLORES);

        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('comida', 'frutas', 'apple','apple',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('comida','frutas', 'banana','banana',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('comida','frutas', 'watermelon','watermelon',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('comida','frutas', 'strawberry','strawberry',1)");

        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('comida','carbohidratos','bread','bread',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('comida','carbohidratos','hamburger','hamburger',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('comida','carbohidratos','rice','rice',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('comida','carbohidratos','pizza','pizza',1)");

        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('animales','', 'dog','dog',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('animales','', 'rabbit','rabbit',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('animales','', 'horse','horse',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('animales', '', 'cat','cat',1)");

        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('comida','frutas', 'orange','orange',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('comida','frutas', 'cherries','cherries',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('comida','frutas', 'pear','pear',1)");
        db.execSQL("INSERT INTO vocabulario (categoria,subCategoria,nombre,imagen,nivel) VALUES ('comida','frutas', 'shoes','shoes',1)");

        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('deporte','Sport', 'deporte',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('desayunar','Have breakfast', 'desayunar',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('dormir','Sleep', 'dormir',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('duchar','Shower', 'duchar',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('escribir','Write', 'escribir',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('estudiar','Study', 'estudiar',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('iralacompra','Go shopping', 'iralacompra',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('iralcolegio','Go to school', 'iralcolegio',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('jugaralapelota','Play ball', 'jugaralapelota',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('leer','Read', 'leer',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('trabajarenelordenador','Work on the computer', 'trabajarenelordenador',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('vamonosacasa','Let''s go home', 'vamonosacasa',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('verlatele','Watch TV', 'verlatele',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('vestir','Dress', 'vestir',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('cenar','Have dinner', 'cenar',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('cocinar','Cook', 'cocinar',1)");
        db.execSQL("INSERT INTO pictogramas (categoria,nombre,imagen,nivel) VALUES ('comer','Eat', 'comer',1)");

        db.execSQL("INSERT INTO colores (nombre,acierto,imagen,nivel) VALUES ('red',1, 'imgred',1)");
        db.execSQL("INSERT INTO colores (nombre,acierto,imagen,nivel) VALUES ('blue',0, 'imgblue',1)");
        db.execSQL("INSERT INTO colores (nombre,acierto,imagen,nivel) VALUES ('green',0, 'imggreen',1)");
        db.execSQL("INSERT INTO colores (nombre,acierto,imagen,nivel) VALUES ('purple',0, 'imgpurple',1)");
        db.execSQL("INSERT INTO colores (nombre,acierto,imagen,nivel) VALUES ('black',0, 'imgblack',1)");
        db.execSQL("INSERT INTO colores (nombre,acierto,imagen,nivel) VALUES ('pink',0, 'imgpink',1)");
        db.execSQL("INSERT INTO colores (nombre,acierto,imagen,nivel) VALUES ('grey',0, 'imggrey',1)");
        db.execSQL("INSERT INTO colores (nombre,acierto,imagen,nivel) VALUES ('white',0, 'imgwhite',1)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS vocabulario");
        db.execSQL("DROP TABLE IF EXISTS pictogramas");
        db.execSQL("DROP TABLE IF EXISTS colores");


        onCreate(db);
    }
}
