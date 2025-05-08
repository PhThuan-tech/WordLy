package com.example.wordly.controllerForUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.wordly.API.DatamuseService;
import com.example.wordly.API.DictionaryAPI;
import com.example.wordly.History.HistoryManage;
import java.util.List;

public class SynAndAntController extends BaseController {
    @FXML private TextField inputField;
    @FXML private ListView<String> resultList;
    @FXML private TextArea definitionArea;
    private List<String> currentWordList;

    private HistoryManage historyManager = new HistoryManage();

    @FXML public void handlebackSearch(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/Advance_Features.fxml");
    }

    @FXML
    public void handleSynonymClick() {
        String word = inputField.getText().trim();
        if (!word.isEmpty()) {
            currentWordList = DatamuseService.getSynonyms(word);
            saveResultsToTrieFile(currentWordList);
            resultList.getItems().setAll(currentWordList);
            definitionArea.clear();
        }
    }

    @FXML
    public void handleAntonymClick() {
        String word = inputField.getText().trim();
        if (!word.isEmpty()) {
            currentWordList = DatamuseService.getAntonyms(word);
            saveResultsToTrieFile(currentWordList);
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

    private void saveResultsToTrieFile(List<String> wordList) {
        if (wordList == null || wordList.isEmpty()) {
            System.out.println("Danh sách kết quả rỗng, không lưu vào file Trie.");
            return;
        }
        System.out.println("Đang lưu " + wordList.size() + " từ vào file Trie...");
        for (String word : wordList) {
            historyManager.saveWordToTrieFile(word);
        }
        System.out.println("Hoàn tất lưu kết quả Syn/Ant vào file Trie.");
    }
}