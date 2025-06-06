// src/main/java/com/mycompany/tareaprogramada/controllers/CertificadoViewController.java
package com.mycompany.tareaprogramada.controllers;

import com.mycompany.tareaprogramada.models.Certificado;
import com.mycompany.tareaprogramada.util.CertificadoPDFGenerator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CertificadoViewController implements Initializable {
    @FXML private ImageView imgEquipoCampeon;
    @FXML private Label lblNombreEquipo;
    @FXML private Label lblInfoTorneo;
    @FXML private Label lblRendimiento;
    @FXML private Button btnGenerarPDF;

    private Certificado certificadoActual;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnGenerarPDF.setOnAction(e -> {
            try {
                CertificadoPDFGenerator.generarPDF(certificadoActual);
                new Alert(Alert.AlertType.INFORMATION, "PDF generado correctamente.").showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error al generar PDF: " + ex.getMessage()).showAndWait();
            }
        });
    }

    public void initData(Certificado cert) {
        this.certificadoActual = cert;
        lblNombreEquipo.setText("Equipo Campeón: " + cert.getEquipoCampeon().getNombre());
        lblInfoTorneo.setText("Torneo: " + cert.getTorneo().getDeporte().getNombre() +
                " (Equipos: " + cert.getTorneo().getEquiposInscritos().size() + ")");
        lblRendimiento.setText("Puntos: " + cert.getEstadisticaCampeon().getPuntos() +
                ", GF: " + cert.getEstadisticaCampeon().getGolesAFavor() +
                ", GC: " + cert.getEstadisticaCampeon().getGolesEnContra());
        // Cargar imagen del equipo
        File f = new File(cert.getEquipoCampeon().getFotoPath());
        if (f.exists()) {
            imgEquipoCampeon.setImage(new Image(f.toURI().toString()));
        }
    }
}
