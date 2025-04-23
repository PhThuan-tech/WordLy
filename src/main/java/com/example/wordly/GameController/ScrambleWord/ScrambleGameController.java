package com.example.wordly.GameController.ScrambleWord;

import com.example.wordly.controllerForUI.BaseController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ScrambleGameController extends BaseController {
    @FXML private Label scrambledWordLabel;
    @FXML private TextField inputField;
    @FXML private Button checkButton, nextButton, restartButton, showHistoryButton;
    @FXML private ChoiceBox<String> difficultyBox;
    @FXML private TextArea historyArea;
    @FXML private Label scoreLabel, resultLabel, timerLabel;

    private int score = 0, timeLeft = 30;
    private Timeline timeline;
    private GameManager gameManager;
    private Path historyPath;

    @FXML
    public void initialize() {
        // Initialize history path in user's home directory
        String userHome = System.getProperty("user.home");
        Path scrambleHistoryDir = Paths.get(userHome, "ScrambleHistory");
        historyPath = scrambleHistoryDir.resolve("historyScramble.txt");

        try {
            // Create directory if it doesn't exist
            if (!Files.exists(scrambleHistoryDir)) {
                Files.createDirectories(scrambleHistoryDir);
            }
            // Create file if it doesn't exist
            if (!Files.exists(historyPath)) {
                Files.createFile(historyPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize history file: " + e.getMessage());
        }

        difficultyBox.getItems().addAll("Dễ", "Vừa", "Khó");
        difficultyBox.setValue("Vừa");

        WordProvider provider = new WordProvider();
        List<String> words = provider.loadWords();

        if (words.isEmpty()) {
            resultLabel.setText("Không thể tải từ từ file.");
            return;
        }

        gameManager = new GameManager(new ScrambleMode(words));
        loadNewRound();
    }

    @FXML
    private void onCheck() {
        String userInput = inputField.getText().trim();
        String currentWord = gameManager.getCurrentWord();
        String resultMessage;

        if (gameManager.checkAnswer(userInput)) {
            score++;
            resultMessage = "✅ Đúng rồi!";
        } else {
            resultMessage = "❌ Sai! Từ đúng là: " + currentWord;
        }

        scoreLabel.setText("Điểm: " + score);
        resultLabel.setText(resultMessage);
        checkButton.setDisable(true);
        timeline.stop();

        // Lưu lịch sử sau khi kiểm tra
        String historyData = resultMessage + " Câu trả lời: " + userInput + " - Từ đúng: " + currentWord + " - Điểm: " + score + " - Thời gian: " + getTimeStamp();
        saveHistory(historyData);
    }

    private String getTimeStamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    @FXML
    private void onNext() {
        loadNewRound();
    }

    @FXML
    private void onRestart() {
        score = 0;
        scoreLabel.setText("Điểm: 0");
        loadNewRound();
    }

    @FXML
    private void onShowHistory() {
        try {
            if (Files.exists(historyPath)) {
                List<String> lines = Files.readAllLines(historyPath);
                if (lines.isEmpty()) {
                    historyArea.setText("Chưa có lịch sử trò chơi.");
                } else {
                    historyArea.setText(String.join("\n", lines));
                }
            } else {
                historyArea.setText("Chưa có lịch sử trò chơi.");
            }
        } catch (IOException e) {
            historyArea.setText("Không thể đọc lịch sử: " + e.getMessage());
        }
    }

    private void loadNewRound() {
        inputField.clear();
        checkButton.setDisable(false);
        resultLabel.setText("");

        gameManager.loadNewWord(difficultyBox.getValue());
        scrambledWordLabel.setText(gameManager.getScrambledWord());
        startTimer();
    }

    private void startTimer() {
        timeLeft = 30;
        timerLabel.setText("⏳ 30s");
        if (timeline != null) timeline.stop();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerLabel.setText("⏳ " + timeLeft + "s");
            if (timeLeft <= 0) {
                String currentWord = gameManager.getCurrentWord();
                resultLabel.setText("⏰ Hết giờ! Từ đúng là: " + currentWord);
                checkButton.setDisable(true);
                timeline.stop();

                // Lưu lịch sử khi hết giờ
                String historyData = "Hết giờ! Từ đúng là: " + currentWord + " - Điểm: " + score + " - Thời gian: " + getTimeStamp();
                saveHistory(historyData);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void saveHistory(String data) {
        try {
            // Add newline before new entry
            String entry = "\n" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("[dd-MM-yyyy HH:mm:ss]")) + " " + data;
            Files.write(historyPath, entry.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("❌ Không thể ghi lịch sử: " + e.getMessage());
            // Show alert to user
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Không thể lưu lịch sử");
                alert.setContentText("Lịch sử trò chơi không thể được lưu lại.");
                alert.showAndWait();
            });
        }
    }

    public void HandleBackToGame(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml");
    }
}
