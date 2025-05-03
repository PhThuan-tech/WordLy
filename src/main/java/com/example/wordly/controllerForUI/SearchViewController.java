package com.example.wordly.controllerForUI;

import com.example.wordly.History.HistoryManage;
import com.example.wordly.SQLite.FavouriteWordDAO;
import com.example.wordly.getWord.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Objects;
// kế thừa lớp BaseController và implements interface SearchUIUpdate.

public class SearchViewController extends BaseController implements SearchUIUpdate {
    public Button searchButton;
    public TextField searchBar;
    public TextArea meaningText;
    public TextArea exampleText;
    public Label proLabel;
    public Label typeLabel;
    public Label statusLabel;
    public Button speakButton;

    @FXML
    private BorderPane rootPane;

    @FXML
    private ListView<String> suggestionList;  // them cai autocomplete dung trie

    private SearchButtonClickHandle searchHandle;
    private WordDetails currDetails;
    private MediaPlayer activeMedia;
    private final Trie trie = new Trie();
    private final HistoryManage historyManager = new HistoryManage();

    @FXML
    public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/view/MainView.fxml");
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
    public void handleGoToTranslateAndTTS(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/TranslateAndTTS.fxml");
    }

    @FXML
    public void handleGoToChat(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/ChatBot.fxml");
    }

    @FXML
    public void handleGoToSynAndAnt(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SynAndAntView.fxml");
    }

    @FXML
    public void initialize() {
        // Khoi tao neu SBCH can no
        getClass().getResource("/com/example/wordly/ListOfWord");
        GetAPI apiInstance = new GetAPI();
        this.searchHandle = new SearchButtonClickHandle(this, apiInstance);
        this.updateStatus("Sẵn sàng tra từ,Bro chọn từ khó vô để tôi tìm");

        changeUI();

        loadWordFromTextFile();

        if (speakButton != null) {
            speakButton.setDisable(true);
        }

        applyHoverEffectToAllButtons(rootPane);
    }

