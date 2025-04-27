package com.example.wordly.GameController.ScrambleWord;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class AudioManager {
    private MediaPlayer winSoundPlayer;
    private MediaPlayer loseSoundPlayer;

    public AudioManager() {
        loadSounds();
    }

    private void loadSounds() {
        try {
            // Lấy đường dẫn đến file âm thanh thắng
            URL winSoundUrl = getClass().getResource("/com/example/wordly/audio/WinGame.mp3");
            if (winSoundUrl != null) {
                Media winSoundMedia = new Media(winSoundUrl.toString());
                winSoundPlayer = new MediaPlayer(winSoundMedia);
            } else {
                System.err.println("Không tìm thấy file âm thanh thắng");
            }

            // Lấy đường dẫn đến file âm thanh thua
            URL loseSoundUrl = getClass().getResource("/com/example/wordly/audio/LoseGame.mp3");
            if (loseSoundUrl != null) {
                Media loseSoundMedia = new Media(loseSoundUrl.toString());
                loseSoundPlayer = new MediaPlayer(loseSoundMedia);
            } else {
                System.err.println("Không tìm thấy file âm thanh thua: lose_sound.mp3");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tải âm thanh.");
        }
    }

    public void playWinSound() {
        if (winSoundPlayer != null) {
            // Dừng âm thanh hiện tại nếu đang phát (để tránh chồng chéo)
            winSoundPlayer.stop();
            // Phát âm thanh
            winSoundPlayer.play();
        }
    }

    public void playLoseSound() {
        if (loseSoundPlayer != null) {
            // Dừng âm thanh hiện tại nếu đang phát
            loseSoundPlayer.stop();
            // Phát âm thanh
            loseSoundPlayer.play();
        }
    }

    // Phương thức để dừng tất cả âm thanh (hữu ích khi chuyển scene)
    public void stopAllSounds() {
        if (winSoundPlayer != null) {
            winSoundPlayer.stop();
        }
        if (loseSoundPlayer != null) {
            loseSoundPlayer.stop();
        }
    }
}
