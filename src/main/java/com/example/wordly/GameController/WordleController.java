package com.example.wordly.GameController;

import com.example.wordly.controllerForUI.BaseController;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordleController extends BaseController {
    @FXML private GridPane grid;
    @FXML private TextField inputField;
    @FXML private Label messageLabel;
    @FXML private Button hintButton;
    
    private  final int WORD_LENGTH = 5;         // Chiều dài của từ
    private final int MAX_ATTEMPTS = 6;         // Số lần thử
    private final int MAX_HINTS = 2;            // số lần gợi ý tối đa

    // Tạo danh sách từ trong WordleGame, Từ bí mật là lấy ngẫu nhiên, số lượt thử (6 lần)
    // hiển thị gợi ý là 2 lần.
    // Vị trí đoán đúng thì sẽ bôi màu.
    private List<String> wordList;
    private String secretWord;
    private int currentAttempt = 0;
    private boolean[] revealedPositions = new boolean[WORD_LENGTH];
    private int hintCount = 0;      //

    public void initialize() {
        wordList = loadWordList(); // <-- GÁN lại nha bro
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
            messageLabel.setText("Bạn đoán đúng rồi 🎉 Quá đỉnh luôn bro!!");
            inputField.setDisable(true);
            hintButton.setDisable(true);
        } else {
            currentAttempt++;
            inputField.clear();
            messageLabel.setText("");
            if (currentAttempt == MAX_ATTEMPTS) {
                messageLabel.setText("Thua rồi bro 💀 Từ đúng là: " + secretWord);
                inputField.setDisable(true);
                hintButton.setDisable(true);
            }
        }
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
}
