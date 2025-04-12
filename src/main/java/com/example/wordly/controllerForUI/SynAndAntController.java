package com.example.wordly.controllerForUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.example.wordly.API.DatamuseService;
import com.example.wordly.API.DictionaryAPI;

import java.util.List;


public class SynAndAntController {
    @FXML private TextField inputField;
    @FXML private ListView<String> resultList;
    @FXML private TextArea definitionArea;

    private List<String> currentWordList;

    @FXML
    public void handleSynonymClick() {
        String word = inputField.getText().trim();
        if (!word.isEmpty()) {
            currentWordList = DatamuseService.getSynonyms(word);
            resultList.getItems().setAll(currentWordList);
            definitionArea.clear();
        }
    }

    @FXML
    public void handleAntonymClick() {
        String word = inputField.getText().trim();
        if (!word.isEmpty()) {
            currentWordList = DatamuseService.getAntonyms(word);
            resultList.getItems().setAll(currentWordList);
            definitionArea.clear();
        }
    }

    @FXML
    public void handleWordSelection() {
        String selectedWord = resultList.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            String definition = DictionaryAPI.getDefinition(selectedWord);
            definitionArea.setText(definition);
        }
    }

    /**
     * Quay lai man hinh search.
     * @param actionEvent hanh dong la khi bam vao.
     * @throws Exception ngoai le khi sai duong dan.
     */
    public void handlebackSearch(ActionEvent actionEvent) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/SearchView.fxml"));
        Parent SearchView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(SearchView);
    }

}
