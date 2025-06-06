package com.mycompany.tareaprogramada;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App que arranca en MainView.fxml (ubicado en src/main/resources/fxml/MainView.fxml)
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Cargamos MainView.fxml desde la carpeta /fxml
        scene = new Scene(loadFXML("MainView"), 640, 480);
        stage.setScene(scene);
        stage.setTitle("Menú Principal");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        // Ahora buscamos en "/fxml/<fxml>.fxml"
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
