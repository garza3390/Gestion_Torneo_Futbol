package com.mycompany.tareaprogramada.util;

import com.mycompany.tareaprogramada.models.Equipo;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ChampionAnimation {

    /**
     * Muestra una ventana modal con una pequeña animación y el nombre del equipo campeón.
     * @param owner   El Stage padre, para que este nuevo Stage sea modal sobre él.
     * @param campeon El objeto Equipo que resultó campeón.
     */
    public static void mostrarAnimacion(Stage owner, Equipo campeon) {
        // 1) Creamos el contenido visual: un StackPane con un texto y algunos círculos “confetti”
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // fondo semitransparente
        root.setPrefSize(400, 300);

        // Texto central que dirá “¡Campeón: <Nombre>!”
        Label txtCampeon = new Label("¡Campeón: " + campeon.getNombre() + "!");
        txtCampeon.setTextFill(Color.GOLD);
        txtCampeon.setFont(Font.font(34));
        StackPane.setAlignment(txtCampeon, Pos.CENTER);

        // Algunas “bolitas” que caerán (simulando confetti)
        Circle confetti1 = new Circle(6, Color.RED);
        Circle confetti2 = new Circle(6, Color.BLUE);
        Circle confetti3 = new Circle(6, Color.GREEN);
        Circle confetti4 = new Circle(6, Color.ORANGE);

        // Posicion inicial arriba de la ventana (fuera de la vista)
        confetti1.setTranslateY(-200);
        confetti2.setTranslateY(-200);
        confetti3.setTranslateY(-200);
        confetti4.setTranslateY(-200);

        // Asignamos X distintos para esparcirlos
        confetti1.setTranslateX(-100);
        confetti2.setTranslateX(-40);
        confetti3.setTranslateX(20);
        confetti4.setTranslateX(80);

        // Agregamos todo al root
        root.getChildren().addAll(confetti1, confetti2, confetti3, confetti4, txtCampeon);

        // 2) Creamos un Stage modal con traslúcido
        Stage dialog = new Stage(StageStyle.TRANSPARENT);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(new Scene(root, Color.TRANSPARENT));

        // 3) Definimos las animaciones:
        // 3a) Animación de entrada del texto (fade + scale)
        FadeTransition fadeInText = new FadeTransition(Duration.seconds(1), txtCampeon);
        fadeInText.setFromValue(0);
        fadeInText.setToValue(1);

        ScaleTransition scaleUpText = new ScaleTransition(Duration.seconds(1), txtCampeon);
        scaleUpText.setFromX(0.5);
        scaleUpText.setFromY(0.5);
        scaleUpText.setToX(1);
        scaleUpText.setToY(1);

        ParallelTransition textoEntrada = new ParallelTransition(fadeInText, scaleUpText);

        // 3b) Animaciones de “confetti” cayendo
        TranslateTransition caer1 = new TranslateTransition(Duration.seconds(2), confetti1);
        caer1.setFromY(-200);
        caer1.setToY(150);

        TranslateTransition caer2 = new TranslateTransition(Duration.seconds(2.2), confetti2);
        caer2.setFromY(-200);
        caer2.setToY(150);

        TranslateTransition caer3 = new TranslateTransition(Duration.seconds(2.4), confetti3);
        caer3.setFromY(-200);
        caer3.setToY(150);

        TranslateTransition caer4 = new TranslateTransition(Duration.seconds(2.6), confetti4);
        caer4.setFromY(-200);
        caer4.setToY(150);

        // 3c) Opcional: un pequeño fade-out al final
        PauseTransition pausa = new PauseTransition(Duration.seconds(3));
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        // 4) Cadena de ejecución de animaciones
        SequentialTransition secuencia = new SequentialTransition(
            // Primero el texto entra
            textoEntrada,
            // Luego, en paralelo, los confettis caen
            new ParallelTransition(caer1, caer2, caer3, caer4),
            // Esperamos un momento
            pausa,
            // Y finalmente hacemos fade-out todo
            fadeOut
        );

        // Cuando termine la animación, cerramos el dialog
        secuencia.setOnFinished(evt -> dialog.close());

        // 5) Mostramos el diálogo y arrancamos la animación
        dialog.show();
        secuencia.play();
    }
}
