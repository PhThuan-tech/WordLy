package com.example.wordly.controllerForUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class UsingMethodController extends BaseController {
    @FXML
    public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/view/MainView.fxml");
    }
}


