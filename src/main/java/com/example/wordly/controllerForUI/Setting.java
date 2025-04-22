package com.example.wordly.controllerForUI;

import com.example.wordly.audio.MusicManager;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

public class Setting extends BaseController {

    @FXML private CheckBox musicToggle;
    @FXML private Slider volumeSlider;
    @FXML private Button nextBtn, prevBtn;

    @FXML
    public void initialize() {
        MusicManager musicManager = MusicManager.getInstance();

        // Load volume đã lưu
        volumeSlider.setValue(musicManager.getSavedVolume());

        // ✅ Đồng bộ trạng thái CheckBox với tình trạng nhạc
        musicToggle.setSelected(musicManager.isPlaying());

        // ✅ Đồng bộ volume slider với giá trị đã lưu
        volumeSlider.setValue(musicManager.getSavedVolume());

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            musicManager.setVolume(newVal.doubleValue());
        });

        musicToggle.setOnAction(e -> {
            if (musicToggle.isSelected()) {
                musicManager.playMusic();
            } else {
                musicManager.pauseMusic();
            }
        });

        nextBtn.setOnAction(e -> musicManager.nextTrack());
        prevBtn.setOnAction(e -> musicManager.previousTrack());
    }

    @FXML
    public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/MainView.fxml");
    }
}