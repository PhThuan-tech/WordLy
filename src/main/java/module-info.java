module com.example.wordly {

    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;

    requires java.net.http;
    requires org.json;
    requires javafx.media;
    requires jdk.jartool;
    requires annotations;
    requires com.fasterxml.jackson.databind;
    requires okhttp3;
    requires java.desktop;
    requires com.google.gson;
    requires java.sql;

    opens com.example.wordly to javafx.fxml;
    exports com.example.wordly;

    exports com.example.wordly.controllerForUI;
    opens com.example.wordly.controllerForUI to javafx.fxml;
    exports com.example.wordly.History;
    opens com.example.wordly.History to javafx.fxml;
    exports com.example.wordly.TTS;
    opens com.example.wordly.TTS to javafx.fxml;

    opens com.example.wordly.getWord to javafx.base;

    exports com.example.wordly.GameController;
    opens com.example.wordly.GameController to javafx.fxml;

    exports com.example.wordly.GameController.ScrambleWord;
    opens com.example.wordly.GameController.ScrambleWord to javafx.fxml;
}