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
    @FXML private Button hintButton;
    @FXML private Label hintLabel;
    @FXML private Label timerLabel;
    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;

    private int wordLength = 5;
    private int maxAttempts = 6;
    private final int MAX_HINTS = 2;
    private final int COUNTDOWN_SECONDS = 90; // 3 minutes countdown
    private List<String> wordList;
    private List<String> wordListAll;
    private String secretWord;
    private int currentAttempt = 0;
    private boolean[] revealedPositions = new boolean[wordLength];
    private int hintCount = 0;

    // Game state variables
    private int currentLevel = 1;
    private int totalScore = 0;
    private int levelScore = 0;
    private int timeRemaining = COUNTDOWN_SECONDS;
    private Timeline countdownTimer;


    /**
     * 2 chức năng chính của game là load từ khi bắt đầu và resetgame.
     */
    public void initialize() {
        wordList = loadWordList();
        wordListAll = loadWordList();
        resetGame();
        startCountdownTimer();
    }


    /**
     * Reset game khi từ mình đoán đúng.
     */
    private void resetGame() {
        currentAttempt = 0;
        hintCount = 0;
        revealedPositions = new boolean[wordLength];
        levelScore = 0;
        timeRemaining = COUNTDOWN_SECONDS;
        updateTimerDisplay();

        // Chỉ chọn từ có độ dài phù hợp
        if (!wordList.isEmpty()) {
            secretWord = wordList.get(new Random().nextInt(wordList.size()));
        } else {
            // Fallback nếu không có từ nào
            secretWord = "default".substring(0, Math.min(10, wordLength));
        }

        System.out.println("🤫 Từ bí mật: " + secretWord);

        grid.getChildren().clear();
        messageLabel.setText("");
        inputField.setDisable(false);
        inputField.clear();
        hintButton.setDisable(false);

        for (int row = 0; row < maxAttempts; row++) {
            for (int col = 0; col < wordLength; col++) {
                Label cell = createCell();
                grid.add(cell, col, row);
            }
        }

        fetchHint(secretWord);
        updateUI();
        startCountdownTimer(); // Khởi động lại đồng hồ
    }

    private void createFlexibleGrid() {
        // Xóa các constraints cũ (nếu có)
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        // Tạo các ô với kích thước linh hoạt
        for (int row = 0; row < maxAttempts; row++) {
            for (int col = 0; col < wordLength; col++) {
                Label cell = createCell();

                // Thiết lập kích thước động dựa trên độ dài từ
                cell.setPrefSize(calculateCellSize(), calculateCellSize());
                grid.add(cell, col, row);
            }
        }
    }

    private double calculateCellSize() {
        // Tính toán kích thước ô dựa trên độ dài từ
        // Ô càng nhiều thì kích thước càng nhỏ để vừa màn hình
        double baseSize = 50.0; // Kích thước cơ bản
        double size = baseSize * (5.0 / wordLength); // Giảm kích thước theo tỷ lệ

        // Đảm bảo kích thước tối thiểu và tối đa
        return Math.max(30.0, Math.min(60.0, size));
    }

    private void updateUI() {
        levelLabel.setText("Level: " + currentLevel);
        scoreLabel.setText("Score: " + totalScore);
    }

    private Label createCell() {
        Label cell = new Label("");
        cell.setFont(Font.font(calculateFontSize()));
        cell.getStyleClass().add("cell");
        cell.setAlignment(javafx.geometry.Pos.CENTER);
        cell.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2px;");
        return cell;
    }

    private double calculateFontSize() {
        // Điều chỉnh font size dựa trên độ dài từ
        double baseFontSize = 20.0;
        return baseFontSize * (5.0 / wordLength);
    }

    private List<String> loadWordList() {
        List<String> words = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/wordlist.txt");
            if (inputStream == null) {
                System.err.println("File wordlist.txt not found!");
                return words;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    private void filterWordListByLength(int length) {
        wordList = new ArrayList<>();
        for (String word : wordListAll) {
            if (word.length() == length) {
                wordList.add(word);
            }
        }

        if (wordList.isEmpty()) {
            System.err.println("Không có từ nào có độ dài " + length + " trong wordlist!");
            // Fallback: thêm một số từ mặc định
            wordList.add("default"); // Ví dụ cho 7 chữ
        }
    }

    public void handleSubmit() {
        String guess = inputField.getText().trim().toLowerCase();
        if (guess.length() != wordLength) {
            messageLabel.setText("Phải nhập đúng " + wordLength + " chữ nha bro 😤");
            return;
        }

        if (currentAttempt >= maxAttempts) return;

        for (int i = 0; i < wordLength; i++) {
            Label cell = getCell(currentAttempt, i);
            char c = guess.charAt(i);
            cell.setText(String.valueOf(c).toUpperCase());
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
            levelScore = calculateScore();
            totalScore += levelScore;
            playWinSound();
            messageLabel.setText(String.format("Level %d hoàn thành! 🎉 Điểm: +%d (Tổng: %d)",
                    currentLevel, levelScore, totalScore));
            inputField.setDisable(true);
            hintButton.setDisable(true);

            // Dừng đồng hồ hiện tại trước khi chuyển level
            if (countdownTimer != null) {
                countdownTimer.stop();
            }

            // Proceed to next level after delay
            Timeline levelTransition = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                currentLevel++;
                resetGame(); // resetGame() sẽ tự động gọi startCountdownTimer()
            }));
            levelTransition.play();
        } else {
            currentAttempt++;
            inputField.clear();
            messageLabel.setText("");

            if (currentAttempt == maxAttempts) {
                playLoseSound();
                messageLabel.setText(String.format("Game Over! 💀 Từ đúng là: %s (Level %d)",
                        secretWord, currentLevel));
                inputField.setDisable(true);
                hintButton.setDisable(true);
                endGame();
            }
        }
    }

    public void setGameDifficulty(int length, int attempts) {
        this.wordLength = length;
        this.maxAttempts = attempts;
        filterWordListByLength(length); // Lọc từ điển theo độ dài
        resetGame();
    }

    @FXML
    public void handleEasyMode() {
        setGameDifficulty(5, 6); // 5 chữ, 6 lần thử
    }

    @FXML
    public void handleMediumMode() {
        setGameDifficulty(7, 6); // 7 chữ, 6 lần thử
    }

    @FXML
    public void handleHardMode() {
        setGameDifficulty(10, 6); // 10 chữ, 6 lần thử
    }

    private void startCountdownTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        timeRemaining = COUNTDOWN_SECONDS;
        updateTimerDisplay();

        countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateTimerDisplay();

            if (timeRemaining <= 0) {
                endGame();
            }
        }));
        countdownTimer.setCycleCount(Animation.INDEFINITE);
        countdownTimer.play();
    }

    private void updateTimerDisplay() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("⏱️ %02d:%02d", minutes, seconds));

        // Đổi màu đỏ nếu còn dưới 10 giây
        if (timeRemaining <= 10) {
            timerLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            timerLabel.setStyle(""); // Reset về style mặc định
        }
    }

    private void endGame() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        inputField.setDisable(true);
        hintButton.setDisable(true);
        messageLabel.setText(String.format("Thời gian kết thúc! Tổng điểm: %d", totalScore));
    }

    private int calculateScore() {
        // Score based on attempts left and time remaining
        int attemptsBonus = (maxAttempts - currentAttempt) * 10;
        int timeBonus = timeRemaining / 10; // Bonus for remaining time
        return Math.max(0, attemptsBonus + timeBonus);
    }

    @FXML
    public void handleRestart() {
        if (wordList == null || wordList.isEmpty()) {
            System.err.println("Word list is empty!");
            return;
        }
        resetGame();
        startCountdownTimer(); // Thêm dòng này để đảm bảo đồng hồ chạy lại
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
        for (int i = 0; i < wordLength; i++) {
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
