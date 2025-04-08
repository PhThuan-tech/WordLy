package com.example.wordly.controllerForUI;

import com.example.wordly.History.HistoryManage;
import com.example.wordly.getWord.GetAPI;
import com.example.wordly.getWord.SearchButtonClickHandle;
import com.example.wordly.getWord.SearchUIUpdate;
import com.example.wordly.getWord.WordDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;

public class SearchViewController implements SearchUIUpdate {
    //Goi API va xu li su kien
    public Button searchButton;
    public TextField searchBar;
    public TextArea meaningText;
    public TextArea exampleText;
    public Label proLabel;
    public Label typeLabel;
    public Label statusLabel;
    public Button speakButton;
    private SearchButtonClickHandle searchHandle;

    private WordDetails currDetails;
    private MediaPlayer activeMedia;

    @FXML
    public void handleBackMain(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/view/MainView.fxml"));
        Parent mainView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(mainView);
    }

    // chuyen cac giao dien :v dai vai sua o cac giao dien khac
    public void handleGoToFavourite(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/FavouriteView.fxml"));
        Parent favouriteView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(favouriteView);
    }


    @FXML
    public void handleGoToHistory(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/HistoryView.fxml"));
        Parent historyView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(historyView);
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

    @FXML
    public void initialize() {

        // Khoi tao neu SBCH can no
        GetAPI apiInstance = new GetAPI();
        this.searchHandle = new SearchButtonClickHandle(this, apiInstance);
        this.updateStatus("San sang tra tu");

        searchBar.setOnAction(this::handleSearchButtonOnClick);

        if (speakButton != null) {
            speakButton.setDisable(true);
        }
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
    public void displayResult(WordDetails details) {
        this.currDetails = details;

        if (details != null) {
            proLabel.setText(details.getPhonetic());
            typeLabel.setText(details.getType());
            exampleText.setText(details.getExample());
            meaningText.setText(details.getDefinition());

            System.out.println(details.getAudioLink());
            HistoryManage hm = new HistoryManage();
           try {
               hm.saveToHistory(details);
           }catch (IOException e) {
               System.err.println(e.getMessage());
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


    // ========================================
    // =========XU LI NUT NGHE===============
    //========================================

    @FXML
    void handleSpeakButtonOnAction(ActionEvent e) {
        System.out.println("Dang o handleSpeakButtonOnAction");
        String audioURL = currDetails.getAudioLink();
        playAudio(audioURL);
    }

    /**
     * Phuong thuc nghe.
     *
     * @param audioURL link mp3
     */
    public void playAudio(String audioURL) {
        updateStatus("Dang tap noi");

        try {
            stopCurrPlayer(); // Dung cai dang nghe

            if (audioURL == null || audioURL.isEmpty()) {
                updateStatus("Khong thay duong dan am thanh");
                return;
            }

            Media media = new Media(audioURL);
            activeMedia = new MediaPlayer(media);

            //Neu xay ra loi
            activeMedia.setOnError(() -> {
                updateStatus("Loi" + activeMedia.getError().getMessage());
                System.out.println(activeMedia.getError().getMessage());
            });

            //Chuan bi phat am thanh
            activeMedia.setOnReady(() -> {
                updateStatus("Dang phat");
                activeMedia.play();
            });

            //khi phat xong
            activeMedia.setOnEndOfMedia(() -> {
                updateStatus("Nghe xong roi nhe");
                stopCurrPlayer();
            });
        } catch (Exception e) {
            updateStatus("Loi " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ham de dung nghe
     */
    private void stopCurrPlayer() {
        if (activeMedia != null) {
            try {
                activeMedia.stop();
                activeMedia.dispose();
                System.out.println("Nghe thich chua");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                activeMedia = null;
            }
        }
    }
}
