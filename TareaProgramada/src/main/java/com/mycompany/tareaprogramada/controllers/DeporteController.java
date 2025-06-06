/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.controllers;



import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.mycompany.tareaprogramada.models.Deporte;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Controlador de persistencia y negocio para la entidad Deporte.
 * Cada Deporte almacena:
 *   - nombre: String
 *   - imagenPath: String (ruta absoluta o relativa donde está el archivo de imagen)
 *
 * Este controlador:
 *   • Carga desde "deportes.json" (si existe) al iniciar.
 *   • Guarda en "deportes.json" cada vez que se agrega o elimina un Deporte.
 *   • Expone la lista observable para que la UI pueda enlazarla a una TableView o ComboBox.
 */
public class DeporteController {
    private static final String ARCHIVO_JSON = "deportes.json";
    private final Gson gson = new Gson();
    private ObservableList<Deporte> deportes;

    public DeporteController() {
        File archivo = new File(ARCHIVO_JSON);
        if (archivo.exists()) {
            // Si ya existe el JSON, lo leemos y construimos la lista Observable
            try (FileReader reader = new FileReader(archivo)) {
                Type tipoLista = new TypeToken<List<Deporte>>() {}.getType();
                List<Deporte> lista = gson.fromJson(reader, tipoLista);
                deportes = FXCollections.observableArrayList(lista);
            } catch (Exception e) {
                e.printStackTrace();
                deportes = FXCollections.observableArrayList();
            }
        } else {
            // Si no existe, iniciamos una lista vacía
            deportes = FXCollections.observableArrayList();
        }
    }

    /**
     * Devuelve la lista observable de deportes.
     *   • En la UI, por ejemplo, se puede hacer tableView.setItems(deporteController.getDeportes()).
     */
    public ObservableList<Deporte> getDeportes() {
        return deportes;
    }

    /**
     * Agrega un nuevo Deporte a la lista y persiste inmediatamente en el JSON.
     *   • Espera que el objeto Deporte ya tenga setNombre(...) y setImagenPath(...).
     */
    public void agregarDeporte(Deporte d) {
        if (d == null) return;
        deportes.add(d);
        guardarDeportes();
    }

    /**
     * Elimina un Deporte existente de la lista (si está) y actualiza el JSON.
     */
    public void eliminarDeporte(Deporte d) {
        if (d == null) return;
        deportes.remove(d);
        guardarDeportes();
    }

    /**
     * Guarda la lista completa de deportes en el archivo "deportes.json".
     *   • Usa Gson para serializar ObservableList<Deporte> directamente.
     */
    public void guardarDeportes() {
        try (FileWriter writer = new FileWriter(ARCHIVO_JSON)) {
            gson.toJson(deportes, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
