/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.controllers;


import com.mycompany.tareaprogramada.models.*;

import java.util.List;

/**
 * Controlador dedicado a la creación de Certificados de campeonato,
 * desacoplando esa responsabilidad del TorneoController.
 */
public class CertificadoController {

    /**
     * Genera un Certificado para el equipo campeón del torneo.
     * @param t Torneo ya con partidos generados y finalizados.
     * @return Certificado o null si no hay un ganador claramente definido.
     */
    public static Certificado generarCertificado(Torneo t) {
        if (t == null || t.getPartidos().isEmpty()) {
            return null;
        }

        // Suponemos que el último partido en la lista es la final
        Partido partidoFinal = t.getPartidos().get(t.getPartidos().size() - 1);
        if (!partidoFinal.isFinalizado()) {
            return null;
        }

        // Obtenemos el ganador de la final
        com.mycompany.tareaprogramada.models.Equipo ganador = partidoFinal.obtenerGanador();
        if (ganador == null) {
            return null;
        }

        // Calculamos estadísticas (usando el controller específico)
        List<Estadistica> todasEst = EstadisticaController.calcularEstadisticas(t);
        Estadistica estCampeon = todasEst.stream()
                .filter(e -> e.getEquipo().getNombre().equals(ganador.getNombre()))
                .findFirst()
                .orElse(null);

        if (estCampeon == null) {
            return null;
        }

        return new Certificado(ganador, t, estCampeon);
    }
}
