package com.example.wordly.GameController.ScrambleWord;

import com.example.wordly.controllerForUI.BaseController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.util.List;

public class ScrambleGameController extends BaseController {
    @FXML private Label scrambledWordLabel;
    @FXML private TextField inputField;
    @FXML private Button checkButton, nextButton, restartButton;
    @FXML private ChoiceBox<String> difficultyBox;
    @FXML private Label scoreLabel, resultLabel, timerLabel;
    @FXML private Label previousScoreLabel;

    private int score = 0;
    private int timeLeft = 30;
    private Timeline timeline;
    private GameManager gameManager;
    private int attemptsLeft = 3;
    private AudioManager audioManager;
    private final int INITIAL_ATTEMPTS = 3;
    private final int INITIAL_TIME = 90;
    private int previousScore = 0;

    @FXML
    public void initialize() {
        difficultyBox.getItems().addAll("Dễ", "Vừa", "Khó");
        difficultyBox.setValue("Vừa");

        difficultyBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((_, oldValue, newValue) -> {
                    if (newValue != null && !newValue.equals(oldValue)) {
                        System.out.println("Độ khó thay đổi từ " + oldValue + " sang " + newValue );
                        resetGame();
                    }
                });
        WordProvider provider = new WordProvider();
        List<String> words = provider.loadWords();

        if (words.isEmpty()) {
            resultLabel.setText("Hiện tại file không có từ nào, vui lòng thêm từ vào file.");
            checkButton.setDisable(true);
            nextButton.setDisable(true);
            restartButton.setDisable(true);
            difficultyBox.setDisable(true);
            if (previousScoreLabel != null) {
                previousScoreLabel.setText("Điểm ván trước: 0");
            }
            return;
        }
        gameManager = new GameManager(new ScrambleMode(words));
        audioManager = new AudioManager();
        resetGame();
    }

    private void resetGame() {
        this.previousScore = this.score;

        if (timeline != null) {
            timeline.stop();
        }
        score = 0;
        attemptsLeft = INITIAL_ATTEMPTS;
        timeLeft = INITIAL_TIME;

        scoreLabel.setText("Điểm hiện tại: " + score);
        timerLabel.setText("⏳ " + timeLeft + "s");
        inputField.clear();
        resultLabel.setText("");
        checkButton.setDisable(false);
        nextButton.setDisable(true);

        if (previousScoreLabel != null) {
            previousScoreLabel.setText("Điểm ván trước: " + previousScore);
        }

        loadNewRound();
    }

    @FXML
    private void onCheck() {
        String userInput = inputField.getText().trim();
        String currentWord = gameManager.getCurrentWord();
        String resultMessage;
        if (userInput.isEmpty()) {
            resultLabel.setText("Đoán đi má nhìn gì nữa :)");
            return;
        }

        if (gameManager.checkAnswer(userInput)) {
            score++;
            resultMessage = "✅ Đỉnh nha Bro, đoán được cũng căng phết!!";
            checkButton.setDisable(true);
            if (timeline != null) {
                timeline.stop();
            }
            audioManager.playWinSound();

            nextButton.setDisable(false);
        } else {
            attemptsLeft--;
            if (attemptsLeft > 0) {
                resultMessage = "❌ Nghĩ kĩ nha mom, bà còn " + attemptsLeft + " lượt thử nữa thôi.";
            } else {
                resultMessage = "❌ Bà sai rồi nè, để tui đoán hộ cho, từ cần đoán là: " + currentWord;
                checkButton.setDisable(true);
                nextButton.setDisable(true);
                if (timeline != null) {
                    timeline.stop();
                }
                audioManager.playLoseSound();
            }
        }
        scoreLabel.setText("Điểm hiện tại: " + score);
        resultLabel.setText(resultMessage);
    }

    @FXML
    private void onNext() {
        loadNewRound();
    }

    @FXML
    private void onRestart() {
        resetGame();
    }

    private void loadNewRound() {
        if (timeline != null) {
            timeline.stop();
        }
        attemptsLeft = INITIAL_ATTEMPTS;
        timeLeft = INITIAL_TIME;

        inputField.clear();
        resultLabel.setText("");
        checkButton.setDisable(false);
        nextButton.setDisable(true);

        String selectedDifficulty = difficultyBox.getValue();
        if (selectedDifficulty == null) {
            selectedDifficulty = "Vừa";
            difficultyBox.setValue(selectedDifficulty);
        }
        gameManager.loadNewWord(selectedDifficulty);

        scrambledWordLabel.setText(gameManager.getScrambledWord());
        timerLabel.setText("⏳ " + timeLeft + "s");
        startTimer();
    }

    private void startTimer() {
        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            timeLeft--;
            timerLabel.setText("⏳ " + timeLeft + "s");
            if (timeLeft <= 0) {
                String currentWord = gameManager.getCurrentWord();
                resultLabel.setText("⏰ Oh no bro hết thời gian rồi! Từ này dễ mà, đó là: " + currentWord);
                checkButton.setDisable(true);
                nextButton.setDisable(true);
                if (timeline != null) {
                    timeline.stop();
                }
                audioManager.playLoseSound();
                resetGame();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void HandleBackToGame(ActionEvent actionEvent) {
        if (timeline != null) {
            timeline.stop();
        }
        if (audioManager != null) {
            audioManager.stopAllSounds();
        }
        switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml");
    }
}