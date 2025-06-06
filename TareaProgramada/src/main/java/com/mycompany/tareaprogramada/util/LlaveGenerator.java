/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.util;

import com.mycompany.tareaprogramada.models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generador de llaves: a partir de los equipos inscritos en el torneo,
 * crea un listado de partidos en orden aleatorio o predefinido.
 */
public class LlaveGenerator {

    /**
     * Genera la primera ronda de partidos (ronda de octavos, cuartos, etc.)
     * emparejando los equipos según su posición en la lista.
     * Por ejemplo, si hay 8 equipos: 
     * se emparejan 0 vs 7, 1 vs 6, 2 vs 5 y 3 vs 4.
     * 
     * @param torneo El torneo donde están los equipos y el tiempoPorPartido.
     */
    public static void generarLlavesIniciales(Torneo torneo) {
        List<Equipo> listaEquipos = new ArrayList<>(torneo.getEquiposInscritos());
        // Barajar la lista para que los emparejamientos sean aleatorios
        Collections.shuffle(listaEquipos);

        int total = listaEquipos.size();
        for (int i = 0; i < total / 2; i++) {
            Equipo e1 = listaEquipos.get(i);
            Equipo e2 = listaEquipos.get(total - 1 - i);
            Partido p = new Partido(e1, e2, torneo.getTiempoPorPartido());
            torneo.agregarPartido(p);
        }
        torneo.setLlavesGeneradas(true);
    }
}