    private void loadWordFromTextFile() {
        // load tu trong file txt =))
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("/com/example/wordly/ListOfWord"))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                trie.insert(line.trim().toLowerCase());
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Không load được ListOfWord: " + e.getMessage());
            updateStatus("Lỗi: Không load được dữ liệu từ điển.");
        }
    }

    private void changeUI() {

        // phan mo rong cho SearchBar - cang qua fix bug tum lum.(ko dung sql lite va database)
        searchBar.setOnAction(this::handleSearchButtonOnClick);
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isBlank()) {
                suggestionList.setVisible(false);
                suggestionList.getItems().clear();
            } else {
                List<String> suggestions = trie.getSuggestions(newValue.toLowerCase());
                if (!suggestions.isEmpty()) {
                    suggestionList.setItems(FXCollections.observableArrayList(
                            suggestions.subList(0, Math.min(suggestions.size(), 10))
                    ));
                    suggestionList.setVisible(true);
                } else {
                    suggestionList.setVisible(false);
                }
            }
        });

        suggestionList.setOnMouseClicked(event -> {
            String selected = suggestionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                searchBar.setText(selected);
                suggestionList.setVisible(false);
                getSearchTerm(); // gọi hàm tìm kiếm
            }
        });

        searchBar.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DOWN:
                    suggestionList.requestFocus();
                    suggestionList.getSelectionModel().selectFirst();
                    break;
                case ENTER:
                    getSearchTerm();
                    suggestionList.setVisible(false);
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * Thuc hien khi searchButton duoc an.
     *
     * @param e ac
     */
    @FXML
    public void handleSearchButtonOnClick(ActionEvent e) {
        if (searchHandle != null) {
            searchHandle.handleSearch();
        }
    }

    @Override
    public String getSearchTerm() {
        return searchBar.getText().trim();
    }

    @Override
    public void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    @Override
    public void displayResult(WordDetails details) throws IOException {
        this.currDetails = details;
        if (details != null) {
            // Hiển thị kết quả của Lịch sử lưu vào game và history
            proLabel.setText(details.getPhonetic());
            typeLabel.setText(details.getType());
            exampleText.setText(details.getExample());
            meaningText.setText(details.getDefinition());
            System.out.println(details.getAudioLink());

            historyManager.saveToHistory(details);
            historyManager.saveToGame(details);

            String searchedWord = details.getWord();
            historyManager.saveWordToTrieFile(details.getWord());

            String wordForTrie = searchedWord.trim().toLowerCase();
            if (wordForTrie.matches("^[a-z]+$")) {
                this.trie.insert(wordForTrie);
                System.out.println("Đã thêm từ '" + wordForTrie + "' vào Trie trong bộ nhớ.");
            } else {
                System.err.println("Không thêm từ '" + searchedWord + "' vào Trie trong bộ nhớ do chứa ký tự không hợp lệ.");
            }

            boolean audio = details.getAudioLink() != null && !details.getAudioLink().trim().isEmpty();
            if (speakButton != null) {
                speakButton.setDisable(!audio);
            }
        } else {
            clearResult();
        }
    }

    @Override
    public void clearResult() {
        proLabel.setText("N/A");
        typeLabel.setText("N/A");
        meaningText.setText("");
        exampleText.setText("");

        this.currDetails = null;
        speakButton.setDisable(true);
    }

    @Override
    public void resetSearchButton(boolean searching) {
        if (searchButton != null) {
            searchButton.setDisable(searching);
        }
    }

    @FXML
    void handleSpeakButtonOnAction(ActionEvent e) {
        System.out.println("Dang o handleSpeakButtonOnAction");
        String audioURL = currDetails.getAudioLink();
        playAudio(audioURL);
    }

    /**
     * Lấy ra URL của audio cần tìm.
     *
     * @param audioURL trả về encoded của URL.
     */
    public void playAudio(String audioURL) {
        updateStatus("Đang tập nói nè cha");

        try {
            stopCurrPlayer(); // Dung cai dang nghe
            if (audioURL == null || audioURL.isEmpty()) {
                updateStatus("Không thấy đường dẫn");
                return;
            }
            Media media = new Media(audioURL);
            activeMedia = new MediaPlayer(media);
            //Neu xay ra loi
            activeMedia.setOnError(() -> {
                updateStatus("Lỗi rồi má ơi" + activeMedia.getError().getMessage());
                System.out.println(activeMedia.getError().getMessage());
            });

            //Chuan bi phat am thanh
            activeMedia.setOnReady(() -> {
                updateStatus("Đang phát ~.~ CHILL GUY");
                activeMedia.play();
            });

            //khi phat xong
            activeMedia.setOnEndOfMedia(() -> {
                updateStatus("Mời mấy bác thưởng thức");
                stopCurrPlayer();
            });
        } catch (Exception e) {
            updateStatus("Lỗi " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopCurrPlayer() {
        if (activeMedia != null) {
            try {
                activeMedia.stop();
                activeMedia.dispose();
                System.out.println("Nghe thích chưa");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                activeMedia = null;
            }
        }
    }

    @FXML
    private void handleAddToFavourite() {
        String word = searchBar.getText();
        String type = typeLabel.getText();
        String pronunciation = proLabel.getText();
        String meaning = meaningText.getText();

        if (word.isEmpty()) {
            System.out.println("Không thể thêm từ");
            return;
        }

        FavouriteWordDAO newWord = new FavouriteWordDAO();
        if (newWord.isFavouriteWord(word)) {
            System.out.println("Từ này đã có trong danh sách yêu thích");
        } else {
            try {
                newWord.addFavouriteWord(word, type, pronunciation, meaning);
                System.out.println("Đã thêm từ " + word + " vào danh sách yêu thích");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Không thể thêm vào danh sách yêu thích");
            }
        }
    }
}