/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.models;

/**
 *
 * @author
 */

public class Deporte {
    private String nombre;
    private String imagenPath;

    public Deporte() {
    }

    public Deporte(String nombre, String imagenPath) {
        this.nombre = nombre;
        this.imagenPath = imagenPath;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagenPath() {
        return imagenPath;
    }

    public void setImagenPath(String imagenPath) {
        this.imagenPath = imagenPath;
    }

    @Override
    public String toString() {
        return nombre;
    }
}