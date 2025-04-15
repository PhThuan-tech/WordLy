package com.example.wordly.controllerForUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class EditWordController extends BaseController {
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EditWordController {
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
    private TextField txtWord, txtPronunciation, txtType;
    @FXML
    private TextArea txtDescription;
    @FXML
    private Button addButton;

    private final Path dictionaryFilePath = Paths.get("data/dictionary.txt");

    public EditWordController() {
        try {
            if (Files.notExists(dictionaryFilePath)) {
                Files.createDirectories(dictionaryFilePath.getParent());
                Files.createFile(dictionaryFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showPopup("Failed to create dictionary file.");
            throw new RuntimeException("Cannot prepare dictionary file!", e);
        }
    }

    public void handleAddButton(ActionEvent event) {
        if (event.getSource() == addButton) {
            String word = txtWord.getText().trim();
            String pronunciation = txtPronunciation.getText().trim();
            String type = txtType.getText().trim();
            String description = txtDescription.getText().trim();

            if (word.isEmpty() || description.isEmpty()) {
                showPopup("Please enter at least the word and its description!");
                return;
            }

            String newLine = String.format("%s | %s | %s | %s", word, pronunciation, type, description);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(dictionaryFilePath.toFile(), true))) {
                writer.write(newLine);
                writer.newLine();
                showPopup("Word added successfully!");
                clearFields();
            } catch (IOException e) {
                e.printStackTrace();
                showPopup("Error writing to dictionary file.");
            }
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
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


