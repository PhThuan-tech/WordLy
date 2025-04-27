package com.example.wordly.controllerForUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;


public class GameViewController extends BaseController {
    // Các phương thức chuyển giao diện.
    @FXML
    public void handleBackMain(ActionEvent actionEvent)  {
        switchScene(actionEvent, "/com/example/wordly/view/MainView.fxml");
    }

    @FXML
    public void handleGoToSearch(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }

    @FXML
    public void handleGoToFavourite(ActionEvent actionEvent)  {
        switchScene(actionEvent, "/com/example/wordly/View/FavouriteView.fxml");
    }

    @FXML
    public void handleGoToHistory(ActionEvent actionEvent)  {
        switchScene(actionEvent, "/com/example/wordly/View/HistoryView.fxml");
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
    public void handleGotoEditWord(ActionEvent actionEvent)  {
        switchScene(actionEvent, "/com/example/wordly/View/EditWordView.fxml");
    }

    public void GoToWordle(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/GameView/WordleView.fxml");
    }


    @FXML
    public void handleStartDefiGame(ActionEvent event) throws IOException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/GameView/DefinitionGame-View.fxml"));
        Parent DefinitionGame = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(DefinitionGame);
    }

    public void handleGoToScrambleWord(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/GameView/ScrambleGameView.fxml");
    }
}
