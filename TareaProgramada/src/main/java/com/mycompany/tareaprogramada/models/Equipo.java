/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.models;


public class Equipo {
    private String nombre;
    private String fotoPath;
    private Deporte deporte;

    
    public Equipo() {
    }

    public Equipo(String nombre, String fotoPath, Deporte deporte) {
        this.nombre = nombre;
        this.fotoPath = fotoPath;
        this.deporte = deporte;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public void setDeporte(Deporte deporte) {
        this.deporte = deporte;
    }

    @Override
    public String toString() {
        return nombre;
    }
}