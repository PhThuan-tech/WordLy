module com.example.wordly {
module com.example.tamthaituuettest {

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
<<<<<<< HEAD
    exports com.example.wordly.controller;
    opens com.example.wordly.controller to javafx.fxml;


    opens com.example.tamthaituuettest to javafx.fxml;
    exports com.example.tamthaituuettest;
=======
    exports com.example.wordly.controllerForUI;
    opens com.example.wordly.controllerForUI to javafx.fxml;
>>>>>>> 84f4436 (add more scence and controller)
}