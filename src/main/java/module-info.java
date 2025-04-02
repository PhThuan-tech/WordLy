module com.example.wordly {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;
    requires java.net.http;
    requires unirest.java;
    requires java.desktop;

    opens com.example.wordly to javafx.fxml;
    exports com.example.wordly;
    exports com.example.wordly.controller;
    opens com.example.wordly.controller to javafx.fxml;
}