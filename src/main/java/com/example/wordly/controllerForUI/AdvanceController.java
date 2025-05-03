package com.example.wordly.controllerForUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AdvanceController extends BaseController {
    @FXML public void BackToScence(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }
    @FXML public void GoToTTS(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/TranslateAndTTS.fxml");
    }
    @FXML public void GoToAnsAndSyn(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SynAndAntView.fxml");
    }
    @FXML public void GoToChatBot(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/ChatBot.fxml");
    }
}
