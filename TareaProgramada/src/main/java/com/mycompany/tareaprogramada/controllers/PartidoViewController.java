/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.mycompany.tareaprogramada.models.*;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.scene.control.ButtonType;

/**
 * Controlador de PartidoView.fxml: - Muestra imágenes de equipo local, balón y
 * equipo visitante. - Maneja Drag & Drop: arrastrar el balón sobre cada equipo
 * para registrar un gol. - Ejecuta un timer regresivo desde tiempoMaximo hasta
 * 0. - Botón “Finalizar partido”: detiene timer, resuelve empate (penales
 * aleatorios) y registra resultado.
 */
public class PartidoViewController implements Initializable {

    @FXML
    private ImageView imgEquipoLocal;
    @FXML
    private ImageView imgBalon;
    @FXML
    private ImageView imgEquipoVisitante;

    @FXML
    private Label lblGolesLocal;
    @FXML
    private Label lblGolesVisitante;
    @FXML
    private Label lblNombreLocal;
    @FXML
    private Label lblNombreVisitante;

    @FXML
    private Label lblTimer;
    @FXML
    private Button btnFinalizar;

    private TorneoController torneoController = new TorneoController();
    private Torneo torneoActual;
    private Partido partidoActual;
    private Timeline timeline;           // Para el temporizador
    private int segundosRestantes;       // En segundos

    private int golesLocal = 0;
    private int golesVisitante = 0;

    // ---------------------------
    // Este método debe invocarse justo después de cargar el FXML:
    //   p.ej. FXMLLoader loader = new FXMLLoader(...);
    //         Parent root = loader.load();
    //         PartidoViewController ctrl = loader.getController();
    //         ctrl.initData(miTorneo, partidoId);
    // ---------------------------
    public void initData(Torneo torneo, String partidoId) {
        this.torneoActual = torneo;
        // Buscar el Partido en la lista por su ID
        this.partidoActual = torneo.getPartidos().stream()
                .filter(p -> p.getId().equals(partidoId))
                .findFirst()
                .orElse(null);

        if (partidoActual == null) {
            showErrorAndClose("No se encontró el partido con ID: " + partidoId);
            return;
        }

        // Inicializamos los elementos de UI con datos del Partido
        Equipo local = partidoActual.getEquipoLocal();
        Equipo visitante = partidoActual.getEquipoVisitante();
        Deporte deporte = torneo.getDeporte();

        // Nombres
        lblNombreLocal.setText(local.getNombre());
        lblNombreVisitante.setText(visitante.getNombre());

        // Imágenes de equipos
        cargarImagenEnView(local.getFotoPath(), imgEquipoLocal);
        cargarImagenEnView(visitante.getFotoPath(), imgEquipoVisitante);

        // Imagen del balón (tomada del Deporte)
        cargarImagenEnView(deporte.getImagenPath(), imgBalon);

        // Inicializar contadores de goles
        golesLocal = 0;
        golesVisitante = 0;
        lblGolesLocal.setText("0");
        lblGolesVisitante.setText("0");

        // Configurar Drag & Drop en la imagen del balón
        configurarDragAndDrop();

        // Inicializar y arrancar el temporizador
        segundosRestantes = partidoActual.getTiempoMaximo() * 60;
        iniciarTemporizador();

        // Configurar acción del botón Finalizar
        btnFinalizar.setOnAction(this::onFinalizarClicked);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Nada aquí; la inicialización real ocurre en initData(...)
    }

    // ---------------------------
    // Carga una imagen desde disco en un ImageView (o muestra error si no existe)
    private void cargarImagenEnView(String ruta, ImageView view) {
        File archivo = new File(ruta);
        if (archivo.exists()) {
            Image img = new Image(archivo.toURI().toString());
            view.setImage(img);
        } else {
            view.setImage(null);
        }
    }

