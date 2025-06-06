// src/main/java/com/mycompany/tareaprogramada/controllers/DeporteViewController.java
package com.mycompany.tareaprogramada.controllers;

import com.mycompany.tareaprogramada.models.Deporte;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class DeporteViewController implements Initializable {
    @FXML private TableView<Deporte> tablaDeportes;
    @FXML private TableColumn<Deporte, String> colNombreDeporte;
    @FXML private TableColumn<Deporte, String> colImagenDeporte;
    @FXML private TextField campoNombreDeporte;
    @FXML private Button btnSeleccionarBalon;
    @FXML private Button btnAgregarDeporte;
    @FXML private Button btnEliminarDeporte;

    private final DeporteController deporteController = new DeporteController();
    private String rutaBalonSeleccionado = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNombreDeporte.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colImagenDeporte.setCellValueFactory(new PropertyValueFactory<>("imagenPath"));
        colImagenDeporte.setCellFactory(col -> new TableCell<>() {
            private final ImageView imgView = new ImageView();
            {
                imgView.setFitWidth(40);
                imgView.setFitHeight(40);
                imgView.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null) {
                    setGraphic(null);
                } else {
                    File f = new File(path);
                    if (f.exists()) {
                        imgView.setImage(new Image(f.toURI().toString(), 40, 40, true, true));
                        setGraphic(imgView);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        tablaDeportes.setItems(deporteController.getDeportes());
    }

    @FXML
    void onSeleccionarBalon() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen de balón");
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PNG/JPG", "*.png", "*.jpg", "*.jpeg")
        );
        Stage stage = (Stage) btnSeleccionarBalon.getScene().getWindow();
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            rutaBalonSeleccionado = file.getAbsolutePath();
            btnSeleccionarBalon.setText("✓ Balón seleccionado");
        }
    }

    @FXML
    void onAgregarDeporte() {
        String nombre = campoNombreDeporte.getText().trim();
        if (nombre.isEmpty() || rutaBalonSeleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Nombre e imagen son obligatorios.").showAndWait();
            return;
        }
        Deporte d = new Deporte(nombre, rutaBalonSeleccionado);
        deporteController.agregarDeporte(d);
        campoNombreDeporte.clear();
        rutaBalonSeleccionado = null;
        btnSeleccionarBalon.setText("Seleccionar balón");
    }

    @FXML
    void onEliminarDeporte() {
        Deporte sel = tablaDeportes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione un deporte.").showAndWait();
            return;
        }
        deporteController.eliminarDeporte(sel);
    }
}
