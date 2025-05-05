package com.example.wordly.GameController.Wordle;

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
import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class WordleController extends BaseController {
    @FXML private GridPane grid;
    @FXML private TextField inputField;
    @FXML private Label messageLabel;
    @FXML private Button hintButton;
    @FXML private Label hintLabel;
    @FXML private Label timerLabel;
    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;

    // cac thuoc tinh tren giao dien
    private int totalScore = 0;
    private int currentLevel = 1;
    private int timeRemaining; // Managed by the timer timeline
    private Timeline countdownTimer;

    // cac instance cua 3 lop logic game, setdifficult.
    private WordleGame game;
    private WordDataLoader dataLoader;
    private List<String> wordListAll;
    private Map<String, String> wordHints;

    // :v pham do kho xu li kha dai, o gan cuoi se co
    private GameDifficulty currentDifficulty = GameDifficulty.EASY;// mac dinh la EASY (5 tu)

    public void initialize() {
        System.out.println("WordleController initialized");
        dataLoader = new WordDataLoader();
        wordListAll = dataLoader.loadWordList();
        wordHints = dataLoader.loadHints();
        game = new WordleGame(wordListAll);

        setGameDifficulty(currentDifficulty);
    }

    private void resetGame() {
        // phan check trang thai khi reset, de con biet loi cho nao.
        System.out.println("Resetting game for level " + currentLevel + " (Word Length: " + game.getWordLength() + ")");

        grid.getChildren().clear();
        messageLabel.setText("");
        inputField.setDisable(false);
        inputField.clear();
        hintLabel.setText("👉 Đang tải gợi ý...");

        // tao 1 bang rong moi, thoi gian, hint , score reset lai tu dau
        createFlexibleGrid();
        displayFileHint(game.getSecretWord());
        timeRemaining = currentDifficulty.getTimeLimitSeconds();
        updateTimerDisplay();
        startCountdownTimer();

        updateUI();
        hintButton.setDisable(!game.canUsePositionalHint());
    }

    private void createFlexibleGrid() {
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        grid.getChildren().clear();

        int wordLength = game.getWordLength();
        int maxAttempts = game.getMaxAttempts();

        for (int row = 0; row < maxAttempts; row++) {
            for (int col = 0; col < wordLength; col++) {
                Label cell = createCell();
                cell.setPrefSize(calculateCellSize(), calculateCellSize());
                grid.add(cell, col, row);
            }
        }
    }

    private double calculateCellSize() {
        int wordLength = game.getWordLength();
        double baseSize = 55.0;
        double size = baseSize * (5.0 / wordLength);
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
        int wordLength = game.getWordLength();
        double baseFontSize = 24.0;
        return baseFontSize * (5.0 / wordLength);
    }

    @FXML
    public void handleSubmit() {
        String guess = inputField.getText().trim().toLowerCase();
        int wordLength = game.getWordLength();

        if (guess.length() != wordLength) {
            messageLabel.setText("Phải nhập đúng " + wordLength + " chữ nha bro 😤");
            return;
        }

        if (game.getCurrentAttempt() >= game.getMaxAttempts()) {
            System.out.println("Attempted to submit guess when game state is already over.");
            return;
        }

        /* king qua ko hieu sao fix dc loi nay :v
        if (!game.isValidWord(guess)) {
            messageLabel.setText("Từ '" + guess + "' không có trong từ điển!");
            return;
        }
        */
        WordleGame.LetterStatus[] results = game.processGuess(guess);

        if (results == null) {
            messageLabel.setText("Lỗi xử lý đoán. Vui lòng thử lại.");
            return;
        }

        int currentAttemptRow = game.getCurrentAttempt() - 1;
        for (int i = 0; i < wordLength; i++) {
            Label cell = getCell(currentAttemptRow, i);
            if (cell == null) {
                System.err.println("Error: Could not get cell at row " + currentAttemptRow + ", col " + i);
                continue;
            }

            char c = guess.charAt(i);
            cell.setText(String.valueOf(c).toUpperCase());
            cell.getStyleClass().removeAll("correct", "present", "absent");
            switch (results[i]) {
                case CORRECT:
                    cell.getStyleClass().add("correct");
                    break;
                case PRESENT:
                    cell.getStyleClass().add("present");
                    break;
                case ABSENT:
                    cell.getStyleClass().add("absent");
                    break;
            }
            animateCell(cell);
        }

        if (game.isWin(guess)) {
            int levelScore = game.calculateScore(timeRemaining);
            totalScore += levelScore;
            playWinSound();
            messageLabel.setText(String.format("Level %d hoàn thành! 🎉 Điểm: +%d (Tổng: %d)",
                    currentLevel, levelScore, totalScore));
            inputField.setDisable(true);
            hintButton.setDisable(true);
            if (countdownTimer != null) {
                countdownTimer.stop();
            }
            updateUI();

            Timeline levelTransition = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                currentLevel++;
                List<String> wordsForCurrentLength = dataLoader.filterWordByLength(wordListAll, currentDifficulty.getWordLength());
                if (wordsForCurrentLength.isEmpty()) {
                    System.err.println("Cannot advance level: No words available for length " + currentDifficulty.getWordLength());
                    messageLabel.setText(String.format("Chúc mừng! Bạn đã hoàn thành Level %d, nhưng không có đủ từ cho độ dài %d để tiếp tục. Tổng điểm: %d",
                            currentLevel - 1, currentDifficulty.getWordLength(), totalScore));
                    endGame();
                } else {
                    game.setDifficulty(
                            currentDifficulty.getWordLength(),
                            currentDifficulty.getMaxAttempts(),
                            wordsForCurrentLength
                    );
                    resetGame();
                }
            }));
            levelTransition.play();

        } else {
            inputField.clear();
            messageLabel.setText("");
            if (game.isGameOver()) {
                playLoseSound();
                messageLabel.setText(String.format("Game Over! 💀 Từ đúng là: %s (Level %d)",
                        game.getSecretWord(), currentLevel));
                endGame();
            }
        }
    }

    public void setGameDifficulty(GameDifficulty difficulty) {
        System.out.println("Setting difficulty to: " + difficulty.name());
        // Stop current game/timer before changing difficulty
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        currentLevel = 1;
        totalScore = 0;

        currentDifficulty = difficulty;
        List<String> filteredList = dataLoader.filterWordByLength(wordListAll, difficulty.getWordLength());
        if (filteredList.isEmpty()) {
            messageLabel.setText("Lỗi: Không có từ cho độ dài " + difficulty.getWordLength() + ". Vui lòng kiểm tra file Word4WordleGame.txt.");
            inputField.setDisable(true);
            hintButton.setDisable(true);
            timerLabel.setText("⏱ --:--");
            return;
        }
        game.setDifficulty(difficulty.getWordLength(), difficulty.getMaxAttempts(), filteredList);
        resetGame();
    }


    // --- FXML Action Handlers for Difficulty Buttons ---

    @FXML public void handleEasyMode() {
        setGameDifficulty(GameDifficulty.EASY);
    }

    @FXML public void handleMediumMode() {
        setGameDifficulty(GameDifficulty.MEDIUM);
    }

    @FXML public void handleHardMode() {
        setGameDifficulty(GameDifficulty.HARD);
    }

    // xu li thoi gian dem cua tro choi
    private void startCountdownTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateTimerDisplay();   // cap nhat thoi gian lien tuc
            // neu het gio thi am thanh ket thuc, thong bao thua va nut restart bat
            if (timeRemaining <= 0) {
                timeRemaining = 0;
                updateTimerDisplay();
                playLoseSound();
                messageLabel.setText(String.format("Hết giờ! 💀 Từ đúng là: %s (Level %d)",
                        game.getSecretWord(), currentLevel));
                endGame();
            }
        }));
        countdownTimer.setCycleCount(Animation.INDEFINITE);
        countdownTimer.play();
    }

    private void updateTimerDisplay() {
        int displayTime = Math.max(0, timeRemaining);
        int minutes = displayTime / 60;
        int seconds = displayTime % 60;
        timerLabel.setText(String.format("⏱️ %02d:%02d", minutes, seconds));
        if (timeRemaining <= 10 && timeRemaining > 0) {
            timerLabel.getStyleClass().add("low-time");
        } else {
            timerLabel.getStyleClass().remove("low-time");
        }
    }

    private void endGame() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        inputField.setDisable(true);
        hintButton.setDisable(true);
    }

    @FXML
    public void handleRestart() {
        System.out.println("Restarting game...");
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        // Reset score and level
        currentLevel = 1;
        totalScore = 0;
        setGameDifficulty(currentDifficulty);
    }

    // --- UI Animation ---
    private void animateCell(Label cell) {
        // Create a scale transition for a pop-out effect
        ScaleTransition st = new ScaleTransition(Duration.millis(200), cell);
        st.setFromX(0.8); // Start slightly smaller
        st.setFromY(0.8);
        st.setToX(1.0);   // Scale back to normal size
        st.setToY(1.0);
        st.setCycleCount(1); // Play once
        st.play(); // Start the animation
    }

    private Label getCell(int row, int col) {
        // Iterate through all children in the grid pane
        for (javafx.scene.Node node : grid.getChildren()) {
            // Get row and column indices, defaulting to 0 if not set (though typically set by grid.add)
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);

            // Check if the current node is at the desired row and column
            // Handle null indices by defaulting to 0, as children added without explicit indices might be at 0,0
            if ((r == null ? 0 : r) == row && (c == null ? 0 : c) == col) {
                return (Label) node; // Cast the node to a Label and return it
            }
        }
        return null; // Return null if no cell found at the given coordinates
    }
    @FXML
    public void handleHint() {
        System.out.println("Hint button clicked. Positional hints used: " + game.getPositionalHintCount());

        // Check if a hint can be used before attempting to get one
        if (!game.canUsePositionalHint()) {
            messageLabel.setText("Đã dùng hết " + game.getMaxPositionalHints() + " lượt gợi ý vị trí hoặc không còn vị trí nào để gợi ý.");
            hintButton.setDisable(true); // Ensure button is disabled
            return; // Do not proceed if no hints are available
        }

        // Request a positional hint index from the game logic
        int indexToReveal = game.getPositionalHintIndex();

        if (indexToReveal != -1) {
            // Hint successfully provided (index is valid)
            char hintChar = game.getSecretWord().charAt(indexToReveal);
            messageLabel.setText("💡 Gợi ý vị trí: Kí tự thứ " + (indexToReveal + 1) + " là '" + Character.toUpperCase(hintChar) + "'");

            // Optional: Deduct score for using hint
            // This score deduction is handled in the Controller as it affects totalScore (UI state)
            totalScore = Math.max(0, totalScore - 15); // Deduct some points, ensure score doesn't go below 0
            scoreLabel.setText("Score: " + totalScore); // Update score display on UI

            System.out.println("Positional hint given at index " + indexToReveal + ". Hint count: " + game.getPositionalHintCount());

        } else {
            // This case should ideally be caught by canUsePositionalHint(), but included defensively.
            messageLabel.setText("Không còn gì để gợi ý vị trí nữa rồi 🫡");
            System.err.println("handleHint called but getPositionalHintIndex returned -1 (no available hint).");
        }

        // After attempting to give a hint, check if the button should now be disabled
        // This handles disabling the button *after* the last hint is used.
        if (!game.canUsePositionalHint()) {
            hintButton.setDisable(true);
            // Append a message indicating hints are exhausted if a hint was just given
            if (indexToReveal != -1) { // Only append if a hint was actually given
                messageLabel.setText(messageLabel.getText() + " (Đã dùng hết " + game.getMaxPositionalHints() + " lượt gợi ý vị trí)");
            }
        }
    }

    // --- Navigation ---
    @FXML
    public void handleGotoGame(ActionEvent actionEvent) {
        System.out.println("Going back to main game view...");
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml");
    }

    // phan lay goi y tu file Hints.txt
    private void displayFileHint(String word) {
        String hint = wordHints.get(word);
        if (hint != null && !hint.isEmpty()) {
            hintLabel.setText("👉 Gợi ý: " + hint);
        } else {
            hintLabel.setText("👉 Không có gợi ý cho từ này 🥲");
        }
    }
    // phan am thanh de xu li nhat =))
    private void playWinSound() {
        URL winSoundURL = getClass().getResource("/com/example/wordly/audio/WinGame.mp3");
        if (winSoundURL != null) {
            AudioClip winSound = new AudioClip(winSoundURL.toExternalForm());
            winSound.play();
        } else {
            System.err.println("Không tìm thấy âm thanh WinGame.mp3!");
        }
    }

    private void playLoseSound() {
        URL loseSoundURL = getClass().getResource("/com/example/wordly/audio/LoseGame.mp3");
        if (loseSoundURL != null) {
            AudioClip loseSound = new AudioClip(loseSoundURL.toExternalForm());
            loseSound.play();
        } else {
            System.err.println("Không tìm thấy âm thanh LoseGame.mp3!");
        }
    }
}