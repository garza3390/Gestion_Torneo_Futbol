package com.mycompany.tareaprogramada.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {

    // =============================
    // Métodos para abrir cada ventana
    // =============================

    @FXML
    private void onMenuDeportes(ActionEvent event) {
        abrirVentana("/fxml/DeporteView.fxml", "Gestión de Deportes", 600, 400);
    }

    @FXML
    private void onMenuEquipos(ActionEvent event) {
        abrirVentana("/fxml/EquipoView.fxml", "Gestión de Equipos", 800, 500);
    }

    @FXML
    private void onMenuTorneos(ActionEvent event) {
        abrirVentana("/fxml/TorneoView.fxml", "Gestión de Torneos", 1000, 700);
    }

    @FXML
    private void onMenuEstadisticas(ActionEvent event) {
        abrirVentana("/fxml/EstadisticasView.fxml", "Estadísticas y Ranking", 900, 600);
    }

    // =============================
    // Método “Acerca de”
    // =============================

    @FXML
    private void onAcercaDe(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.setTitle("Acerca de");
        alerta.setHeaderText("Sistema de Gestión de Torneos");
        alerta.setContentText(
            "Versión: 1.0.0\n" +
            "Desarrollado por: Equipo de trabajo\n" +
            "Descripción: Esta aplicación permite gestionar deportes, equipos y torneos\n" +
            "con generación de llaves, partidos, estadísticas y certificados en PDF."
        );
        alerta.showAndWait();
    }

    // =============================
    // Método “Salir”
    // =============================

    @FXML
    private void onSalir(ActionEvent event) {
        // Cierra todas las ventanas y sale de la aplicación
        Stage ventanaActual = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        ventanaActual.close();
    }

    // =============================
    // Helper: abre una nueva ventana con el FXML dado
    // =============================
    private void abrirVentana(String rutaFxml, String titulo, double ancho, double alto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent root = loader.load();
            Stage ventana = new Stage();
            ventana.setTitle(titulo);
            ventana.setScene(new Scene(root, ancho, alto));
            ventana.initModality(Modality.NONE);
            ventana.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir la ventana: " + titulo, ButtonType.OK).showAndWait();
        }
    }
}
