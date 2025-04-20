package com.example.wordly.controllerForUI;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.List;
import java.util.Random;

public class Setting extends BaseController {

    @FXML private CheckBox musicToggle;
    @FXML private Slider volumeSlider;

    private MediaPlayer mediaPlayer;
    private MediaPlayer currentPlayer;
    // Danh sách file nhạc
    private final List<String> musicFiles = List.of(
            "/com/example/wordly/audio/Lofi1.mp3",
            "/com/example/wordly/audio/Lofi2.mp3",
            "/com/example/wordly/audio/Lofi3.mp3",
            "/com/example/wordly/audio/Lofi4.mp3"
    );

    @FXML
    public void initialize() {
        mediaPlayer = createRandomMediaPlayer();
        mediaPlayer.setVolume(volumeSlider.getValue());

        musicToggle.setOnAction(e -> {
            if (musicToggle.isSelected()) {
                mediaPlayer.play();
            } else {
                mediaPlayer.pause();
            }
        });

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            mediaPlayer.setVolume(newVal.doubleValue());
        });
    }

    private MediaPlayer createRandomMediaPlayer() {
        Random rand = new Random();
        String randomPath = musicFiles.get(rand.nextInt(musicFiles.size()));

        // Đảm bảo đường dẫn chính xác
        URL mediaURL = getClass().getResource(randomPath);

        if (mediaURL == null) {
            System.err.println("Không tìm thấy file: " + randomPath);
            return null;
        }

        Media media = new Media(mediaURL.toExternalForm());
        currentPlayer = new MediaPlayer(media);

        currentPlayer.setOnEndOfMedia(() -> {
            // Dừng bài hát cũ
            currentPlayer.stop();

            // Tạo MediaPlayer mới và phát bài tiếp theo
            createRandomMediaPlayer().play();
        });

        currentPlayer.setCycleCount(1);  // Đảm bảo bài hát không lặp lại vô hạn
        currentPlayer.play();  // Phát bài hát ngay lập tức
        return currentPlayer;
    }

    public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/MainView.fxml");
    }

    public void pauseAudio() {
        if (currentPlayer != null) {
            currentPlayer.pause();
        }
    }
}
