package com.example.wordly.controllerForUI;

import com.example.wordly.History.HistoryManage;
import com.example.wordly.SQLite.FavouriteWordDAO;
import com.example.wordly.getWord.GetAPI;
import com.example.wordly.getWord.SearchButtonClickHandle;
import com.example.wordly.getWord.SearchUIUpdate;
import com.example.wordly.getWord.WordDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


import java.io.*;
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
    private SearchButtonClickHandle searchHandle;

    private WordDetails currDetails;
    private MediaPlayer activeMedia;

    // Dùng kế thừa và tái sử dụng trông sang hẳn =))
    // lỗi cũng ít hơn thôi mấy ôg, đỡ copy nhiều phần ko quan trọng.
    @FXML
    public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/view/MainView.fxml");
    }

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
               hm.saveToGame(details);
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


    /**
     * Hàm sử lí hành động khi bấm nút speak.
     * @param e tạo hành động cần thực hiện.
     */
    @FXML
    void handleSpeakButtonOnAction(ActionEvent e) {
        System.out.println("Dang o handleSpeakButtonOnAction");
        String audioURL = currDetails.getAudioLink();
        playAudio(audioURL);
    }

    /**
     * Lấy ra URL của audio cần tìm.
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

    /**
     * Hàm để nghe từ cần nghe.
     */
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
        String pronunciation =proLabel.getText();
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
                System.out.println("Đã thêm từ" + word + "vào danh sách yêu thích");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Không thể thêm vào danh sách yêu thích");
            }
        }
    }
}
