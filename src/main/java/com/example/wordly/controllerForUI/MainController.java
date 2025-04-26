package com.example.wordly.controllerForUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.AudioClip;

public class MainController extends BaseController {
    @FXML
    public Button menuScence;

    @FXML
    public Button HowToUse;

    @FXML
    public Button features;

    private AudioClip clickSound;

    /**
     * Hàm khởi tạo CSS vào trong MainController.
     */
    public void initialize() {
        if (menuScence != null) {
            Scene scene = menuScence.getScene();
            if (scene != null) {
                scene.getStylesheets().add(getClass()
                        .getResource("/com/example/wordly/styles/mainsce.css").toExternalForm());
            }
        }
        clickSound = new AudioClip(getClass().getResource("/com/example/wordly/audio/TrollButton.mp3").toString());
    }

    // Các Hàm chuyển đổi giữa các giao diện.
    public void switchToHomeScence(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }

    public void handleGoToUsing(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/UsingMehodView.fxml");
    }


    @FXML
    public void HandlePlaysound(ActionEvent actionEvent) {
        // Phát âm thanh
        if (clickSound != null) {
            if (clickSound.isPlaying()) {
                clickSound.stop();
            } else {
                clickSound.play();
            }
        } else {
            System.out.println("Âm thanh chưa được load!");
        }
    }

    public void handleGoToSetting(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SettingView.fxml");
    }
}
