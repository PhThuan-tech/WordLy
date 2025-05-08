package com.example.wordly.controllerForUI;

import com.example.wordly.Levenshtein.LevenshteinUtils;
import com.example.wordly.SQLite.DictionaryDAO;
import com.example.wordly.SQLite.DictionaryViEnDAO;
import com.example.wordly.SQLite.NewAddedWordDAO;
import com.example.wordly.getWord.Trie;
import com.example.wordly.getWord.WordEntry;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
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
import java.util.stream.Collectors;

public class EditWordController extends BaseController {
/* =================== PHƯƠNG THỨC CHUYỂN VIEW ====================================================================== */
    @FXML public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/view/MainView.fxml");
    }
    @FXML public void handleGoToSearch(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }
    @FXML public void handleGoToFavourite(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/FavouriteView.fxml");
    }
    @FXML public void handleGotoGame(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml");
    }
    @FXML public void handleGoToHistory(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/HistoryView.fxml");
    }
    @FXML public void GoToAdvanceFeature(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/Advance_Features.fxml");
    }

/* ================================================================================================================== */





/* ============================= THÀNH PHẦN THUỘC TÍNH PHỤC VỤ CHỨC NĂNG ============================================ */
    @FXML public BorderPane rootPane;
    @FXML private ToggleButton addToggle;
    @FXML private ToggleButton deleteToggle;
    @FXML private ToggleGroup modeToggleGroup;

    @FXML private AnchorPane addModeContainer;
    @FXML private AnchorPane deleteModeContainer;

    // các phần của thêm từ
    @FXML private TextField txtWord, txtPronunciation, txtType;
    @FXML private TextArea txtDescription;
    @FXML private Button addButton;

    // các phần của xóa từ
    @FXML private TextField searchField;
    @FXML private Button deleteButton;
    @FXML private ListView<WordEntry> wordListView;
    @FXML private Button btnSwitchLang;

    // pane confirm thêm từ vào từ điển nào
    @FXML private StackPane confirmDictionaryPane;
    @FXML private Button EnViConfirmation;
    @FXML private Button ViEnConfirmation;

    // pane confirm xóa từ
    @FXML private StackPane confirmationOverlay;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    @FXML private ImageView imgFromLang, imgToLang;

    // thành phần phụ phục vụ chức năng
    private boolean isEnToVi = true; // mặc định tiếng anh
    private Trie trie = new Trie();
    private ObservableList<WordEntry> wordList = FXCollections.observableArrayList();
    private FilteredList<WordEntry> filteredData = new FilteredList<>(wordList, p -> true);
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> currentSearchTask;
    private PauseTransition pause = new PauseTransition(Duration.millis(300));

/* ================================================================================================================== */





/* ============================== PHƯƠNG THỨC KHỞI TẠO VIEW BAN ĐẦU ================================================== */
    private String wordToAdd, pronunciationToAdd, wordtypeToAdd, meaningToAdd;

    @FXML
    public void initialize() {
        applyHoverEffectToAllButtons(rootPane);
        // Khởi tạo ToggleGroup nếu chưa được khởi tạo trong FXML
        if (modeToggleGroup == null) {
            modeToggleGroup = new ToggleGroup();
            addToggle.setToggleGroup(modeToggleGroup);
            deleteToggle.setToggleGroup(modeToggleGroup);
        }

        // Ẩn các container và pane ban đầu
        addModeContainer.setVisible(true);
        deleteModeContainer.setVisible(false);
        confirmationOverlay.setVisible(false);
        confirmDictionaryPane.setVisible(false);

        // Thiết lập mặc định là chế độ thêm từ
        addToggle.setSelected(true);

        // Thiết lập sự kiện cho các toggle button
        addToggle.setOnAction(event -> switchToAddMode());
        deleteToggle.setOnAction(event -> switchToDeleteMode());

        // Thiết lập các sự kiện cho các nút
        addButton.setOnAction(event -> handleAddButton());
        EnViConfirmation.setOnAction(event -> handleAddToDictionary("en-vi"));
        ViEnConfirmation.setOnAction(event -> handleAddToDictionary("vi-en"));
        btnSwitchLang.setOnAction(event -> switchLanguage());
        deleteButton.setOnAction(this::handleDeleteButton);

        // Khởi tạo giao diện ban đầu và tìm kiếm
        setupInitialUI();
        setupSearchFunctionality();
    }

    @FXML
    private void switchToAddMode() {
        addModeContainer.setVisible(true);
        deleteModeContainer.setVisible(false);

        // Enable add mode controls
        txtWord.setDisable(false);
        txtPronunciation.setDisable(false);
        txtType.setDisable(false);
        txtDescription.setDisable(false);
        addButton.setDisable(false);

        // Disable delete mode controls
        searchField.setDisable(true);
        deleteButton.setDisable(true);
    }

    @FXML
    private void switchToDeleteMode() {
        addModeContainer.setVisible(false);
        deleteModeContainer.setVisible(true);

        // Enable delete mode controls
        searchField.setDisable(false);
        deleteButton.setDisable(false);

        // Disable add mode controls
        txtWord.setDisable(true);
        txtPronunciation.setDisable(true);
        txtType.setDisable(true);
        txtDescription.setDisable(true);
        addButton.setDisable(true);

        // Load words for delete mode if needed
        loadWordsToListView();
    }

    private void setupInitialUI() {
        // Đặt prompt text cho search field
        searchField.setPromptText(isEnToVi ? "Search here" : "Tìm kiếm ở đây");

        // Tải danh sách từ
        loadWordsToListView();

        // Cấu hình cell factory cho ListView
        wordListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(WordEntry item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getWord());
            }
        });

        // Khởi tạo filtered data
        filteredData = new FilteredList<>(wordList, e -> true);
        wordListView.setItems(filteredData);
    }

