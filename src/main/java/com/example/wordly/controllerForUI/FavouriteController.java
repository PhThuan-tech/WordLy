package com.example.wordly.controllerForUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class FavouriteController extends BaseController {
    // Các hàm chuyển đổi các giao diện qua lại.
    @FXML
    public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/view/MainView.fxml");
    }

    @FXML
    public void handleGoToSearch(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }

    @FXML
    public void handleGoToHistory(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/HistoryView.fxml");
    }

    @FXML
    public void handleGotoEdit(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/EditWordView.fxml");
    }

    @FXML
    public void handleGotoGame(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml");
    }

    @FXML
    public void handleGoToTranslateAndTTS(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/TranslateAndTTS.fxml");
    }

    public void handleKeyEnterPress(KeyEvent keyEvent) {
    }

    public void handleMouseClicked(MouseEvent mouseEvent) {

    }
}
