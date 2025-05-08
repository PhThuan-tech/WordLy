package com.example.wordly.controllerForUI;

import com.example.wordly.Levenshtein.LevenshteinUtils;
import com.example.wordly.SQLite.FavouriteWordDAO;
import com.example.wordly.SQLite.NewAddedWordDAO;
import com.example.wordly.getWord.WordEntry;
import javafx.animation.ScaleTransition;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;


public class FavouriteController extends BaseController {
/* =================== PHƯƠNG THỨC CHUYỂN VIEW ====================================================================== */
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
    public void GoToAdvanceFeature(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/Advance_Features.fxml");
    }

/* ================================================================================================================== */



/* ======================= THAM CHIẾU TỪ VÀO BẢNG ========================================================================== */
    @FXML public TableView<WordEntry> tableView;
    @FXML public TableColumn<WordEntry, String> wordCol;
    @FXML public TableColumn<WordEntry, String> typeCol;
    @FXML public TableColumn<WordEntry, String> pronCol;
    @FXML public TableColumn<WordEntry, String> meaningCol;
    @FXML
    public void mappingTable() {
        wordCol.setCellValueFactory(new PropertyValueFactory<>("word"));
        pronCol.setCellValueFactory(new PropertyValueFactory<>("pronunciation"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        meaningCol.setCellValueFactory(new PropertyValueFactory<>("meaning"));

        tableView.setItems(wordList);
    }
/* ================================================================================================================== */




/* ======================= CHUYỂN ĐỔI VIEW ========================================================================== */
    @FXML private Button btnSwitchView;
    private boolean showFavouriteWords = true;

    private void updateSwitchbutton() {
        if (showFavouriteWords) {
            btnSwitchView.setText("Word List");
        } else {
            btnSwitchView.setText("Favourite List");
        }
    }

    private void toggleView() {
        if (showFavouriteWords) {
            loadWordsAdded();
        } else {
            loadFavourites();
        }
        showFavouriteWords = !showFavouriteWords;
        updateSwitchbutton();
    }

    private void playButtonAnimation() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), btnSwitchView);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.1);   st.setToY(1.1);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }
/* ================================================================================================================== */



/* ====================== HIỂN THỊ TỪ VÀO BẢNG ====================================================================== */
    private void loadFavourites() {
        wordList.clear();
        FavouriteWordDAO favouriteWordDAO = new FavouriteWordDAO();
        wordList.addAll(favouriteWordDAO.getAllFavourites());
    }

    private void loadWordsAdded() {
        wordList.clear();
        NewAddedWordDAO newAddedWordDAO = new NewAddedWordDAO();
        wordList.addAll(newAddedWordDAO.getAddedWords());
    }
/* ================================================================================================================== */



/* ============================= XỬ LÝ XÓA TỪ KHỎI BẢNG ============================================================= */
    @FXML private Button yesButton;
    @FXML private Button noButton;
    @FXML private AnchorPane confirmationDialog;
    @FXML private Button deleteButton;

    @FXML private void handleDeleteButton() {
        confirmationDialog.setVisible(true);
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

    private void deleteSelectedWord() {
        WordEntry selectedWord = tableView.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            FavouriteWordDAO favouriteWordDAO = new FavouriteWordDAO();
            favouriteWordDAO.removeFavouriteWord(selectedWord.getWord());
            wordList.remove(selectedWord);
        }
    }

/* ================================================================================================================== */



/* ============================== VIEW KHỞI TẠO ===================================================================== */
    @FXML private BorderPane rootPane;
    private ObservableList<WordEntry> wordList = FXCollections.observableArrayList();
    private FilteredList<WordEntry> filteredData;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        /** hiệu ứng nút bấm khi di chuột cho view **/
        applyHoverEffectToAllButtons(rootPane);
        /** ------------------------------------------------------------------------------- **/

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

        // filter tìm kiếm với levenshtein
        filteredData = new FilteredList<>(wordList, e -> true);
        tableView.setItems(filteredData);
        searchField.setOnKeyReleased(this::onSearchKeyReleased);

        // animation switch view
        btnSwitchView.setOnAction(e -> {
            playButtonAnimation();
            toggleView();
        });

        // load dữ liệu đầu vào
        loadFavourites();
        updateSwitchbutton();
    }

/* ================================================================================================================== */
}

