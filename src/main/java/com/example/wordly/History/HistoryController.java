package com.example.wordly.History;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {
    private static final String History_File = "src/history.txt";
    private static final int Max_Row = 12;
    public TableView<WordEntry> historyTable;
    public TableColumn<WordEntry, String> wordCol;
    public TableColumn<WordEntry, String> proCol;
    public TableColumn<WordEntry, String> typeCol;
    public TableColumn<WordEntry, String> meanCol;
    public Button delHistory;

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

    public void handleGoToFavourite(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/FavouriteView.fxml"));
        Parent favouriteView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(favouriteView);
    }

    @FXML
    public void handleGotoEdit(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/EditWordView.fxml"));
        Parent EditWordView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(EditWordView);
    }

    @FXML
    public void handleGotoGame(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/GameView.fxml"));
        Parent GameView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(GameView);
    }

    @FXML
    public void handleGoToTranslateAndTTS(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/TranslateAndTTS.fxml"));
        Parent TranslateAndTTSView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(TranslateAndTTSView);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        wordCol.setCellValueFactory(new PropertyValueFactory<>("word"));
        proCol.setCellValueFactory(new PropertyValueFactory<>("pronunciation")); // Giả sử property là "pronunciation"
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        meanCol.setCellValueFactory(new PropertyValueFactory<>("definition"));     // Giả sử property là "definition"

        try {
            loadAndDisplayHistory();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void loadAndDisplayHistory() throws IOException {
        ObservableList<WordEntry> historyData = loadHistoryFromFile(History_File);
        historyTable.setItems(historyData);
    }

    private ObservableList<WordEntry> loadHistoryFromFile(String filePath) throws IOException {
        ObservableList<WordEntry> data = FXCollections.observableArrayList();
        Path path = Paths.get(filePath);

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return data;
        }

        int startIndex = Math.max(0, lines.size() - Max_Row);
        List<WordEntry> recent = new ArrayList<>();

        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i);

            String[] parts = line.split("\t");

            if (parts.length == 4) {
                String word = parts[0].trim();
                String pronunciation = parts[1].trim();
                String type = parts[2].trim();
                String definition = parts[3].trim();

                recent.add(new WordEntry(word, pronunciation, type, definition));
            } else {
                System.err.println("Loi o " + line);
            }
        }

        data.addAll(recent);
        return data;
    }

    private void setDelHistory() throws IOException {
        try (FileWriter fw = new FileWriter("src/history.txt")) {

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void delHistory(ActionEvent event) throws IOException {
        setDelHistory();
    }
}
