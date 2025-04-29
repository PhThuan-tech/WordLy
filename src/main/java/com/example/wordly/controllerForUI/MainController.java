package com.example.wordly.controllerForUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.AudioClip;
import java.net.URL; // Import URL

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
     * Và tải âm thanh.
     */
    public void initialize() {
        if (menuScence != null) {
            Scene scene = menuScence.getScene();
            if (scene != null) {
                scene.getStylesheets().add(getClass()
                        .getResource("/com/example/wordly/styles/mainsce.css").toExternalForm());
            }
        } else {
            System.out.println("FXML elements chưa được load đầy đủ khi initialize MainController.");
        }

        URL audioResource = getClass().getResource("/com/example/wordly/audio/TrollButton.mp3");
        if (audioResource != null) {
            clickSound = new AudioClip(audioResource.toString());
        } else {
            System.err.println("Không tìm thấy tài nguyên âm thanh: /com/example/wordly/audio/TrollButton.mp3");
        }
    }

    public void switchToHomeScence(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }

    public void handleGoToUsing(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/UsingMehodView.fxml");
    }

    public void handleGoToSetting(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SettingView.fxml");
    }

    @FXML
    public void HandlePlaysound(ActionEvent actionEvent) {
        // Phát âm thanh
        if (clickSound != null) {
            clickSound.stop();
            clickSound.play();   
        } else {
            System.err.println("Âm thanh chưa được load hoặc không tìm thấy tài nguyên âm thanh!");
        }
    }
}