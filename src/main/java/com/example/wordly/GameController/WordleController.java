package com.example.wordly.GameController;

import com.example.wordly.controllerForUI.BaseController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.media.AudioClip;

public class WordleController extends BaseController {
    @FXML private GridPane grid;
    @FXML private TextField inputField;
    @FXML private Label messageLabel;

    // Nút bấm xử lí hành động gợi ý.
    @FXML private Button hintButton;

    // label hiển thị gợi ý từ.
    @FXML private Label hintLabel;

    // Label đếm số giây chạy còn lại
    @FXML private Label timerLabel;


    private  final int WORD_LENGTH = 5;
    private final int MAX_ATTEMPTS = 6;
    private final int MAX_HINTS = 2;
    private List<String> wordList;
    private String secretWord;
    private int currentAttempt = 0;

    // Hiển thị vị trí đoán đúng.
    private boolean[] revealedPositions = new boolean[WORD_LENGTH];
    private int hintCount = 0;

    // Biến xử lí đếm ngược thời gian.
    private int secondsElapsed = 0;
    private Timeline timeline;

    // Biến xử lí tính điểm
    private int score = 0;


    /**
     * 2 chức năng chính của game là load từ khi bắt đầu và resetgame.
     */
    public void initialize() {
        wordList = loadWordList();
        resetGame();
    }


    /**
     * Reset game khi từ mình đoán đúng.
     */
    private void resetGame() {
        currentAttempt = 0;
        hintCount = 0;
        revealedPositions = new boolean[WORD_LENGTH];

        secretWord = wordList.get(new Random().nextInt(wordList.size()));   // chọn ngẫu nhiên kiểu String
        System.out.println("🤫 Từ bí mật: " + secretWord);                  // =)) ko hiểu sao lỗi chỗ này nữa thiếu là ko tìm được kết quả.

        grid.getChildren().clear();
        messageLabel.setText("");
        inputField.setDisable(false);
        inputField.clear();
        hintButton.setDisable(false);

        // Đặt các chữ cái vào từng ô ( Mảng 2 chiều kích thước 5 x 6 )
        for (int row = 0; row < MAX_ATTEMPTS; row++) {
            for (int col = 0; col < WORD_LENGTH; col++) {
                Label cell = createCell();
                grid.add(cell, col, row);
            }
        }
        fetchHint(secretWord);
        startTimer();
    }

    private Label createCell() {
        Label cell = new Label("");
        cell.setPrefSize(50, 50);
        cell.setFont(Font.font(20));
        cell.getStyleClass().add("cell");
        cell.setAlignment(javafx.geometry.Pos.CENTER);
        return cell;
    }

