package com.example.wordly.controllerForUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.wordly.API.DatamuseService;
import com.example.wordly.API.DictionaryAPI;
import java.util.List;

public class SynAndAntController extends BaseController {
    @FXML private TextField inputField;
    @FXML private ListView<String> resultList;
    @FXML private TextArea definitionArea;

    /**
     * Quay lại màn hình chính.
     * @param actionEvent Nhấn nút để quay lại.
     */
    public void handlebackSearch(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }

    // Tạo 1 danh sách các từ vựng cùng trường nghĩa với nhau lưu vào List kiểu String.
    private List<String> currentWordList;

    /*
      Xử lí hành động nhấn nút bấm để tạo danh sách từ cùng trường nghĩa.
      Có thể lưu danh sách đó vào "history.txt" .
     */
    @FXML
    public void handleSynonymClick() {
        String word = inputField.getText().trim();
        if (!word.isEmpty()) {
            currentWordList = DatamuseService.getSynonyms(word);    // tạo danh sách lấy từ đồng nghĩa
            resultList.getItems().setAll(currentWordList);          // lấy hết từ đồng nghĩa đó cho vào list
            definitionArea.clear();                     // Khi lấy xong từ này mà cần chuyển sang từ khác thì xóa từ cũ
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

    /**
     * Xử lí tra nghĩa của từ khi bấm vào danh sách các trường nghĩa.
     */
    @FXML
    public void handleWordSelection() {
        String selectedWord = resultList.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            String definition = DictionaryAPI.getDefinition(selectedWord);
            definitionArea.setText(definition);
        }
    }
}
