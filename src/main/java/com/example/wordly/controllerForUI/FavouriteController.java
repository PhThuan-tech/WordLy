package com.example.wordly.controllerForUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

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

    @FXML
    private Button deleteButton;

    @FXML
    private Button yesButton;

    @FXML
    private Button noButton;

    @FXML
    private AnchorPane confirmationDialog;

    @FXML
    private void handleDeleteButton() {
        confirmationDialog.setVisible(true);
    }

    @FXML
    public void initialize() {
        confirmationDialog.setVisible(false);

        // hiện hộp thoại
        deleteButton.setOnAction(event -> confirmationDialog.setVisible(true));

        // bấm no -> ẩn hộp thoại
        noButton.setOnAction(event -> confirmationDialog.setVisible(false));

        // bấm YES, chức năng xóa
        yesButton.setOnAction(event -> {
            //Thêm chức năng xóa

            confirmationDialog.setVisible(false);
        });
    }
}
