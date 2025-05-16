package com.example.wordly.controllerForUI;

import com.example.wordly.Levenshtein.LevenshteinUtils;
import com.example.wordly.SQLite.DictionaryDAO;
import com.example.wordly.SQLite.DictionaryViEnDAO;
import com.example.wordly.SQLite.FavouriteWordDAO;
import com.example.wordly.getWord.Trie;
import com.example.wordly.getWord.WordEntry;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SearchOnDatabase extends BaseController {
/* =================== PHƯƠNG THỨC CHUYỂN VIEW ================================================================= */
    @FXML
    public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/view/MainView.fxml");
    }

    @FXML
    public void BackToSearchView(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }

    @FXML
    public void handleGoToFavourite(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/FavouriteView.fxml");
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
    public void GoToAdvanceFeature(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/Advance_Features.fxml");
    }

/* ============================================================================================================== */



/* ============================== PHƯƠNG THỨC KHỞI TẠO VIEW BAN ĐẦU ================================================== */
    @FXML public BorderPane rootPane;
    @FXML private ListView<WordEntry> wordListView;
    @FXML private TextArea wordDetail;
    private ObservableList<WordEntry> wordList = FXCollections.observableArrayList();
    private FilteredList<WordEntry> filteredData;

    private PauseTransition pause = new PauseTransition(Duration.millis(300));

    @FXML
    public void initialize() {
        applyHoverEffectToAllButtons(rootPane);
        // Thiết lập placeholder mặc định dựa trên trạng thái ban đầu
        if (isEnToVi) {
            searchField.setPromptText("Search here");
        } else {
            searchField.setPromptText("Tìm kiếm ở đây");
        }

        loadWordsToListView();

        wordListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(WordEntry item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getWord());
            }
        });

        wordListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedWord) -> {
            if (selectedWord != null) {
                StringBuilder detail = new StringBuilder();

                if (selectedWord.getPronunciation() != null && !selectedWord.getPronunciation().isEmpty()) {
                    detail.append(selectedWord.getPronunciation()).append("\n");
                }

                if (selectedWord.getMeaning() != null) {
                    detail.append(selectedWord.getMeaning().trim());
                }

                wordDetail.setText(detail.toString());
            }
        });

        filteredData = new FilteredList<>(wordList, e -> true);
        wordListView.setItems(filteredData);

        // Cải thiện xử lý sự kiện nhập liệu
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Hủy task tìm kiếm hiện tại nếu có
            if (currentSearchTask != null && !currentSearchTask.isDone()) {
                currentSearchTask.cancel(true);
            }

            pause.setOnFinished(e -> {
                String keyword = newVal.trim().toLowerCase();
                if (keyword.isEmpty()) {
                    filteredData.setPredicate(entry -> true);
                } else {
                    performSearch(keyword);
                }
            });
            pause.playFromStart();
        });

        btnSwitchLang.setOnAction(event -> switchLanguage());

    }
/* ================================================================================================================== */



