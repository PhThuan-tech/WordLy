package com.example.wordly.controllerForUI;

import com.example.wordly.SQLite.NewAddedWordDAO;
import com.example.wordly.getWord.WordEntry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EditWordController extends BaseController {

    @FXML
    public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/view/MainView.fxml");
    }

    @FXML
    public void handleGoToSearch(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }

    @FXML
    public void handleGoToFavourite(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/FavouriteView.fxml");
    }

    @FXML
    public void handleGotoGame(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml");
    }

    @FXML
    public void handleGoToTranslateAndTTS(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/TranslateAndTTS.fxml");
    }

    @FXML
    public void handleGoToHistory(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/HistoryView.fxml");
    }


    @FXML
    public void handleGoToChat(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/ChatBot.fxml");
    }

    @FXML
    private TextField txtWord, txtPronunciation, txtType;
    @FXML
    private TextArea txtDescription;
    @FXML
    private Button addButton;

    public void handleAddButton(ActionEvent event) {
        if (event.getSource() != addButton) {
            return;
        }

        String word = txtWord.getText();
        String pron = "/" + txtPronunciation.getText() + "/";
        String type = txtType.getText();
        String desc = txtDescription.getText();

        if (word.isEmpty()) {
            showPopup("Vui lòng điền từ bạn muốn thêm !");
            return;
        }
        if (desc.isEmpty()) {
            showPopup("Vui long điền nghĩa của từ bạn thêm !");
            return;
        }

        NewAddedWordDAO newAddedWordDAO = new NewAddedWordDAO();
        boolean isAdded = newAddedWordDAO.addNewWord(word, pron, type, desc);

        if (isAdded) {
            showPopup("Thêm từ thành công !!!");
            clearFields();
        } else {
            showPopup("\"Từ này đã thêm mất rồi \uD83D\uDE22!!!\"");
            clearFields();
        }

    }

    private void clearFields() {
        txtWord.clear();
        txtPronunciation.clear();
        txtType.clear();
        txtDescription.clear();
    }

    // Popup hien thi them tu
    private void showPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ALO ALO");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


