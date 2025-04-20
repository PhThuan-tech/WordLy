package com.example.wordly.controllerForUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class FavouriteController {
    @FXML
    public void handleBackMain(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/view/MainView.fxml"));
        Parent mainView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(mainView);
    }

    @FXML
    public void handleGoToSearch(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/SearchView.fxml"));
        Parent searchView = null;
        try {
            searchView = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(searchView);
    }

    @FXML
    public void handleGoToHistory(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/HistoryView.fxml"));
        Parent historyView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(historyView);
    }

    @FXML
    public void handleGotoEdit(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/EditWordView.fxml"));
        Parent EditWordView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(EditWordView);
    }

    @FXML
    public void handleGotoGame(ActionEvent actionEvent) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/GameView.fxml"));
        Parent GameView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(GameView);
    }

    @FXML
    public void handleGoToTranslateAndTTS(ActionEvent actionEvent) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/TranslateAndTTS.fxml"));
        Parent TranslateAndTTSView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(TranslateAndTTSView);
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
