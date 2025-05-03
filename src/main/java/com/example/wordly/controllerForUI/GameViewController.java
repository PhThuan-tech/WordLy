package com.example.wordly.controllerForUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;


public class GameViewController extends BaseController {
    // Các phương thức chuyển giao diện.
    @FXML public void handleBackMain(ActionEvent actionEvent)  {
        switchScene(actionEvent, "/com/example/wordly/view/MainView.fxml");
    }
    @FXML public void handleGoToSearch(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }
    @FXML public void handleGoToFavourite(ActionEvent actionEvent)  {
        switchScene(actionEvent, "/com/example/wordly/View/FavouriteView.fxml");
    }
    @FXML public void handleGoToHistory(ActionEvent actionEvent)  {
        switchScene(actionEvent, "/com/example/wordly/View/HistoryView.fxml");
    }
    @FXML public void handleGotoEditWord(ActionEvent actionEvent)  {
        switchScene(actionEvent, "/com/example/wordly/View/EditWordView.fxml");
    }
    @FXML public void GoToAdvanceFeature(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/Advance_Features.fxml");
    }

    @FXML
    public void GoToWordle(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/GameView/WordleView.fxml");
    }
    @FXML
    private BorderPane rootPane;

    public void initialize() {
        applyHoverEffectToAllButtons(rootPane);
    }


    @FXML
    public void handleGoToScrambleWord(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/GameView/ScrambleGameView.fxml");
    }

    @FXML
    public void handleStartDefiGame(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/GameView/DefinitionGame-View.fxml");
    }
}
