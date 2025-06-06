// src/main/java/com/mycompany/tareaprogramada/controllers/EstadisticaViewController.java
package com.mycompany.tareaprogramada.controllers;

import com.mycompany.tareaprogramada.models.Estadistica;
import com.mycompany.tareaprogramada.models.Equipo;
import com.mycompany.tareaprogramada.models.Partido;
import com.mycompany.tareaprogramada.models.Torneo;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EstadisticasViewController implements Initializable {

    @FXML private ComboBox<Equipo> comboEquipoEstadisticas;

    // Pestaña "Torneos del Equipo"
    @FXML private TableView<Torneo> tablaTorneosEquipo;
    @FXML private TableColumn<Torneo, String> colTorneoNombre;
    @FXML private TableColumn<Torneo, Integer> colTorneoPosicion;
    @FXML private TableColumn<Torneo, Integer> colTorneoPuntos;

    @FXML private TableView<Partido> tablaPartidosEquipo;
    @FXML private TableColumn<Partido, String> colPartidoFecha;
    @FXML private TableColumn<Partido, String> colPartidoRival;
    @FXML private TableColumn<Partido, String> colPartidoResultado;

    // Pestaña "Ranking Global"
    @FXML private TableView<Map.Entry<String,Integer>> tablaRankingGlobal;
    @FXML private TableColumn<Map.Entry<String,Integer>, String> colRankingEquipo;
    @FXML private TableColumn<Map.Entry<String,Integer>, Integer> colRankingPuntos;

    private EquipoController equipoController = new EquipoController();
    private TorneoController torneoController = new TorneoController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Llenar combo de equipos
        comboEquipoEstadisticas.setItems(equipoController.getEquipos());

        // Configurar tabla de torneos del equipo
        colTorneoNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDeporte().getNombre() + 
                " (" + cell.getValue().getEquiposInscritos().size() + " equipos)"));
        colTorneoPosicion.setCellValueFactory(cell -> {
            Torneo t = cell.getValue();
            Equipo seleccionado = comboEquipoEstadisticas.getValue();
            // Calculamos posición del seleccionado en ese torneo
            List<Estadistica> ests = torneoController.calcularEstadisticas(t);
            ests.sort((e1, e2) -> Integer.compare(e2.getPuntos(), e1.getPuntos()));
            int pos = 0;
            for (int i = 0; i < ests.size(); i++) {
                if (ests.get(i).getEquipo().getNombre().equals(seleccionado.getNombre())) {
                    pos = i + 1;
                    break;
                }
            }
            return new SimpleIntegerProperty(pos).asObject();
        });
        colTorneoPuntos.setCellValueFactory(cell -> {
            Torneo t = cell.getValue();
            Equipo sel = comboEquipoEstadisticas.getValue();
            List<Estadistica> ests = torneoController.calcularEstadisticas(t);
            for (Estadistica est : ests) {
                if (est.getEquipo().getNombre().equals(sel.getNombre())) {
                    return new SimpleIntegerProperty(est.getPuntos()).asObject();
                }
            }
            return new SimpleIntegerProperty(0).asObject();
        });

        // Listener: al seleccionar un torneo en esa tabla, mostrar detalle de partidos
        tablaTorneosEquipo.getSelectionModel().selectedItemProperty().addListener((obs, oldT, nuevoT) -> {
            mostrarPartidosDelTorneo(nuevoT);
        });

        // Configurar tabla de partidos
        colPartidoFecha.setCellValueFactory(cell -> 
            new SimpleStringProperty(cell.getValue().getFechaHora().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        );
        colPartidoRival.setCellValueFactory(cell -> {
            Partido p = cell.getValue();
            Equipo sel = comboEquipoEstadisticas.getValue();
            if (p.getEquipoLocal().getNombre().equals(sel.getNombre())) {
                return new SimpleStringProperty(p.getEquipoVisitante().getNombre());
            } else {
                return new SimpleStringProperty(p.getEquipoLocal().getNombre());
            }
        });
        colPartidoResultado.setCellValueFactory(cell -> {
            Partido p = cell.getValue();
            Equipo sel = comboEquipoEstadisticas.getValue();
            int golesSel = (p.getEquipoLocal().getNombre().equals(sel.getNombre())) 
                    ? p.getGolesLocal()
                    : p.getGolesVisitante();
            int golesRival = (p.getEquipoLocal().getNombre().equals(sel.getNombre()))
                    ? p.getGolesVisitante()
                    : p.getGolesLocal();
            return new SimpleStringProperty(golesSel + " - " + golesRival);
        });

        // Configurar tabla de ranking global
        colRankingEquipo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getKey()));
        colRankingPuntos.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getValue()).asObject());

        // Cuando se elige un equipo, recargar “Torneos del equipo” y “Ranking global”
        comboEquipoEstadisticas.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                cargarTorneosDelEquipo(nuevo);
            } else {
                tablaTorneosEquipo.setItems(FXCollections.emptyObservableList());
                tablaPartidosEquipo.setItems(FXCollections.emptyObservableList());
            }
            cargarRankingGlobal();
        });

        // Inicialmente, ranking global en cero
        cargarRankingGlobal();
    }

    private void cargarTorneosDelEquipo(Equipo equipo) {
        ObservableList<Torneo> torneosEquipo = FXCollections.observableArrayList();
        for (Torneo t : torneoController.getTorneos()) {
            if (t.getEquiposInscritos().stream().anyMatch(e -> e.getNombre().equals(equipo.getNombre()))) {
                torneosEquipo.add(t);
            }
        }
        tablaTorneosEquipo.setItems(torneosEquipo);
        tablaPartidosEquipo.setItems(FXCollections.emptyObservableList());
    }

    private void mostrarPartidosDelTorneo(Torneo t) {
        if (t == null) return;
        Equipo sel = comboEquipoEstadisticas.getValue();
        ObservableList<Partido> listaPartidos = FXCollections.observableArrayList();
        for (Partido p : t.getPartidos()) {
            // Solo agregar si el equipo participó en ese partido y ya está finalizado
            if (p.isFinalizado() && 
                (p.getEquipoLocal().getNombre().equals(sel.getNombre()) ||
                 p.getEquipoVisitante().getNombre().equals(sel.getNombre()))) {
                listaPartidos.add(p);
            }
        }
        tablaPartidosEquipo.setItems(listaPartidos);
    }

    private void cargarRankingGlobal() {
        // Mapa de <Equipo, puntosTotales>
        Map<String, Integer> ranking = new HashMap<>();
        for (Torneo t : torneoController.getTorneos()) {
            List<Estadistica> ests = torneoController.calcularEstadisticas(t);
            for (Estadistica est : ests) {
                ranking.merge(est.getEquipo().getNombre(), est.getPuntos(), Integer::sum);
            }
        }
        // Convertir a lista ordenada
        List<Map.Entry<String,Integer>> lista = new ArrayList<>(ranking.entrySet());
        lista.sort((a,b) -> b.getValue().compareTo(a.getValue()));
        tablaRankingGlobal.setItems(FXCollections.observableArrayList(lista));
    }
}
