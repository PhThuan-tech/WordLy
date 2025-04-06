package com.example.wordly.controllerForUI;

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
import javafx.stage.Stage;

import java.io.IOException;

public class SearchViewController implements SearchUIUpdate{
    public Button searchButton;
    public TextField searchBar;
    public TextArea meaningText;
    public TextArea exampleText;
    public Label proLabel;
    public Label typeLabel;
    public Label statusLabel;

    //Goi API va xu li su kien
    private final GetAPI api = new GetAPI();
    private SearchButtonClickHandle searchHandle;

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
        this.searchHandle = new SearchButtonClickHandle( this, api);
        this.updateStatus("San sang tra tu");

        searchBar.setOnAction(this::handleSearchButtonOnClick);
    }

    /**
     * Thuc hien khi searchButton duoc an
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
        if (details != null) {
            proLabel.setText(details.getPhonetic());
            typeLabel.setText(details.getType());
            exampleText.setText(details.getExample());
            meaningText.setText(details.getDefinition());

            System.out.println(details.getAudioLink());
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
    }

    @Override
    public void resetSearchButton(boolean searching) {
        if (searchButton != null) {
            searchButton.setDisable(searching);
        }
    }
}