/* ================================================================================================================== */





/* ============================= PHẦN XỬ LÝ THÊM TỪ ========================================================= */
    @FXML
    private void handleAddButton() {
        wordToAdd = txtWord.getText().trim();
        pronunciationToAdd = txtPronunciation.getText().trim();
        wordtypeToAdd = txtType.getText().trim();
        meaningToAdd = txtDescription.getText().trim();

        if (wordToAdd.isEmpty() || meaningToAdd.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập từ và nghĩa.");
            return;
        }

        confirmDictionaryPane.setVisible(true); // Hiện pane xác nhận từ điển
    }

    @FXML
    private void handleAddToDictionary(String dictionaryType) {
        boolean exists = false;
        boolean success = false;
        String fullMeaning = "*" + wordtypeToAdd + "\n" + pronunciationToAdd +"\n" + meaningToAdd;

        if (dictionaryType.equals("en-vi")) {
            DictionaryDAO dao = new DictionaryDAO();
            NewAddedWordDAO newAddedWordDAO = new NewAddedWordDAO();
            exists = dao.isInDictionary(wordToAdd);
            if (!exists) {
                success = dao.addToDictionary(wordToAdd, pronunciationToAdd, fullMeaning);
                success = newAddedWordDAO.addNewWord(wordToAdd, pronunciationToAdd, wordtypeToAdd, meaningToAdd);
            }
        } else if (dictionaryType.equals("vi-en")) {
            DictionaryViEnDAO dao = new DictionaryViEnDAO();
            NewAddedWordDAO newAddedWordDAO = new NewAddedWordDAO();
            exists = dao.isInDictionaryViEn(wordToAdd);
            if (!exists) {
                success = dao.addToDictionaryViEn(wordToAdd, fullMeaning);
                success = newAddedWordDAO.addNewWord(wordToAdd, pronunciationToAdd, wordtypeToAdd, meaningToAdd);
            }
        }

        confirmDictionaryPane.setVisible(false);

        if (exists) {
            showAlert("Từ đã tồn tại", "Từ này đã có trong từ điển " + (dictionaryType.equals("en-vi") ? "Anh-Việt" : "Việt-Anh") + ".");
        } else if (success) {
            showAlert("Thành công", "Đã thêm từ vào từ điển " + (dictionaryType.equals("en-vi") ? "Anh-Việt" : "Việt-Anh") + ".");
            clearInputFields();
        } else {
            showAlert("Thất bại", "Có lỗi xảy ra khi thêm từ.");
        }
    }

/* ================================================================================================================== */





