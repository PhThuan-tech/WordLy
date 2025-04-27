package com.example.wordly.GameController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DefinitionGameController {
    private static final String GAME_FILE_RESOURCE_PATH = "/game_data.txt";
    private static final String DELIMITER = "\t";
    private static final int REQUIRED_WORDS = 4;
    private final Random random = new Random();
    private final int totalQuestions = 10;

    @FXML
    public Button backButton;
    @FXML
    public Label feedbackLabel;
    @FXML
    public Label wordLabel;
    @FXML
    public Label scoreLabel;
    @FXML
    public Label questionCounterLabel;
    @FXML
    public GridPane optionsContainer;
    @FXML
    public Button optionButton1;
    @FXML
    public Button optionButton2;
    @FXML
    public Button optionButton3;
    @FXML
    public Button optionButton4;
    @FXML
    public Button nextButton;

    private List<String> wordList;
    private List<String> definitionList;
    private List<Button> optionButtons;
    private String correctDefinition;
    private int score = 0;
    private int currentQuestion = 0;

    @FXML
    public void initialize() {
        optionButtons = List.of(optionButton1, optionButton2, optionButton3, optionButton4);
        loadWordsAndDefinitions();

        System.out.println("DEBUG: Loaded words = " + wordList.size());
        if (wordList.size() < REQUIRED_WORDS) {
            showErrorAndDisable("Lỗi: Không đủ từ hợp lệ để bắt đầu");
        } else {
            displayNewQuestion();
            updateScoreLabel();
            updateQuestionCounter();
            nextButton.setVisible(false);
            feedbackLabel.setText("");
        }
    }

    /**
     * TODO : Lấy từ random trong game_data.
     */
    private void loadWordsAndDefinitions() {
        wordList = new ArrayList<>();
        definitionList = new ArrayList<>();
        InputStream is = getClass().getResourceAsStream(GAME_FILE_RESOURCE_PATH);
        System.out.println("DEBUG: game_data.txt stream is " + (is == null ? "NULL" : "OK"));
        if (is == null) return;

        //DOC FILE VA LUU VAO WORDLIST & DEFINITION LIST
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(DELIMITER, 4);
                if (parts.length == 4) {
                    String word = parts[0].trim(); // Lấy từ
                    String def = parts[3].trim(); // Lấy định nghĩa từ
                    //Chỉ add các từ hữu hình
                    if (!word.isEmpty() && !def.isEmpty()) {
                        wordList.add(word);
                        definitionList.add(def);
                    }
                }
            }
            System.out.println("DEBUG : Da them vao " + wordList.size() + "từ.");
        } catch (IOException e) {
            System.err.println("Error reading game_data.txt: " + e.getMessage());
        }
    }

    // HIEN THI CAU HOI TIEP THEO
    private void displayNewQuestion() {
        //HET 10 CAU
        if (currentQuestion >= totalQuestions) {
            showEndGameMessage();
            return;
        }
        resetUIState();
        currentQuestion++;
        updateQuestionCounter();

        //RANDOM VA LAY INDEX
        int index = random.nextInt(wordList.size());
        String word = wordList.get(index);
        correctDefinition = definitionList.get(index);

        wordLabel.setTextFill(Color.BLACK);
        wordLabel.setText(word);
        System.out.println("DEBUG QA: word='" + word + "', def='" + correctDefinition + "'");

        List<String> options = new ArrayList<>();
        options.add(correctDefinition);
        // LAY RANDOM VA SUFFLE 4 DEFINITIONS
        List<String> pool = new ArrayList<>(definitionList);
        pool.remove(index);
        Collections.shuffle(pool);
        for (String d : pool) {
            if (options.size() >= REQUIRED_WORDS) break;
            options.add(d);
        }
        Collections.shuffle(options);

        for (int i = 0; i < optionButtons.size(); i++) {
            if (i < options.size()) {
                Button b = optionButtons.get(i);
                b.setText(options.get(i));
                b.setVisible(true);
                b.setDisable(false);
            } else {
                optionButtons.get(i).setVisible(false);
            }
        }
    }

    @FXML
    public void handleOptionSelected(ActionEvent event) {
        String sel = ((Button) event.getSource()).getText();
        if (sel == null || sel.isBlank()) {
            feedbackLabel.setText("Lỗi: Định nghĩa không hợp lệ.");
            return;
        }
        enableOptionButtons(false);
        nextButton.setVisible(true);
        if (sel.equals(correctDefinition)) {
            feedbackLabel.setText("Chính xác!");
            score++;
            updateScoreLabel();
        } else {
            feedbackLabel.setText("Không đúng. Đúng là:\n" + correctDefinition);
        }
    }

    @FXML
    public void handleNextQuestion(ActionEvent event) {
        displayNewQuestion();
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            Parent p = FXMLLoader.load(getClass().getResource(
                    "/com/example/wordly/View/GameView.fxml"));
            Stage s = (Stage) ((Node) event.getSource()).getScene().getWindow();
            s.getScene().setRoot(p);
        } catch (IOException ignored) {
        }
    }


    // ===== CAC PHUONG THUC HO TRO =====

    // HIEN THI NUT CHON
    private void enableOptionButtons(boolean e) {
        optionButtons.forEach(b -> b.setDisable(!e));
    }

    // TAO LAI UI (user interface)
    private void resetUIState() {
        feedbackLabel.setText("");
        nextButton.setVisible(false);
        optionButtons.forEach(b -> {
            b.setDisable(false);
            b.setVisible(true);
        });
    }

    //CAP NHAT BANG DIEM
    private void updateScoreLabel() {
        scoreLabel.setText("Điểm: " + score);
    }

    //DEM SO CAU HOI DA LAM
    private void updateQuestionCounter() {
        questionCounterLabel.setText("Câu hỏi " + currentQuestion + " / " + totalQuestions);
    }

    //HIEN THI LOI VA DUNG TAC VU
    private void showErrorAndDisable(String m) {
        feedbackLabel.setText(m);
        wordLabel.setText("ERROR");
        enableOptionButtons(false);
        nextButton.setDisable(true);
        new Alert(Alert.AlertType.ERROR, m).showAndWait();
    }

    //KHI KET THUC GAME
    private void showEndGameMessage() {
        new Alert(Alert.AlertType.INFORMATION,
                "Bạn đã hoàn thành trò chơi! Điểm: " + score + "/" + totalQuestions)
                .showAndWait();
    }
}
