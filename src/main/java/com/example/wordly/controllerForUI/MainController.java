package com.example.wordly.controllerForUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class MainController extends BaseController {
    @FXML
    public Button menuScence;

    @FXML
    public Button HowToUse;

    @FXML
    public Button features;

    /**
     * Hàm khởi tạo CSS vào trong MainController.
     */
    public void initialize() {
        if (menuScence != null) {
            Scene scene = menuScence.getScene();
            if (scene != null) {
                scene.getStylesheets().add(getClass()
                        .getResource("/com/example/wordly/styles/mainsce.css").toExternalForm());
            }
        }
    }

    // Các Hàm chuyển đổi giữa các giao diện.
    public void switchToHomeScence(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }

    public void handleGoToUsing(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/UsingMehodView.fxml");
    }
}
