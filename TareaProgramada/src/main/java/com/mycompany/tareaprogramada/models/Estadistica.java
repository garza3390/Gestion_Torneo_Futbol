/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.models;

/**
 * Representa las estadísticas de un equipo en un torneo específico.
 * Calcula puntos, goles a favor, en contra, victorias directas/por desempate, etc.
 */
public class Estadistica {
    private Equipo equipo;
    private int partidosJugados;
    private int golesAFavor;
    private int golesEnContra;
    private int victoriasDirectas;
    private int victoriasPorDesempate;
    private int empates;
    private int derrotas;
    private int puntos;

   
    public Estadistica() {
    }

    public Estadistica(Equipo equipo) {
        this.equipo = equipo;
        this.partidosJugados = 0;
        this.golesAFavor = 0;
        this.golesEnContra = 0;
        this.victoriasDirectas = 0;
        this.victoriasPorDesempate = 0;
        this.empates = 0;
        this.derrotas = 0;
        this.puntos = 0;
    }

    // Getters y setters

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public int getPartidosJugados() {
        return partidosJugados;
    }

    public int getGolesAFavor() {
        return golesAFavor;
    }

    public int getGolesEnContra() {
        return golesEnContra;
    }

    public int getVictoriasDirectas() {
        return victoriasDirectas;
    }

    public int getVictoriasPorDesempate() {
        return victoriasPorDesempate;
    }

    public int getEmpates() {
        return empates;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public int getPuntos() {
        return puntos;
    }

    /**
     * Actualiza las estadísticas tras un partido jugado.
     * @param golesFavor Goles anotados por este equipo en el partido.
     * @param golesContra Goles recibidos por este equipo en el partido.
     * @param fueVictoriaDirecta true si ganó sin haber empatado en tiempo regular.
     * @param fueVictoriaPorDesempate true si ganó en la ronda de desempate.
     */
    public void registrarPartido(int golesFavor, int golesContra,
                                 boolean fueVictoriaDirecta,
                                 boolean fueVictoriaPorDesempate) {
        partidosJugados++;
        golesAFavor += golesFavor;
        golesEnContra += golesContra;

        if (fueVictoriaDirecta) {
            victoriasDirectas++;
            puntos += 3;   // Ejemplo: 3 puntos por victoria directa
        } else if (fueVictoriaPorDesempate) {
            victoriasPorDesempate++;
            puntos += 2;   // Ejemplo: 2 puntos por ganar en desempate
        } else if (golesFavor == golesContra) {
            empates++;
            puntos += 1;   // 1 punto por empate (en caso de que se permita)
        } else {
            derrotas++;
            // 0 puntos por derrota
        }
    }

    /**
     * Fórmula simple para el cálculo de la diferencia de goles.
     */
    public int diferenciaDeGol() {
        return golesAFavor - golesEnContra;
    }

    @Override
    public String toString() {
        return equipo.getNombre() + ": PJ=" + partidosJugados +
               ", GF=" + golesAFavor +
               ", GC=" + golesEnContra +
               ", Pts=" + puntos;
    }
}