    // ---------------------------
    // Configura Drag & Drop: arrastrar imgBalon sobre imgEquipoLocal o imgEquipoVisitante
    private void configurarDragAndDrop() {
        // Al detectar arrastre sobre la imagen del balón:
        imgBalon.setOnDragDetected(event -> {
            Dragboard db = imgBalon.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString("BALON"); // simplemente un identificador
            db.setContent(content);
            event.consume();
        });

        // Cuando el mouse entra sobre el ImageView de Equipo Local:
        imgEquipoLocal.setOnDragOver(event -> {
            if (event.getGestureSource() == imgBalon && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        imgEquipoLocal.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString() && db.getString().equals("BALON")) {
                golesLocal++;
                lblGolesLocal.setText(String.valueOf(golesLocal));
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });

        // Mismo para Equipo Visitante:
        imgEquipoVisitante.setOnDragOver(event -> {
            if (event.getGestureSource() == imgBalon && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        imgEquipoVisitante.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString() && db.getString().equals("BALON")) {
                golesVisitante++;
                lblGolesVisitante.setText(String.valueOf(golesVisitante));
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }

    // ---------------------------
    // Inicia el temporizador regresivo usando Timeline
    private void iniciarTemporizador() {
        lblTimer.setText(formatearTiempo(segundosRestantes));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            segundosRestantes--;
            lblTimer.setText(formatearTiempo(segundosRestantes));
            if (segundosRestantes <= 0) {
                timeline.stop();
                // Tiempo cumplido → finalizar partido automáticamente
                finalizarPartido();
            }
        }));

        timeline.setCycleCount(segundosRestantes);
        timeline.play();
    }

    // Formatea segundos a "MM:SS"
    private String formatearTiempo(int totalSegundos) {
        int minutos = totalSegundos / 60;
        int segs = totalSegundos % 60;
        return String.format("%02d:%02d", minutos, segs);
    }

    // ---------------------------
    // Evento: el usuario hace clic en "Finalizar partido"
    private void onFinalizarClicked(ActionEvent event) {
        if (timeline != null) {
            timeline.stop();
        }
        finalizarPartido();
    }

    // ---------------------------
    // Lógica para terminar el partido:
    //  • Si hay empate, llama a desempate creativo.
    //  • Luego invoca a TorneoController.registrarResultado(...)
    private void finalizarPartido() {
        if (timeline != null) {
            timeline.stop();
        }

        // Si hay empate, resolvemos con desempate de penales
        if (golesLocal == golesVisitante) {
            desempatarPorPenales();
        }

        // Invoco directamente al método de negocio del modelo:
        //   este método ya asigna golesLocal, golesVisitante, finalizado=true y potencialmente huboDesempate.
        partidoActual.finalizarPartido(golesLocal, golesVisitante);

        // Guardar en JSON a través de TorneoController
        torneoController.registrarResultado(torneoActual, partidoActual.getId(), golesLocal, golesVisitante);

        String ganador = (golesLocal > golesVisitante)
                ? partidoActual.getEquipoLocal().getNombre()
                : partidoActual.getEquipoVisitante().getNombre();

        new Alert(Alert.AlertType.INFORMATION,
                "Partido finalizado.\nGanador: " + ganador,
                ButtonType.OK
        ).showAndWait();

        // Cerrar la ventana
        Stage stage = (Stage) lblTimer.getScene().getWindow();
        stage.close();
    }

    // ---------------------------
    // Lógica sencilla de desempate con "penales aleatorios"
    private void desempatarPorPenales() {
        partidoActual.setHuboDesempate(true);
        Random rnd = new Random();

        // Simular penales: cada equipo lanza uno aleatorio hasta que uno quede adelante
        int golesL = golesLocal;
        int golesV = golesVisitante;

        while (golesL == golesV) {
            // Simular tiro local (0 o 1)
            boolean aciertaLocal = rnd.nextBoolean();
            if (aciertaLocal) {
                golesL++;
            }
            // Simular tiro visitante (0 o 1)
            boolean aciertaVisit = rnd.nextBoolean();
            if (aciertaVisit) {
                golesV++;
            }
            // Si tras ambos tiros queda empate, se repite
        }

        golesLocal = golesL;
        golesVisitante = golesV;
        // Actualizar labels en la UI
        lblGolesLocal.setText(String.valueOf(golesLocal));
        lblGolesVisitante.setText(String.valueOf(golesVisitante));
    }

    // ---------------------------
    private void showErrorAndClose(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        alerta.showAndWait();
        Stage stage = (Stage) lblTimer.getScene().getWindow();
        stage.close();
    }
}
