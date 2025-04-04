package com.example.wordly.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class MainController {
    @FXML
    public Button menuScence;

    @FXML
    public Button HowToUse;

    @FXML
    public Button features;

    // load CSS vao scence builder
    public void initialize() {
        Scene scene = menuScence.getScene();
        if (scene != null) {
            scene.getStylesheets().add(getClass()
                    .getResource("/com/example/wordly/styles/button.css").toExternalForm());
        }
    }
}
