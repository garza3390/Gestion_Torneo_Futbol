/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.controllers;

import com.mycompany.tareaprogramada.models.*;
import com.mycompany.tareaprogramada.util.CertificadoPDFGenerator;
import com.mycompany.tareaprogramada.util.LlaveGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Controlador de TorneoView.fxml: - Lista de torneos existentes. - Crear /
 * Eliminar torneos. - Mostrar detalles del torneo seleccionado. - Inscribir /
 * Desinscribir equipos. - Generar llaves iniciales. - Listar partidos de la
 * fase actual (con botón “Iniciar partido”). - “Siguiente Ronda” cuando todos
 * los partidos finalizados. - “Imprimir Certificado” cuando el torneo termina.
 */
public class TorneoViewController implements Initializable {

    // ---------------------------
    // Campos del TOP (Crear/Eliminar torneo)
    @FXML
    private ComboBox<Deporte> comboDeporteTorneo;
    @FXML
    private TextField campoMaxEquipos;
    @FXML
    private TextField campoTiempoPartido;
    @FXML
    private Button btnCrearTorneo;
    @FXML
    private Button btnEliminarTorneo;

    // ---------------------------
    // Tabla de torneos en LEFT
    @FXML
    private TableView<Torneo> tablaTorneos;
    @FXML
    private TableColumn<Torneo, String> colDeporteTorneo;
    @FXML
    private TableColumn<Torneo, String> colEquiposTorneo;

    // ---------------------------
    // Detalles del torneo seleccionado en CENTER (labels + inscripción)
    @FXML
    private Label lblDeporteSeleccionado;
    @FXML
    private Label lblMaxEquiposSeleccionado;
    @FXML
    private Label lblTiempoSeleccionado;
    @FXML
    private ComboBox<Equipo> comboEquiposDisponibles;
    @FXML
    private Button btnInscribirEquipo;

    // ---------------------------
    // Tabla de equipos inscritos
    @FXML
    private TableView<Equipo> tablaEquiposInscritos;
    @FXML
    private TableColumn<Equipo, String> colNombreInscrito;
    @FXML
    private TableColumn<Equipo, String> colDeporteInscrito;
    @FXML
    private TableColumn<Equipo, String> colAccionInscrito;

    // ---------------------------
    // Botón Generar llaves
    @FXML
    private Button btnGenerarLlaves;

    // ---------------------------
    // Tabla de partidos en fase actual
    @FXML
    private TableView<Partido> tablaPartidos;
    @FXML
    private TableColumn<Partido, String> colLocal;
    @FXML
    private TableColumn<Partido, String> colVisitante;
    @FXML
    private TableColumn<Partido, String> colEstado;
    @FXML
    private TableColumn<Partido, String> colAccionPartido;

    // ---------------------------
    // Botones en BOTTOM
    @FXML
    private Button btnSiguienteRonda;
    @FXML
    private Button btnImprimirCertificado;

    // Controladores de negocio
    private DeporteController deporteController = new DeporteController();
    private EquipoController equipoController = new EquipoController();
    private TorneoController torneoController = new TorneoController();

