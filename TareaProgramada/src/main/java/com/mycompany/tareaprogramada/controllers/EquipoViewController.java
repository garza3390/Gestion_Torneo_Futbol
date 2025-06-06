/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.mycompany.tareaprogramada.models.Deporte;
import com.mycompany.tareaprogramada.models.Equipo;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador de la vista EquipoView.fxml.
 * Permite:
 *   • Cargar la lista de equipos en la tabla (mostrar nombre, deporte y miniatura de foto).
 *   • Seleccionar un deporte existente desde ComboBox<Deporte>.
 *   • Elegir una imagen para la foto del equipo con FileChooser.
 *   • Agregar un nuevo Equipo a través de EquipoController.
 *   • Eliminar un equipo seleccionado de la tabla.
 */
public class EquipoViewController implements Initializable {

    @FXML private TableView<Equipo> tablaEquipos;
    @FXML private TableColumn<Equipo, String> colNombreEquipo;
    @FXML private TableColumn<Equipo, String> colDeporteEquipo;
    @FXML private TableColumn<Equipo, String> colFotoEquipo;

    @FXML private TextField campoNombreEquipo;
    @FXML private ComboBox<Deporte> comboDeporte;
    @FXML private Button btnSeleccionarFoto;
    @FXML private Label labelRutaFoto;
    @FXML private Button btnAgregarEquipo;
    @FXML private Button btnEliminarEquipo;

    private final EquipoController equipoController = new EquipoController();
    private final DeporteController deporteController = new DeporteController();

    // Ruta absoluta de la foto seleccionada (o null si no se ha seleccionado)
    private String rutaFotoSeleccionada = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1) Configurar columnas de la tabla
        colNombreEquipo.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        colDeporteEquipo.setCellValueFactory(cellData ->
            // Convertir Deporte a String
            javafx.beans.binding.Bindings.createStringBinding(
                () -> {
                    Deporte d = cellData.getValue().getDeporte();
                    return (d != null) ? d.getNombre() : "";
                }
            )
        );

        colFotoEquipo.setCellValueFactory(new PropertyValueFactory<>("fotoPath"));
        colFotoEquipo.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String fotoPath, boolean empty) {
                super.updateItem(fotoPath, empty);
                if (empty || fotoPath == null) {
                    setGraphic(null);
                } else {
                    File archivo = new File(fotoPath);
                    if (archivo.exists()) {
                        Image img = new Image(archivo.toURI().toString(), 40, 40, true, true);
                        imageView.setImage(img);
                        setGraphic(imageView);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // 2) Cargar lista de equipos en la tabla
        ObservableList<Equipo> listaEquipos = equipoController.getEquipos();
        tablaEquipos.setItems(listaEquipos);

        // 3) Poblamos el ComboBox con la lista de deportes
        comboDeporte.setItems(deporteController.getDeportes());

        // 4) Listener opcional: al seleccionar una fila en la tabla, 
        //    podrías mostrar información adicional si quieres.
        //    (No es estrictamente necesario para agregar/eliminar).
    }

    @FXML
    void onSeleccionarFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Elegir foto de equipo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes PNG", "*.png"),
            new FileChooser.ExtensionFilter("Imágenes JPEG", "*.jpg", "*.jpeg")
        );
        Stage stage = (Stage) btnSeleccionarFoto.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);
        if (archivo != null) {
            rutaFotoSeleccionada = archivo.getAbsolutePath();
            labelRutaFoto.setText("Foto: " + archivo.getName());
        }
    }

    @FXML
    void onAgregarEquipo(ActionEvent event) {
        String nombre = campoNombreEquipo.getText().trim();
        Deporte deporteSeleccionado = comboDeporte.getValue();

        // Validaciones básicas
        if (nombre.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Debe ingresar un nombre de equipo.", ButtonType.OK)
                .showAndWait();
            return;
        }
        if (deporteSeleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar un deporte.", ButtonType.OK)
                .showAndWait();
            return;
        }
        if (rutaFotoSeleccionada == null) {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar una foto.", ButtonType.OK)
                .showAndWait();
            return;
        }

        // Crear el nuevo Equipo y guardarlo
        Equipo nuevoEquipo = new Equipo(nombre, rutaFotoSeleccionada, deporteSeleccionado);
        equipoController.agregarEquipo(nuevoEquipo);

        // Limpiar formulario
        campoNombreEquipo.clear();
        comboDeporte.getSelectionModel().clearSelection();
        rutaFotoSeleccionada = null;
        labelRutaFoto.setText("Ninguna foto");
    }

    @FXML
    void onEliminarEquipo(ActionEvent event) {
        Equipo seleccionado = tablaEquipos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione un equipo para eliminar.", ButtonType.OK)
                .showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de eliminar al equipo \"" + seleccionado.getNombre() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                equipoController.eliminarEquipo(seleccionado);
            }
        });
    }
}
