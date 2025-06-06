/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.models;

/**
 * Representa los datos que se imprimirán en un certificado para el equipo campeón.
 * Contiene información de contexto: nombre del equipo, foto, datos del torneo y rendimiento.
 */
public class Certificado {
    private Equipo equipoCampeon;
    private Torneo torneo;
    private Estadistica estadisticaCampeon;

  
    public Certificado() {
    }

    public Certificado(Equipo equipoCampeon, Torneo torneo, Estadistica estadisticaCampeon) {
        this.equipoCampeon = equipoCampeon;
        this.torneo = torneo;
        this.estadisticaCampeon = estadisticaCampeon;
    }

    public Equipo getEquipoCampeon() {
        return equipoCampeon;
    }

    public void setEquipoCampeon(Equipo equipoCampeon) {
        this.equipoCampeon = equipoCampeon;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public Estadistica getEstadisticaCampeon() {
        return estadisticaCampeon;
    }

    public void setEstadisticaCampeon(Estadistica estadisticaCampeon) {
        this.estadisticaCampeon = estadisticaCampeon;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Certificado de Campeón\n");
        sb.append("Equipo: ").append(equipoCampeon.getNombre()).append("\n");
        sb.append("Torneo: ").append(torneo.getDeporte().getNombre()).append(" (")
          .append(torneo.getEquiposInscritos().size()).append(" equipos)").append("\n");
        sb.append("Puntos totales: ").append(estadisticaCampeon.getPuntos()).append("\n");
        sb.append("Goles a favor: ").append(estadisticaCampeon.getGolesAFavor())
          .append(", Goles en contra: ").append(estadisticaCampeon.getGolesEnContra()).append("\n");
        sb.append("Victorias directas: ").append(estadisticaCampeon.getVictoriasDirectas())
          .append(", Victorias por desempate: ").append(estadisticaCampeon.getVictoriasPorDesempate()).append("\n");
        return sb.toString();
    }
}