/* =============================== PHẦN XỬ LÝ XÓA TỪ ================================================================ */
    // PHƯƠNG THỨC XÓA TỪ KHỎI DATABASE
    @FXML
    private void handleDeleteButton(ActionEvent event) {
        WordEntry selectedWord = wordListView.getSelectionModel().getSelectedItem();

        if (selectedWord == null) {
            showAlert("Xóa từ", "Vui lòng chọn từ để xóa.");
            return;
        }

        confirmationOverlay.setVisible(true);

        confirmButton.setOnAction(confirmEvent -> {
            boolean isViEn = !isEnToVi;

            boolean deleteSuccess;
            boolean deleteOutOfAddedWord;
            DictionaryViEnDAO dictionaryViEnDAO = new DictionaryViEnDAO();
            DictionaryDAO dictionaryDAO = new DictionaryDAO();
            NewAddedWordDAO newAddedWordDAO = new NewAddedWordDAO();

            if (isViEn) {
                // Xóa từ khỏi database Vi-En
                deleteSuccess = dictionaryViEnDAO.removeFromDictionaryViEn(selectedWord.getWord());
                deleteOutOfAddedWord = newAddedWordDAO.removeAddedWord(selectedWord.getWord());
            } else {
                // Xóa từ khỏi database En-Vi
                deleteSuccess = dictionaryDAO.removeFromDictionary(selectedWord.getWord());
                deleteOutOfAddedWord = newAddedWordDAO.removeAddedWord(selectedWord.getWord());
            }

            if (deleteSuccess) {
                wordList.remove(selectedWord); // Quan trọng: Cập nhật nguồn dữ liệu gốc
                filteredData.remove(selectedWord);
                showAlert("Thông báo", "Xóa từ thành công.");
                searchField.clear();
            } else {
                showAlert("Lỗi", "Không thể xóa từ. Vui lòng thử lại.");
            }
            confirmationOverlay.setVisible(false);
        });
        // Thiết lập sự kiện cho nút hủy
        cancelButton.setOnAction(cancelEvent -> {
            confirmationOverlay.setVisible(false);
        });
    }

    // SETUP TÁK CHO CHỨC NĂNG TÌM KIẾM TỪ CẦN XÓA
    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Hủy task tìm kiếm hiện tại nếu có
            if (currentSearchTask != null && !currentSearchTask.isDone()) {
                currentSearchTask.cancel(true);
            }
            pause.setOnFinished(e -> {
                String keyword = newVal.trim().toLowerCase();
                filteredData.setPredicate(wordEntry -> {
                    if (keyword.isEmpty()) return true;
                    return wordEntry.getWord().toLowerCase().contains(keyword);
                });
                if (deleteToggle.isSelected() && !keyword.isEmpty()) {
                    performSearch(keyword);
                }
            });

            pause.playFromStart();
        });
    }

    // PHƯƠNG THỨC CHUYỂN DATABASE CHO NÚT CHUYỂN
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

        // Chuyển động sang ngang
        TranslateTransition moveFrom = new TranslateTransition(duration, imgFromLang);
        moveFrom.setByX(imgToLang.getLayoutX() - imgFromLang.getLayoutX());

        TranslateTransition moveTo = new TranslateTransition(duration, imgToLang);
        moveTo.setByX(imgFromLang.getLayoutX() - imgToLang.getLayoutX());

        // Chạy song song các hiệu ứng
        ParallelTransition parallelFadeOut = new ParallelTransition(fadeOutFrom, fadeOutTo);

        // Sau khi hiệu ứng mờ dần hoàn thành
        parallelFadeOut.setOnFinished(e -> {
            // Hoán đổi hình ảnh
            Image temp = imgFromLang.getImage();
            imgFromLang.setImage(imgToLang.getImage());
            imgToLang.setImage(temp);

            // Reset vị trí của các ImageView
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
                // Load từ điển tương ứng khi hiệu ứng hoàn thành
                loadDictionary();
            });

            parallelFadeIn.play();
        });

        parallelFadeOut.play();
    }

    // LOAD TỪ ĐIỂN VỚI DATABASE TƯƠNG ỨNG
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

    // CẬP NHẬT TỪ TỪ DATABSE VÀO LISTVIEW
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

    // ỨNG DỤNG TRIE VÀ LEVENSHTEIN TRONG TÌM KIẾM TỪ
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

        // Gửi task cho Executor để thực thi bất đồng bộ
        currentSearchTask = executor.submit(searchTask);
    }

/* ================================================================================================================== */





/* ========================= PHẦN PHỤ TRỢ VIEW ====================================================================== */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearInputFields() {
        txtWord.clear();
        txtPronunciation.clear();
        txtType.clear();
        txtDescription.clear();
    }

/* ================================================================================================================== */
}