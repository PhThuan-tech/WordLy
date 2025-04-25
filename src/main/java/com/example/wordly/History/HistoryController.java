package com.example.wordly.History;

import com.example.wordly.controllerForUI.BaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;


import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class HistoryController extends BaseController {

    @FXML private ListView<WordEntry> historyListView;

    @FXML private Button delHistory;

    private ObservableList<WordEntry> historyEntries;

    private YourHistoryService historyService;


    @FXML
    public void initialize() {
        historyService = new YourHistoryService();


        historyEntries = FXCollections.observableArrayList();

        historyListView.setCellFactory(listView -> new ListCell<WordEntry>() {
            private HistoryCellController controller;
            private VBox cellLayout;

            @Override
            protected void updateItem(WordEntry item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    if (controller == null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/HistoryCell.fxml"));
                            cellLayout = loader.load();
                            controller = loader.getController();
                        } catch (IOException e) {
                            e.printStackTrace();
                            setGraphic(new Label("Error loading cell"));
                            return;
                        }
                    }
                    controller.updateCell(item);
                    setGraphic(cellLayout);
                }
            }
        });

        loadHistoryData();

        historyListView.setItems(historyEntries);
    }

    private void loadHistoryData() {
        List<WordEntry> loadedData = historyService.getAllHistoryEntries();

        if (loadedData != null) {
            historyEntries.setAll(loadedData);
        } else {
            historyEntries.clear();
        }
    }

    @FXML
    private void delHistory() {
        historyEntries.clear();
        historyService.clearHistory();
    }

    // *** SỬA CÁC PHƯƠNG THỨC XỬ LÝ SỰ KIỆN ĐỂ NHẬN ActionEvent VÀ TRUYỀN NÓ ĐI ***
    @FXML
    private void handleBackMain(ActionEvent event) { // Nhận ActionEvent
        switchScene(event, "/com/example/wordly/View/HomeView.fxml"); // Truyền ActionEvent
    }

    @FXML
    private void handleGoToSearch(ActionEvent event) { // Nhận ActionEvent
        switchScene(event, "/com/example/wordly/View/SearchView.fxml"); // Truyền ActionEvent
    }

    @FXML
    private void handleGoToFavourite(ActionEvent event) { // Nhận ActionEvent
        switchScene(event, "/com/example/wordly/View/FavouriteView.fxml"); // Truyền ActionEvent
    }

    @FXML
    private void handleGotoEdit(ActionEvent event) { // Nhận ActionEvent
        switchScene(event, "/com/example/wordly/View/EditWordView.fxml"); // Truyền ActionEvent
    }

    @FXML
    private void handleGotoGame(ActionEvent event) { // Nhận ActionEvent
        switchScene(event, "/com/example/wordly/View/GameView.fxml"); // Truyền ActionEvent
    }

    @FXML
    private void handleGoToTranslateAndTTS(ActionEvent event) { // Nhận ActionEvent
        switchScene(event, "/com/example/wordly/View/TranslateAndTTSView.fxml"); // Truyền ActionEvent
    }
    // *** KẾT THÚC SỬA CÁC PHƯƠNG THỨC ***


    // LỚP SERVICE QUẢN LÝ LỊCH SỬ (VÍ DỤ)
    private static class YourHistoryService {
        private static List<WordEntry> historyStorage = new ArrayList<>();

        public YourHistoryService() {
            if (historyStorage.isEmpty()) {
                historyStorage.add(new WordEntry("Apple", "noun", "/ˈæpl/", "a round fruit with firm, white flesh and a green, red, or yellow skin."));
                historyStorage.add(new WordEntry("Banana", "noun", "/bəˈnænə/", "a long, curved fruit with a yellow skin and soft, sweet flesh."));
                historyStorage.add(new WordEntry("Computer", "noun", "/kəmˈpjuːtər/", "an electronic device for storing and processing data, typically in binary form, according to instructions given to it in a variable program."));
            }
        }

        public List<WordEntry> getAllHistoryEntries() {
            return new ArrayList<>(historyStorage);
        }

        public void addHistoryEntry(WordEntry entry) {
            historyStorage.add(entry);
        }

        public void clearHistory() {
            historyStorage.clear();
        }

        public void saveHistoryToFile() {
            System.out.println("Saving history to file...");
        }
    }
}