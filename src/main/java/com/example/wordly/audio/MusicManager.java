package com.example.wordly.audio;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.Random;

public class MusicManager {

    private static MusicManager instance;
    private MediaPlayer mediaPlayer;
    private final List<String> musicFiles = List.of(
            "/com/example/wordly/audio/Lofi1.mp3",
            "/com/example/wordly/audio/Lofi2.mp3",
            "/com/example/wordly/audio/Lofi3.mp3",
            "/com/example/wordly/audio/Lofi4.mp3"
    );
    private int currentTrackIndex = 0;
    private double savedVolume = 0.5;

    private MusicManager() {}

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public void playMusic() {
        if (mediaPlayer == null) {
            createMediaPlayer(currentTrackIndex);
        }
        mediaPlayer.play();
    }

    public void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void setVolume(double volume) {
        this.savedVolume = volume;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public double getSavedVolume() {
        return savedVolume;
    }

    private void createMediaPlayer(int index) {
        if (mediaPlayer != null) {
            fadeOut(mediaPlayer, () -> mediaPlayer.stop());
        }

        String path = musicFiles.get(index);
        URL mediaURL = getClass().getResource(path);
        if (mediaURL == null) {
            System.err.println("Không tìm thấy file: " + path);
            return;
        }

        Media media = new Media(mediaURL.toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0); // bắt đầu từ âm lượng 0 để fade in

        mediaPlayer.setOnEndOfMedia(() -> {
            nextTrack(); // tự động chuyển bài khi hết
        });

        mediaPlayer.setCycleCount(1);
        mediaPlayer.play();
        fadeIn(mediaPlayer); // fade in khi bắt đầu bài
    }

    public void nextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % musicFiles.size();
        createMediaPlayer(currentTrackIndex);
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void previousTrack() {
        currentTrackIndex = (currentTrackIndex - 1 + musicFiles.size()) % musicFiles.size();
        createMediaPlayer(currentTrackIndex);
    }

    // Fade In - mượt mà khi bắt đầu bài
    private void fadeIn(MediaPlayer player) {
        Timeline timeline = new Timeline();
        for (int i = 0; i <= 10; i++) {
            double vol = i / 10.0 * savedVolume;
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i * 100), e -> player.setVolume(vol))
            );
        }
        timeline.play();
    }

    // Fade Out - mượt mà khi chuyển bài
    private void fadeOut(MediaPlayer player, Runnable onFinished) {
        Timeline timeline = new Timeline();
        for (int i = 10; i >= 0; i--) {
            double vol = i / 10.0 * savedVolume;
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis((10 - i) * 100), e -> player.setVolume(vol))
            );
        }
        timeline.setOnFinished(e -> onFinished.run());
        timeline.play();
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
}