    // Torneo actualmente seleccionado
    private Torneo torneoSeleccionado = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1) Poblamos comboDeporteTorneo con todos los deportes
        comboDeporteTorneo.setItems(deporteController.getDeportes());

        // 2) Configuramos la tabla de torneos
        colDeporteTorneo.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getDeporte().getNombre())
        );
        colEquiposTorneo.setCellValueFactory(cellData -> {
            Torneo t = cellData.getValue();
            String texto = t.getEquiposInscritos().size() + " / " + t.getMaxEquipos();
            return new SimpleStringProperty(texto);
        });
        tablaTorneos.setItems(torneoController.getTorneos());

        // Listener: cuando el usuario selecciona un torneo, mostramos sus detalles
        tablaTorneos.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            torneoSeleccionado = nuevo;
            mostrarDetallesTorneo();
        });

        // 3) Configuramos comboEquiposDisponibles con la lista global de equipos
        comboEquiposDisponibles.setItems(equipoController.getEquipos());

        // 4) Configuramos tabla de equipos inscritos
        colNombreInscrito.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDeporteInscrito.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getDeporte().getNombre())
        );
        // En colAccionInscrito pondremos un botón “Desinscribir” en cada fila
        colAccionInscrito.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Desinscribir");

            {
                btn.setStyle("-fx-background-color:#E53935; -fx-text-fill:white;");
                btn.setOnAction(e -> {
                    Equipo eq = getTableView().getItems().get(getIndex());
                    desinscribirEquipo(eq);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        // 5) Configuramos tabla de partidos (fase actual)
        colLocal.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getEquipoLocal().getNombre())
        );
        colVisitante.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getEquipoVisitante().getNombre())
        );
        colEstado.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().isFinalizado() ? "Finalizado" : "Pendiente")
        );
        // En colAccionPartido ponemos un botón “Iniciar partido” si no está finalizado
        colAccionPartido.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Iniciar partido");

            {
                btn.setStyle("-fx-background-color:#3F51B5; -fx-text-fill:white;");
                btn.setOnAction(e -> {
                    Partido p = getTableView().getItems().get(getIndex());
                    abrirVentanaPartido(p);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Partido p = getTableView().getItems().get(getIndex());
                    if (p.isFinalizado()) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn);
                    }
                }
            }
        });

        // 6) Inicialmente, deshabilitamos botones que dependen de selección
        btnInscribirEquipo.setDisable(true);
        btnEliminarTorneo.setDisable(true);
        btnGenerarLlaves.setDisable(true);
        btnSiguienteRonda.setDisable(true);
        btnImprimirCertificado.setDisable(true);
        comboEquiposDisponibles.setDisable(true);

        // Listener: cuando seleccione un equipo del combo para inscribir, habilitamos el botón
        comboEquiposDisponibles.getSelectionModel().selectedItemProperty().addListener((obs, v, nuevo) -> {
            btnInscribirEquipo.setDisable(nuevo == null || torneoSeleccionado == null);
        });
    }

    // ---------------------------
    // Mostrar detalles del torneo seleccionado en el área central
    private void mostrarDetallesTorneo() {
        if (torneoSeleccionado == null) {
            lblDeporteSeleccionado.setText("-");
            lblMaxEquiposSeleccionado.setText("-");
            lblTiempoSeleccionado.setText("-");
            tablaEquiposInscritos.setItems(FXCollections.emptyObservableList());
            tablaPartidos.setItems(FXCollections.emptyObservableList());
            btnEliminarTorneo.setDisable(true);
            comboEquiposDisponibles.setDisable(true);
            btnGenerarLlaves.setDisable(true);
            btnSiguienteRonda.setDisable(true);
            btnImprimirCertificado.setDisable(true);
            return;
        }

        // Llenar labels con datos del torneo
        lblDeporteSeleccionado.setText(torneoSeleccionado.getDeporte().getNombre());
        lblMaxEquiposSeleccionado.setText(String.valueOf(torneoSeleccionado.getMaxEquipos()));
        lblTiempoSeleccionado.setText(String.valueOf(torneoSeleccionado.getTiempoPorPartido()) + " min");

        // Cargar lista de equipos inscritos en la tabla
        ObservableList<Equipo> inscritos = FXCollections.observableArrayList(torneoSeleccionado.getEquiposInscritos());
        tablaEquiposInscritos.setItems(inscritos);

        // Cargar lista de partidos: si ya generó llaves, mostrar partidos; si no, lista vacía
        if (torneoSeleccionado.isLlavesGeneradas()) {
            ObservableList<Partido> partidos = FXCollections.observableArrayList(torneoSeleccionado.getPartidos());
            tablaPartidos.setItems(partidos);
        } else {
            tablaPartidos.setItems(FXCollections.emptyObservableList());
        }

        // Habilitar botones
        btnEliminarTorneo.setDisable(false);
        comboEquiposDisponibles.setDisable(false);

        // “Generar llaves” solo si no se han generado y hay al menos 2 equipos inscritos
        boolean puedeGenerar = !torneoSeleccionado.isLlavesGeneradas()
                && torneoSeleccionado.getEquiposInscritos().size() >= 2;
        btnGenerarLlaves.setDisable(!puedeGenerar);

        // “Siguiente Ronda” sólo si ya generó llaves y todos los partidos de la fase están finalizados
        actualizarBotonSiguienteRonda();

        // “Imprimir Certificado” sólo si el torneo terminó (un solo partido finalizado)
        boolean torneoTerminado = torneoSeleccionado.isLlavesGeneradas()
                && torneoSeleccionado.getPartidos().size() == 1
                && torneoSeleccionado.getPartidos().get(0).isFinalizado();
        btnImprimirCertificado.setDisable(!torneoTerminado);
    }

    // ---------------------------
    // Evento: Crear nuevo torneo
    @FXML
    void onCrearTorneo(ActionEvent event) {
        Deporte deporte = comboDeporteTorneo.getValue();
        String maxEqText = campoMaxEquipos.getText().trim();
        String tiempoText = campoTiempoPartido.getText().trim();

        if (deporte == null || maxEqText.isEmpty() || tiempoText.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Complete todos los campos.").showAndWait();
            return;
        }

        int maxEq, tiempo;
        try {
            maxEq = Integer.parseInt(maxEqText);
            tiempo = Integer.parseInt(tiempoText);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Los campos 'Máx. equipos' y 'Tiempo' deben ser números.").showAndWait();
            return;
        }
        if (maxEq < 2) {
            new Alert(Alert.AlertType.WARNING, "Debe haber al menos 2 equipos.").showAndWait();
            return;
        }
        if (tiempo <= 0) {
            new Alert(Alert.AlertType.WARNING, "El tiempo debe ser mayor que 0.").showAndWait();
            return;
        }

        Torneo nuevo = new Torneo(deporte, maxEq, tiempo);
        torneoController.crearTorneo(nuevo);

        // Limpiar formulario
        comboDeporteTorneo.getSelectionModel().clearSelection();
        campoMaxEquipos.clear();
        campoTiempoPartido.clear();
    }

    // ---------------------------
    // Evento: Eliminar torneo seleccionado
    @FXML
    void onEliminarTorneo(ActionEvent event) {
        if (torneoSeleccionado == null) {
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar torneo de " + torneoSeleccionado.getDeporte().getNombre() + "?",
                ButtonType.YES, ButtonType.NO);
        conf.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                torneoController.eliminarTorneo(torneoSeleccionado);
                torneoSeleccionado = null;
                mostrarDetallesTorneo();
            }
        });
    }

    // ---------------------------
    // Evento: Inscribir equipo en torneo seleccionado
    @FXML
    void onInscribirEquipo(ActionEvent event) {
        if (torneoSeleccionado == null) {
            return;
        }
        Equipo eq = comboEquiposDisponibles.getValue();
        if (eq == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione un equipo.").showAndWait();
            return;
        }
        boolean ok = torneoController.inscribirEquipoEnTorneo(torneoSeleccionado, eq);
        if (!ok) {
            new Alert(Alert.AlertType.WARNING, "No hay más cupo o ya está inscrito.").showAndWait();
        }
        mostrarDetallesTorneo();
    }

    // ---------------------------
    // Desinscribir equipo (botón en tabla de inscritos)
    private void desinscribirEquipo(Equipo eq) {
        if (torneoSeleccionado == null || eq == null) {
            return;
        }
        torneoController.desinscribirEquipoDeTorneo(torneoSeleccionado, eq);
        mostrarDetallesTorneo();
    }

    // ---------------------------
    // Evento: Generar llaves iniciales
    @FXML
    void onGenerarLlaves(ActionEvent event) {
        if (torneoSeleccionado == null) {
            return;
        }
        torneoController.generarLlavesTorneo(torneoSeleccionado);
        mostrarDetallesTorneo();
    }

    // ---------------------------
    // Abre la ventana de PartidoView para el partido seleccionado
    private void abrirVentanaPartido(Partido p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PartidoView.fxml"));
            Parent root = loader.load();
            PartidoViewController ctrl = loader.getController();
            ctrl.initData(torneoSeleccionado, p.getId());
            Stage ventana = new Stage();
            ventana.setTitle("Partido: " + p.getEquipoLocal().getNombre() + " vs " + p.getEquipoVisitante().getNombre());
            ventana.setScene(new Scene(root));
            ventana.show();
            // Cuando se cierre PartidoView, refrescamos la tabla de partidos
            ventana.setOnHiding(e -> mostrarDetallesTorneo());
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir la ventana de Partido").showAndWait();
        }
    }

    // ---------------------------
    // Actualiza el estado del botón “Siguiente Ronda”
    private void actualizarBotonSiguienteRonda() {
        if (torneoSeleccionado == null) {
            btnSiguienteRonda.setDisable(true);
            return;
        }
        if (!torneoSeleccionado.isLlavesGeneradas()) {
            btnSiguienteRonda.setDisable(true);
            return;
        }
        // Verificar si todos los partidos de la fase actual están finalizados
        boolean todosFinalizados = true;
        for (Partido p : torneoSeleccionado.getPartidos()) {
            if (!p.isFinalizado()) {
                todosFinalizados = false;
                break;
            }
        }
        // Solo habilitar si hubo llaves generadas y hay más de 1 partido finalizado
        // En vez de exigir size >= 2, permitimos size >= 1 (para que la final también active el botón, si todos sus partidos están listos)
        btnSiguienteRonda.setDisable(!(todosFinalizados && torneoSeleccionado.getPartidos().size() >= 1));

    }

    // ---------------------------
    // Evento: Siguiente Ronda
    @FXML
    void onSiguienteRonda(ActionEvent event) {
        if (torneoSeleccionado == null) {
            return;
        }
        torneoController.generarSiguienteRonda(torneoSeleccionado);
        mostrarDetallesTorneo();
    }

    // ---------------------------
    // Evento: Imprimir certificado
    @FXML
    void onImprimirCertificado(ActionEvent event) {
        if (torneoSeleccionado == null) {
            return;
        }
        Certificado cert = torneoController.generarCertificado(torneoSeleccionado);
        if (cert == null) {
            new Alert(Alert.AlertType.WARNING, "No se puede generar certificado aún.").showAndWait();
            return;
        }
        try {
            CertificadoPDFGenerator.generarPDF(cert);
            new Alert(Alert.AlertType.INFORMATION, "Certificado PDF generado exitosamente.").showAndWait();
            // Opción: abrir automáticamente el PDF si se desea
            String nombreArchivo = String.format("certificado_%s_%s.pdf",
                    torneoSeleccionado.getId(),
                    cert.getEquipoCampeon().getNombre().replaceAll("\\s+", "_"));
            File filePDF = new File(nombreArchivo);
            if (filePDF.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(filePDF);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error generando el PDF: " + ex.getMessage()).showAndWait();
        }
    }
}
