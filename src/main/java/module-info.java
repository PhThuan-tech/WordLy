module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;
    requires javafx.media;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires org.json;
    requires annotations;
    requires org.slf4j;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}