/* ============================ CHUYỂN ĐỔI VIEW + LOAD TỪ ĐIỂN TƯƠNG ỨNG ============================================ */
    @FXML private TextField searchField;
    @FXML private ImageView imgFromLang;
    @FXML private ImageView imgToLang;
    @FXML private Button btnSwitchLang;

    private DictionaryDAO dictionaryDAO = new DictionaryDAO();
    private Trie trie = new Trie();

    private boolean isEnToVi = true;

    // đổi ngôn ngữ + hiệu ứng
    private void switchLanguage() {
        // Tạo hiệu ứng cho việc hoán đổi hình ảnh
        Duration duration = Duration.millis(500); // Thời gian của hiệu ứng

        // Tạo hiệu ứng mờ dần cho cả hai hình ảnh
        FadeTransition fadeOutFrom = new FadeTransition(duration, imgFromLang);
        fadeOutFrom.setFromValue(1.0);
        fadeOutFrom.setToValue(0.0);

        FadeTransition fadeOutTo = new FadeTransition(duration, imgToLang);
        fadeOutTo.setFromValue(1.0);
        fadeOutTo.setToValue(0.0);

        TranslateTransition moveFrom = new TranslateTransition(duration, imgFromLang);
        moveFrom.setByX(imgToLang.getLayoutX() - imgFromLang.getLayoutX());

        TranslateTransition moveTo = new TranslateTransition(duration, imgToLang);
        moveTo.setByX(imgFromLang.getLayoutX() - imgToLang.getLayoutX());

        // Chạy song song các hiệu ứng
        ParallelTransition parallelFadeOut = new ParallelTransition(fadeOutFrom, fadeOutTo);

        parallelFadeOut.setOnFinished(e -> {
            // Hoán đổi hình ảnh
            Image temp = imgFromLang.getImage();
            imgFromLang.setImage(imgToLang.getImage());
            imgToLang.setImage(temp);

            imgFromLang.setTranslateX(0);
            imgToLang.setTranslateX(0);

            // Đảo ngược trạng thái ngôn ngữ
            isEnToVi = !isEnToVi;
            searchField.clear();
            if (isEnToVi) {
                searchField.setPromptText("Search here");
            } else {
                searchField.setPromptText("Tìm kiếm ở đây");
            }

            // Tạo hiệu ứng hiện dần cho cả hai hình ảnh sau khi đã hoán đổi
            FadeTransition fadeInFrom = new FadeTransition(duration, imgFromLang);
            fadeInFrom.setFromValue(0.0);
            fadeInFrom.setToValue(1.0);

            FadeTransition fadeInTo = new FadeTransition(duration, imgToLang);
            fadeInTo.setFromValue(0.0);
            fadeInTo.setToValue(1.0);

            ParallelTransition parallelFadeIn = new ParallelTransition(fadeInFrom, fadeInTo);

            parallelFadeIn.setOnFinished(event -> {
                loadDictionary();
            });

            parallelFadeIn.play();
        });

        parallelFadeOut.play();
    }

    // load từ điển tương ứng
    private void loadDictionary() {
        Task<ObservableList<WordEntry>> task = new Task<>() {
            @Override
            protected ObservableList<WordEntry> call() {
                List<WordEntry> wordEntries;

                if (isEnToVi) {
                    DictionaryDAO dao = new DictionaryDAO();
                    wordEntries = dao.getAllWords();
                } else {
                    DictionaryViEnDAO dao = new DictionaryViEnDAO();
                    wordEntries = dao.getAllWords();
                }

                return FXCollections.observableArrayList(wordEntries);
            }
        };

        searchField.setDisable(true);

        task.setOnSucceeded(e -> {
            wordList.setAll(task.getValue());

            trie = new Trie();
            for (WordEntry word : wordList) {
                trie.insert(word.getWord().toLowerCase());
            }

            searchField.setDisable(false);
        });

        new Thread(task).start();
    }

    // load từ vào listview +
    private void loadWordsToListView() {
        Task<ObservableList<WordEntry>> task = new Task<>() {
            @Override
            protected ObservableList<WordEntry> call() {
                List<WordEntry> wordEntries;

                if (isEnToVi) {
                    DictionaryDAO dao = new DictionaryDAO();
                    wordEntries = dao.getAllWords();
                } else {
                    DictionaryViEnDAO dao = new DictionaryViEnDAO();
                    wordEntries = dao.getAllWords();
                }

                return FXCollections.observableArrayList(wordEntries);
            }
        };

        searchField.setDisable(true);

        task.setOnSucceeded(e -> {
            wordList.setAll(task.getValue());

            trie = new Trie();
            for (WordEntry word : wordList) {
                trie.insert(word.getWord().toLowerCase());
            }

            searchField.setDisable(false);
        });

        new Thread(task).start();
    }

/* ================================================================================================================== */


