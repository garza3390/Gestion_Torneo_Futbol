module com.mycompany.tareaprogramada {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    requires org.apache.pdfbox;
    requires org.apache.fontbox;
    requires java.desktop;

    // Abrir paquetes de controladores a JavaFX FXML para que FXMLLoader pueda instanciarlos
    opens com.mycompany.tareaprogramada.controllers to javafx.fxml;
    // Abrir paquete de modelos a Gson para serialización/deserialización
    opens com.mycompany.tareaprogramada.models to com.google.gson;
    // Abrir paquete raíz si también contiene clases con @FXML
    opens com.mycompany.tareaprogramada to javafx.fxml;

    
    exports com.mycompany.tareaprogramada;
    exports com.mycompany.tareaprogramada.controllers;
    exports com.mycompany.tareaprogramada.models;
}
