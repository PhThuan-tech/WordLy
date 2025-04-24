package com.example.wordly.GameController.ScrambleWord;

import com.example.wordly.API.DictionaryAPI;
import com.example.wordly.controllerForUI.BaseController;
import com.example.wordly.GameController.ScrambleWord.AudioManager; // Import AudioManager
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
    @FXML private TextArea definitionArea;

    private int score = 0, timeLeft = 30;
    private Timeline timeline;
    private GameManager gameManager;
    private int attemptsLeft = 3;
    private AudioManager audioManager;

    @FXML
    public void initialize() {
        difficultyBox.getItems().addAll("Dễ", "Vừa", "Khó");
        difficultyBox.setValue("Vừa");

        WordProvider provider = new WordProvider();
        List<String> words = provider.loadWords();

        if (words.isEmpty()) {
            resultLabel.setText("Không thể tải từ từ file.");
            return;
        }

        gameManager = new GameManager(new ScrambleMode(words));
        audioManager = new AudioManager();
        resetGame(); // Gọi resetGame khi controller được khởi tạo
    }

    // Phương thức reset toàn bộ trạng thái game
    private void resetGame() {
        score = 0;
        scoreLabel.setText("Điểm: 0");
        loadNewRound(); // Bắt đầu vòng mới sau khi reset điểm
    }

    @FXML
    private void onCheck() {
        String userInput = inputField.getText().trim();
        String currentWord = gameManager.getCurrentWord();
        String resultMessage;

        if (gameManager.checkAnswer(userInput)) {
            score++;
            resultMessage = "✅ Đúng rồi!";

            String definition = DictionaryAPI.getDefinition(currentWord);
            definitionArea.setText("📖 Nghĩa của từ: " + definition);

            checkButton.setDisable(true);
            timeline.stop();
            audioManager.playWinSound();
        } else {
            attemptsLeft--;
            if (attemptsLeft > 0) {
                resultMessage = "❌ Sai! Bạn còn " + attemptsLeft + " lượt thử.";
            } else {
                resultMessage = "❌ Sai! Hết lượt thử. Từ đúng là: " + currentWord;
                checkButton.setDisable(true);
                timeline.stop();
                audioManager.playLoseSound();
                // Tùy chọn: resetGame() ở đây nếu muốn game reset ngay sau khi thua hết lượt
            }
        }

        scoreLabel.setText("Điểm: " + score);
        resultLabel.setText(resultMessage);
    }

    @FXML
    private void onNext() {
        loadNewRound();
    }

    @FXML
    private void onRestart() {
        resetGame(); // Gọi phương thức resetGame khi nhấn Restart
    }

    private void loadNewRound() {
        inputField.clear();
        checkButton.setDisable(false);
        resultLabel.setText("");
        definitionArea.clear();
        attemptsLeft = 3;

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
                audioManager.playLoseSound();
                // Tùy chọn: resetGame() ở đây nếu muốn game reset ngay sau khi hết giờ
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
        // Tùy chọn: resetGame() ở đây nếu muốn game reset khi rời khỏi màn hình
        switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml");
    }

    public Button getNextButton() {
        return nextButton;
    }

    public void setNextButton(Button nextButton) {
        this.nextButton = nextButton;
    }

    public Button getRestartButton() {
        return restartButton;
    }

    public void setRestartButton(Button restartButton) {
        this.restartButton = restartButton;
    }
}