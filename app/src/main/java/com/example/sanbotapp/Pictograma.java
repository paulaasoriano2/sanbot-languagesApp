package com.example.sanbotapp;

import java.io.Serializable;

public class Pictograma implements Serializable {

    private String nombre;
    private String imagen;

    public Pictograma(String nombre, String imagen) {
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagen() {
        return imagen;
    }
}