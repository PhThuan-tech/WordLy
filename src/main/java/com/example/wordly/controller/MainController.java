package com.example.wordly.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    public Button menuScence;

    @FXML
    public Button HowToUse;

    @FXML
    public Button features;

    public void initialize() {
        if (menuScence != null) {
            Scene scene = menuScence.getScene();
            if (scene != null) {
                scene.getStylesheets().add(getClass()
                        .getResource("/com/example/wordly/styles/mainsce.css").toExternalForm());
            }
        }
    }

    // chuyen giao dien man hinh home
    public void switchToHomeScence(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/view/HomeView.fxml"));
        Parent homeView = loader.load();

        // Xử lý nút bấm sau khi click
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(homeView);
    }
}
