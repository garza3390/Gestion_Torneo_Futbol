/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.models;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa un torneo de un solo deporte (p. ej. fútbol, baloncesto, etc.).
 * Almacena el deporte, la cantidad máxima de equipos, el tiempo por partido,
 * la lista de equipos inscritos y la lista de partidos generados.
 */
public class Torneo {
    private String id;                     // Identificador único
    private Deporte deporte;               // Deporte del torneo
    private int maxEquipos;                // Máximo de equipos permitidos
    private int tiempoPorPartido;          // En minutos
    private List<Equipo> equiposInscritos; // Lista de equipos que se inscribieron
    private List<Partido> partidos;        // Lista de todos los partidos del torneo
    private LocalDateTime fechaCreacion;   // Fecha en que se creó el torneo
    private boolean llavesGeneradas;       // Indica si ya se generó el cuadro (llaves)

    /** Constructor vacío (Gson). */
    public Torneo() {
    }

    /**
     * Crea un nuevo torneo.
     * @param deporte El deporte (objeto Deporte)
     * @param maxEquipos Cantidad máxima de equipos
     * @param tiempoPorPartido Tiempo de duración por partido (minutos)
     */
    public Torneo(Deporte deporte, int maxEquipos, int tiempoPorPartido) {
        this.id = UUID.randomUUID().toString();
        this.deporte = deporte;
        this.maxEquipos = maxEquipos;
        this.tiempoPorPartido = tiempoPorPartido;
        this.equiposInscritos = new ArrayList<>();
        this.partidos = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
        this.llavesGeneradas = false;
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public void setDeporte(Deporte deporte) {
        this.deporte = deporte;
    }

    public int getMaxEquipos() {
        return maxEquipos;
    }

    public void setMaxEquipos(int maxEquipos) {
        this.maxEquipos = maxEquipos;
    }

    public int getTiempoPorPartido() {
        return tiempoPorPartido;
    }

    public void setTiempoPorPartido(int tiempoPorPartido) {
        this.tiempoPorPartido = tiempoPorPartido;
    }

    public List<Equipo> getEquiposInscritos() {
        return equiposInscritos;
    }

    public void setEquiposInscritos(List<Equipo> equiposInscritos) {
        this.equiposInscritos = equiposInscritos;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public void setPartidos(List<Partido> partidos) {
        this.partidos = partidos;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isLlavesGeneradas() {
        return llavesGeneradas;
    }

    public void setLlavesGeneradas(boolean llavesGeneradas) {
        this.llavesGeneradas = llavesGeneradas;
    }

    /**
     * Inscribe un nuevo equipo al torneo (siempre que no se supere el máximo).
     * @param equipo El objeto Equipo a inscribir.
     * @return true si pudo inscribir; false si ya se llegó al máximo.
     */
    public boolean inscribirEquipo(Equipo equipo) {
        if (equiposInscritos.size() >= maxEquipos) {
            return false;
        }
        boolean agregado = equiposInscritos.add(equipo);
        return agregado;
    }

    /**
     * Elimina un equipo inscrito antes de generar llaves.
     * @param equipo El equipo que se quiere desinscribir.
     */
    public void desinscribirEquipo(Equipo equipo) {
        equiposInscritos.remove(equipo);
    }

    /**
     * Agrega un partido a la lista de partidos del torneo.
     */
    public void agregarPartido(Partido partido) {
        partidos.add(partido);
    }

    /**
     * Método para consultar cuántos equipos faltan por inscribirse
     * para llegar al máximo definido.
     */
    public int cupoDisponible() {
        return maxEquipos - equiposInscritos.size();
    }

    @Override
    public String toString() {
        return "Torneo de " + deporte.getNombre() +
               " (Equipos: " + equiposInscritos.size() + "/" + maxEquipos + ")";
    }
}

