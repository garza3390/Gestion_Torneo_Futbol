/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.controllers;



import com.mycompany.tareaprogramada.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador dedicado a la lógica de cálculo de estadísticas,
 * de forma independiente a la persistencia de torneos. 
 */
public class EstadisticaController {

    /**
     * Calcula y devuelve una lista de Estadística para cada equipo en el torneo.
     * - Recorre todos los partidos finalizados
     * - Suma goles, victorias, empates, derrotas y calcula puntos
     */
    public static List<Estadistica> calcularEstadisticas(Torneo t) {
        Map<String, Estadistica> mapaEst = new HashMap<>();
        for (Equipo e : t.getEquiposInscritos()) {
            mapaEst.put(e.getNombre(), new Estadistica(e));
        }

        for (Partido p : t.getPartidos()) {
            if (!p.isFinalizado()) continue;
            Equipo local = p.getEquipoLocal();
            Equipo visitante = p.getEquipoVisitante();
            int gl = p.getGolesLocal();
            int gv = p.getGolesVisitante();
            boolean victoriaDirectaLocal = gl > gv && !p.isHuboDesempate();
            boolean victoriaDirectaVisit = gv > gl && !p.isHuboDesempate();
            boolean victoriaDesempateLocal = gl > gv && p.isHuboDesempate();
            boolean victoriaDesempateVisit = gv > gl && p.isHuboDesempate();

            Estadistica estLocal = mapaEst.get(local.getNombre());
            Estadistica estVisit = mapaEst.get(visitante.getNombre());

            if (estLocal != null && estVisit != null) {
                estLocal.registrarPartido(gl, gv, victoriaDirectaLocal, victoriaDesempateLocal);
                estVisit.registrarPartido(gv, gl, victoriaDirectaVisit, victoriaDesempateVisit);
            }
        }

        return new ArrayList<>(mapaEst.values());
    }
}