    private List<String> loadWordList() {
        List<String> wordList = new ArrayList<>();
        fetchHint(secretWord);                      // hiển thị gợi ý lúc đầu game // ở restart game có nữa.
        startTimer();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/wordlist.txt");
            if (inputStream == null) {
                System.err.println("File wordlist.txt not found!");
                return wordList; // trả về list rỗng để tránh crash
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                wordList.add(line.trim());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordList;
    }

    public void handleSubmit() {
        String guess = inputField.getText().trim().toLowerCase();
        if (guess.length() != WORD_LENGTH) {
            messageLabel.setText("Phải nhập đúng 5 chữ nha bro 😤");
            return;
        }

        if (currentAttempt >= MAX_ATTEMPTS) return;

        for (int i = 0; i < WORD_LENGTH; i++) {
            Label cell = getCell(currentAttempt, i);
            char c = guess.charAt(i);
            cell.setText(String.valueOf(c).toUpperCase());

            // Animation lật chữ
            animateCell(cell);

            if (c == secretWord.charAt(i)) {
                cell.getStyleClass().add("correct");
            } else if (secretWord.contains(String.valueOf(c))) {
                cell.getStyleClass().add("present");
            } else {
                cell.getStyleClass().add("absent");
            }
        }

        if (guess.equals(secretWord)) {
            stopTimer();
            playWinSound();
            messageLabel.setText("Bạn đoán đúng rồi 🎉 Quá đỉnh luôn bro!!");
            inputField.setDisable(true);
            hintButton.setDisable(true);
        } else {
            currentAttempt++;
            inputField.clear();
            messageLabel.setText("");

            if (currentAttempt == MAX_ATTEMPTS) {
                stopTimer();
                playLoseSound();
                messageLabel.setText("Thua rồi bro 💀 Từ đúng là: " + secretWord);
                inputField.setDisable(true);
                hintButton.setDisable(true);
            }
        }
        calculateScore();
    }

    @FXML
    public void handleRestart() {
        if (wordList == null || wordList.isEmpty()) {
            System.err.println("Word list is empty!");
            return;
        }
        resetGame(); // <-- Quan trọng
    }

    private void animateCell(Label cell) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), cell);
        st.setFromX(0);
        st.setToX(1);
        st.setCycleCount(1);
        st.play();
    }

    private Label getCell(int row, int col) {
        for (javafx.scene.Node node : grid.getChildren()) {
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);
            if ((r == null ? 0 : r) == row && (c == null ? 0 : c) == col) {
                return (Label) node;
            }
        }
        return null;
    }

    @FXML
    public void handleHint() {
        if (hintCount >= MAX_HINTS) {
            messageLabel.setText("Bro còn trông chờ gì tôi nữa =))");
            hintButton.setDisable(true);
            return;
        }

        List<Integer> unrevealed = new ArrayList<>();
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (!revealedPositions[i]) {
                unrevealed.add(i);
            }
        }

        if (unrevealed.isEmpty()) {
            messageLabel.setText("Không còn gì để gợi ý nữa rồi 🫡");
            hintButton.setDisable(true);
            return;
        }

        int index = unrevealed.get(new Random().nextInt(unrevealed.size()));
        char hintChar = secretWord.charAt(index);

        messageLabel.setText("💡 Gợi ý: Vị trí " + (index + 1) + " là '" + Character.toUpperCase(hintChar) + "'");
        hintCount++;

        if (hintCount >= MAX_HINTS) {
            hintButton.setDisable(true);
        }
    }

    @FXML
    public void handleGotoGame(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml");
    }

    /**
     * Hiển thị gợi ý dùng DictionaryDev lấy meaning.
     * @param word nghĩa của từ đó.
     */
    private void fetchHint(String word) {
        new Thread(() -> {
            try {
                JSONArray jsonArray = getObjects(word);
                String definition = jsonArray
                        .getJSONObject(0)
                        .getJSONArray("meanings")
                        .getJSONObject(0)
                        .getJSONArray("definitions")
                        .getJSONObject(0)
                        .getString("definition");

                // Cập nhật hint trên giao diện
                javafx.application.Platform.runLater(() -> {
                    hintLabel.setText("👉 Gợi ý: " + definition);
                });

            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    hintLabel.setText("Không lấy được gợi ý 🥲");
                });
            }
        }).start();
    }

    @NotNull
    private static JSONArray getObjects(String word) throws IOException {
        String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Phân tích JSON trả về
        JSONArray jsonArray = new JSONArray(response.toString());
        return jsonArray;
    }

    // Thêm tính năng đồng hồ đếm ngược cho game.
    private void startTimer() {
        if (timeline != null) {
            timeline.stop(); // dừng cái cũ nếu đang chạy
        }
        secondsElapsed = 0;
        timerLabel.setText("⏱️ 0s");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            timerLabel.setText("⏱️ " + secondsElapsed + "s");
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    // Thêm tính năng tính điểm - đoán càng nhanh cho điểm càng cao
    // Đoán sai là trừ 10 điểm.
    private void calculateScore() {
        // Càng ít lượt và nhanh thì điểm càng cao
        score = Math.max(0, (MAX_ATTEMPTS - currentAttempt) * 10 -secondsElapsed);
        messageLabel.setText("Bạn đoán đúng rồi 🎉 Điểm của bạn: " + score);
    }

    private void playWinSound() {
        URL winSoundURL = getClass().getResource("/com/example/wordly/audio/WinGame.mp3");
        if (winSoundURL != null) {
            AudioClip winSound = new AudioClip(winSoundURL.toExternalForm());
            winSound.play();
        } else {
            System.out.println("Không tìm thấy âm thanh WinGame.mp3!");
        }
    }

    private void playLoseSound() {
        URL loseSoundURL = getClass().getResource("/com/example/wordly/audio/LoseGame.mp3");
        if (loseSoundURL != null) {
            AudioClip loseSound = new AudioClip(loseSoundURL.toExternalForm());
            loseSound.play();
        } else {
            System.out.println("Không tìm thấy âm thanh LoseGame.mp3!");
        }
    }
}
