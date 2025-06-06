/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.controllers;



import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.mycompany.tareaprogramada.models.Equipo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Controlador de persistencia y negocio para la entidad Equipo.
 * Cada Equipo almacena:
 *   - nombre: String
 *   - fotoPath: String (ruta absoluta o relativa del archivo de imagen del equipo)
 *   - deporte: Deporte (objeto que referencia al deporte del equipo)
 *
 * Este controlador:
 *   • Carga desde "equipos.json" (si existe) al iniciar.
 *   • Guarda en "equipos.json" cada vez que se agrega o elimina un Equipo.
 *   • Expone la lista observable para la UI (TableView o ComboBox, etc.).
 */
public class EquipoController {
    private static final String ARCHIVO_JSON = "equipos.json";
    private final Gson gson = new Gson();
    private ObservableList<Equipo> equipos;

    public EquipoController() {
        File archivo = new File(ARCHIVO_JSON);
        if (archivo.exists()) {
            // Deserializa la lista existente
            try (FileReader reader = new FileReader(archivo)) {
                Type tipoLista = new TypeToken<List<Equipo>>() {}.getType();
                List<Equipo> lista = gson.fromJson(reader, tipoLista);
                equipos = FXCollections.observableArrayList(lista);
            } catch (Exception e) {
                e.printStackTrace();
                equipos = FXCollections.observableArrayList();
            }
        } else {
            equipos = FXCollections.observableArrayList();
        }
    }

    /**
     * Devuelve la lista observable de equipos.
     *   • En la UI, se puede hacer tableView.setItems(equipoController.getEquipos()).
     */
    public ObservableList<Equipo> getEquipos() {
        return equipos;
    }

    /**
     * Agrega un nuevo Equipo y guarda inmediatamente.
     *   • El objeto Equipo debe tener previamente setNombre(...), setFotoPath(...) y setDeporte(...).
     */
    public void agregarEquipo(Equipo e) {
        if (e == null) return;
        equipos.add(e);
        guardarEquipos();
    }

    /**
     * Elimina un Equipo existente (si está) y persiste la lista actualizada.
     */
    public void eliminarEquipo(Equipo e) {
        if (e == null) return;
        equipos.remove(e);
        guardarEquipos();
    }

    /**
     * Guarda la lista completa en "equipos.json" usando Gson.
     */
    public void guardarEquipos() {
        try (FileWriter writer = new FileWriter(ARCHIVO_JSON)) {
            gson.toJson(equipos, writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
