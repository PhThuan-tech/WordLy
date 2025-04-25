package com.example.wordly.controllerForUI;

import com.example.wordly.Levenshtein.LevenshteinUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javax.imageio.IIOException;
import java.io.*;

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

    @FXML
    public void handleGoToWordEdit(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/WordListView.fxml");
    }

    @FXML
    private Button deleteButton;

    @FXML
    private Button yesButton;

    @FXML
    private Button noButton;

    @FXML
    private AnchorPane confirmationDialog;

    @FXML public TableView<WordEntry> tableView;
    @FXML public TableColumn<WordEntry, String> wordCol;
    @FXML public TableColumn<WordEntry, String> typeCol;
    @FXML public TableColumn<WordEntry, String> pronCol;
    @FXML public TableColumn<WordEntry, String> meaningCol;
    @FXML private TextField searchField;

    @FXML
    private void handleDeleteButton() {
        confirmationDialog.setVisible(true);
    }

    private FilteredList<WordEntry> filteredData;


    public class WordEntry {
        private final SimpleStringProperty word;
        private final SimpleStringProperty pronunciation;
        private final SimpleStringProperty type;
        private final SimpleStringProperty meaning;

        public WordEntry(String word, String pronunciation, String type, String meaning) {
            this.word = new SimpleStringProperty(word);
            this.pronunciation = new SimpleStringProperty(pronunciation);
            this.type = new SimpleStringProperty(type);
            this.meaning = new SimpleStringProperty(meaning);
        }

        public String getWord() {
            return word.get();
        }


        public String getPronunciation() {
            return pronunciation.get();
        }

        public String getType() {
            return type.get();
        }

        public String getMeaning() {
            return meaning.get();
        }
    }


    private ObservableList<WordEntry> wordList = FXCollections.observableArrayList();

    @FXML
    public void mappingTable() {
        wordCol.setCellValueFactory(new PropertyValueFactory<>("word"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        pronCol.setCellValueFactory(new PropertyValueFactory<>("pronunciation"));
        meaningCol.setCellValueFactory(new PropertyValueFactory<>("meaning"));

        tableView.setItems(wordList);
    }

    private void loadTableData(String fileName) {
        wordList.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    String word = parts[0].trim();
                    String type = parts[1].trim();
                    String pron = parts[2].trim();
                    String meaning = parts[3].trim();
                    wordList.add(new WordEntry(word, pron, type, meaning));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveListToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (WordEntry entry : wordList) {
                writer.write(entry.getWord() + " | "
                        + entry.getPronunciation() + " | "
                        + entry.getType() + " | "
                        + entry.getMeaning());
                writer.newLine();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void deleteSelectedWord() {
        WordEntry selectedWord = tableView.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            wordList.remove(selectedWord);
            saveListToFile("data/favourites.txt");
        }
    }

    @FXML
    private void onSearchKeyReleased(KeyEvent event) {
        String keyword = searchField.getText().trim();
        int threshold = 3;

        filteredData.setPredicate(entry -> {
            if (keyword.isEmpty()) return true;
            return LevenshteinUtils.inThreshold(entry.getWord(), keyword, threshold);
        });
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
            deleteSelectedWord();

            confirmationDialog.setVisible(false);
        });
        mappingTable();
        loadTableData("data/favourites.txt");

        filteredData = new FilteredList<>(wordList, e -> true);
        tableView.setItems(filteredData);

        searchField.setOnKeyReleased(this::onSearchKeyReleased);
    }

}

