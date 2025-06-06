/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa un partido entre dos equipos dentro de un torneo.
 * Contiene los identificadores de cada equipo, los goles anotados
 * y la lógica mínima para registrar resultados y desempates.
 */
public class Partido {
    private String id;               // UUID para identificar el partido
    private Equipo equipoLocal;      // Equipo 1
    private Equipo equipoVisitante;  // Equipo 2
    private int golesLocal;          // Goles anotados por equipoLocal
    private int golesVisitante;      // Goles anotados por equipoVisitante
    private int tiempoMaximo;        // Tiempo máximo en minutos definido por el torneo
    private boolean finalizado;      // Indica si el partido ya terminó
    private boolean huboDesempate;   // Indica si se resolvió por desempate
    private LocalDateTime fechaHora; // Fecha y hora en que se jugó el partido

    
    public Partido() {
    }

    /**
     * Crea un nuevo Partido, asignando a ambos equipos
     * y el tiempo máximo en minutos.
     */
    public Partido(Equipo local, Equipo visitante, int tiempoMaximo) {
        this.id = UUID.randomUUID().toString();
        this.equipoLocal = local;
        this.equipoVisitante = visitante;
        this.golesLocal = 0;
        this.golesVisitante = 0;
        this.tiempoMaximo = tiempoMaximo;
        this.finalizado = false;
        this.huboDesempate = false;
        this.fechaHora = LocalDateTime.now();
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public Equipo getEquipoLocal() {
        return equipoLocal;
    }

    public void setEquipoLocal(Equipo equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public Equipo getEquipoVisitante() {
        return equipoVisitante;
    }

    public void setEquipoVisitante(Equipo equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public int getTiempoMaximo() {
        return tiempoMaximo;
    }

    public void setTiempoMaximo(int tiempoMaximo) {
        this.tiempoMaximo = tiempoMaximo;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    public boolean isHuboDesempate() {
        return huboDesempate;
    }

    public void setHuboDesempate(boolean huboDesempate) {
        this.huboDesempate = huboDesempate;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    /**
     * Registra el resultado final del partido.
     * Si quedaron empatados, marca que se resolverá por desempate.
     */
    public void finalizarPartido(int golesLocal, int golesVisitante) {
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.finalizado = true;
        if (golesLocal == golesVisitante) {
            this.huboDesempate = true;
        }
    }

    /**
     * Determina qué equipo ganó (o devuelve null si hay empate y no se resolvió).
     * Si huboDesempate = true, se asumirá que más adelante otro mecanismo
     * asignará un ganador.
     */
    public Equipo obtenerGanador() {
        if (!finalizado) {
            return null;
        }
        if (golesLocal > golesVisitante) {
            return equipoLocal;
        } else if (golesVisitante > golesLocal) {
            return equipoVisitante;
        } else {
            // Empate; si se hizo desempate, habría que actualizar goles con el ganador real
            return null;
        }
    }

    @Override
    public String toString() {
        String resultado = equiposToString() + " ";
        if (finalizado) {
            resultado += "(" + golesLocal + " - " + golesVisitante + ")";
            if (huboDesempate) {
                resultado += " [desempate]";
            }
        } else {
            resultado += "(pendiente)";
        }
        return resultado;
    }

    private String equiposToString() {
        String nombreLocal = (equipoLocal != null) ? equipoLocal.getNombre() : "Local";
        String nombreVisita = (equipoVisitante != null) ? equipoVisitante.getNombre() : "Visitante";
        return nombreLocal + " vs " + nombreVisita;
    }
}
