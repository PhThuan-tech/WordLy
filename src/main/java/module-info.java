module com.example.wordly {

    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;

    requires java.net.http;
    requires java.desktop;
    requires org.json;
    requires javafx.media;
    requires jdk.jartool;

    opens com.example.wordly to javafx.fxml;
    exports com.example.wordly;

    exports com.example.wordly.controllerForUI;
    opens com.example.wordly.controllerForUI to javafx.fxml;
    exports com.example.wordly.History;
    opens com.example.wordly.History to javafx.fxml;
    exports com.example.wordly.TTS;
    opens com.example.wordly.TTS to javafx.fxml;
}