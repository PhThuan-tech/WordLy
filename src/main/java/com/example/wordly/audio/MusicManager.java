package com.example.wordly.audio;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;

public class MusicManager {

    private static MusicManager instance;
    private MediaPlayer mediaPlayer;
    // Hmm ghi vao file txt ko duoc nen la dung kieu List final cho on dinh
    private final List<String> musicFiles = List.of(
            "/com/example/wordly/audio/Lofi1.mp3",
            "/com/example/wordly/audio/Lofi2.mp3",
            "/com/example/wordly/audio/Lofi3.mp3",
            "/com/example/wordly/audio/Lofi4.mp3",
            "/com/example/wordly/audio/Loifi5.mp3",
            "/com/example/wordly/audio/Lofi6.mp3",
            "/com/example/wordly/audio/Lofi7.mp3",
            "/com/example/wordly/audio/BanhMi.mpeg"         // easter egg=)) nghe 2-3 lan la trung
    );
    private int currentTrackIndex = 0;      // index la bai hat dang chay, dung cho nut next va previous
    private double savedVolume = 0.5;       // Volume khi chuyen bai se mac dinh la 0.5

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

    // fix loi khi chuyen giao dien thi ko co nhac
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    // chinh am luong cua am thanh
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
        MediaPlayer oldPlayer = this.mediaPlayer;

        String path = musicFiles.get(index);
        URL mediaURL = getClass().getResource(path);
        if (mediaURL == null) {
            System.err.println("Không tìm thấy file: " + path);
            // Đảm bảo biến mediaPlayer được set null nếu không load được bài hát
            this.mediaPlayer = null;
            // Có thể xử lý lỗi ở đây, ví dụ: thử bài tiếp theo hoặc dừng phát
            return;
        }

        Media media = new Media(mediaURL.toExternalForm());
        this.mediaPlayer = new MediaPlayer(media);
        this.mediaPlayer.setVolume(0);

        this.mediaPlayer.setOnEndOfMedia(() -> {
            nextTrack();
        });

        this.mediaPlayer.setCycleCount(1);
        this.mediaPlayer.play();
        fadeIn(this.mediaPlayer);
        if (oldPlayer != null) {
            fadeOut(oldPlayer, () -> {
                oldPlayer.stop();
            });
        }
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

    // Khi chuyen bai am thanh no bi de nen la dung cai nay de dung doan cuoi r sang bai khac
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

    // Tuong tu fadeIn
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
}