/* ==================== TÌM KIẾM TỪ ================================================================================= */
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> currentSearchTask = null;


    private void performSearch(String keyword) {
        Task<List<String>> searchTask = new Task<>() {
            @Override
            protected List<String> call() {
                // Lấy kết quả từ Trie (prefix search)
                List<String> trieSuggestions = trie.getSuggestions(keyword);
                // Nếu đủ kết quả, trả về ngay
                if (trieSuggestions.size() >= 10) {
                    return trieSuggestions;
                }
                // Nếu cần bổ sung bằng Levenshtein
                List<String> fuzzyResults = new ArrayList<>();
                int checked = 0;
                // Tạo một tập hợp từ nhỏ hơn để kiểm tra (tối ưu hóa)
                List<WordEntry> candidatesToCheck = new ArrayList<>();
                // Chỉ xem xét những từ có độ dài tương tự
                for (WordEntry entry : wordList) {
                    String word = entry.getWord().toLowerCase();
                    if (!trieSuggestions.contains(word) &&
                            Math.abs(word.length() - keyword.length()) <= 1) {
                        candidatesToCheck.add(entry);
                    }
                }
                // Áp dụng Levenshtein trên tập nhỏ hơn
                for (WordEntry entry : candidatesToCheck) {
                    if (Thread.currentThread().isInterrupted()) {
                        return trieSuggestions; // Return early if interrupted
                    }

                    String word = entry.getWord().toLowerCase();
                    checked++;

                    if (LevenshteinUtils.inThreshold(word, keyword, 1)) {
                        fuzzyResults.add(word);
                    }

                    if (fuzzyResults.size() + trieSuggestions.size() >= 10) {
                        break;
                    }
                }
                // Kết hợp kết quả
                trieSuggestions.addAll(fuzzyResults);
                return trieSuggestions;
            }
        };

        searchTask.setOnSucceeded(e -> {
            List<String> matches = searchTask.getValue();
            filteredData.setPredicate(entry ->
                    matches.contains(entry.getWord().toLowerCase()));
        });

        searchTask.setOnFailed(e -> {
            Throwable exception = searchTask.getException();
            if (exception != null) {
                System.err.println("Search failed: " + exception.getMessage());
                exception.printStackTrace();
            }
        });
        // Sử dụng ExecutorService để quản lý thread
        currentSearchTask = executor.submit(() -> {
            try {
                searchTask.run();
            } catch (Exception ex) {
                System.err.println("Search task error: " + ex.getMessage());
            }
        });
    }
/* ================================================================================================================== */



/* ============================= LƯU VÀO FAVOURITE/HIỂN THỊ THÔNG BÁO =============================================== */
    @FXML private StackPane saveSuccessfullyNotification;
    @FXML private StackPane saveFailNotification;
    @FXML private Button saveButton;

    @FXML private void handleSaveButton(ActionEvent actionEvent) {
        String word = wordListView.getSelectionModel().getSelectedItem().getWord().trim();
        String meaning = wordDetail.getText().trim();

        if (word.isEmpty()) {
            System.out.println("không thể thêm từ");
            return;
        }

        FavouriteWordDAO newFavourite = new FavouriteWordDAO();
        if (newFavourite.isFavouriteWord(word)) {
            System.out.println("Từ này đã có trong danh sách yêu thích");
            // hiện label
            saveFailNotification.setVisible(true);
            /** ---------- hiệu ứng hiển thị pane thông báo ---------------- **/
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), saveFailNotification);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            PauseTransition stay = new PauseTransition(Duration.seconds(1.5));

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), saveFailNotification);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> saveFailNotification.setVisible(false));

            SequentialTransition sequence = new SequentialTransition(fadeIn, stay, fadeOut);
            sequence.play();

            /** ---------------------------------------------------------------------------------------------------- **/
        } else {
            try {
                newFavourite.addFavouriteWord(word,null, null, meaning);
                System.out.println("Đã thêm từ vào danh sách yêu thích");
                // Hiện label "Đã copy"
                saveSuccessfullyNotification.setVisible(true);

                /** ---------- hiệu ứng hiển thị pane thông báo ---------------- **/
                FadeTransition fadeIn = new FadeTransition(Duration.millis(200), saveSuccessfullyNotification);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);

                PauseTransition stay = new PauseTransition(Duration.seconds(1.5));

                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), saveSuccessfullyNotification);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);

                fadeOut.setOnFinished(e -> saveSuccessfullyNotification.setVisible(false));

                SequentialTransition sequence = new SequentialTransition(fadeIn, stay, fadeOut);
                sequence.play();
                /** ------------------------------------------------------------------------------------------------ **/
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Không thể thêm vào danh sách yêu thích");
            }
        }
    }
/* ================================================================================================================== */
}