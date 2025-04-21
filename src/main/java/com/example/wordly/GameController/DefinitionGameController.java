package com.example.wordly.GameController;

import com.example.wordly.getWord.WordDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DefinitionGameController {

    //Phục vụ cho việc đọc từ file.
    private static final String GAME_FILE_RESOURCE_PATH = "/game_data.txt";
    private static final String DELIMITER = "\t"; // Dấu phân cách là TAB
    private static final int REQUIRED_WORDS = 4; // Số lượng từ tối thiểu cần có trong file
    private final Random random = new Random(); // Random từ
    private final int totalQuestions = 10; // Tổng số câu hỏi
    // Thành phần FXML
    @FXML
    public Button backButton;
    @FXML
    public Label feedbackLabel; // ĐÚNG / SAI
    @FXML
    public Label wordLabel;      // Từ
    @FXML
    public Label scoreLabel;     // Điểm
    @FXML
    public Label questionCounterLabel; // Nhãn mới để hiển thị bộ đếm câu hỏi
    @FXML
    public GridPane optionsContainer; // Thay đổi từ VBox sang GridPane
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
    // --- Biến logic của trò chơi ---
    private List<WordDetails> wordList; // Nhận danh sách
    private List<Button> optionButtons;  // Danh sách choice
    private String correctDefinition; // Định nghĩa đúng
    private int score = 0;           // Điểm
    private int currentQuestion = 0; // Theo dõi câu hỏi hiện tại

    // --- Khởi tạo Controller ---
    @FXML
    public void initialize() {
        // Gộp các nút định nghĩa vào một danh sách để dễ quản lý
        optionButtons = List.of(optionButton1, optionButton2, optionButton3, optionButton4);

        loadWordsFromResource(); // Load file game_data.txt

        //Kiểm tra để bắt đầu chơi
        if (wordList != null && wordList.size() >= REQUIRED_WORDS) {
            displayNewQuestion();     // Hiển thị câu hỏi đầu tiên
            updateScoreLabel();       // Cập nhật điểm
            updateQuestionCounter();  // Cập nhật bộ đếm câu hỏi
            nextButton.setVisible(false);
            feedbackLabel.setText("");
        } else {
            // Xử lý lỗi nếu không tải được file hoặc không đủ từ
            String errorMsg = wordList == null ?
                    "Lỗi: Không thể tải file" :
                    "Lỗi: Không đủ từ để bắt đầu";
            showErrorAndDisable(errorMsg);
            if (nextButton != null) nextButton.setDisable(true); // Vô hiệu hóa nút Next
        }
    }

    /**
     * TODO : DOC DU LIEU TU FILE.
     */
    private void loadWordsFromResource() {
        wordList = new ArrayList<>();
        //DOC FILE
        InputStream is = getClass().getResourceAsStream(GAME_FILE_RESOURCE_PATH);

        // KIEM TRA TON TAI FILE
        if (is == null) {
            showErrorAndDisable("Lỗi nghiêm trọng: Không tìm thấy file " + GAME_FILE_RESOURCE_PATH + " trong classpath.");
            wordList = null;
            return;
        }

        // DOC FILE
        try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; //LOC DONG DE DOC

                // Tách dòng thành các phần tử bằng dấu TAB, tối đa 4 phần
                String[] parts = line.split(DELIMITER, 4);
                if (parts.length == 4) {
                    try {
                        wordList.add(new WordDetails(parts[0].trim(),
                                parts[1].trim(),
                                parts[2].trim(),
                                parts[3].trim()));
                    } catch (Exception e) {
                        //NEU CO LOI
                        System.err.println("ERROR AT LINE 118");
                    }
                } else {
                    //NEU SAI DINH DANG
                    System.err.println("SAI DINH DANG DE DICH");
                }
            }
        } catch (IOException e) {
            // LOI DOC FILE
            showErrorAndDisable("Lỗi khi đọc file dữ liệu (" + GAME_FILE_RESOURCE_PATH + "): " + e.getMessage());
            wordList = null;
        }
    }

    /**
     * TODO : HIEN THI CAU HOI
     */
    private void displayNewQuestion() {
        //KIEM TRA LAI
        if (wordList == null || wordList.size() < REQUIRED_WORDS) {
            showErrorAndDisable("Không thể hiển thị câu hỏi");
            return;
        }

        if (currentQuestion >= totalQuestions) {
            showEndGameMessage();
            return;
        }

        resetUIState(); //RESET STATUS KHI CHUYEN TRANG THAI
        currentQuestion++;
        updateQuestionCounter();

        //STEP 1 : CHON TU NGAU NHIEN TRONG DANH SACH
        int targetIndex = random.nextInt(wordList.size());
        WordDetails currentTargetWord = wordList.get(targetIndex);
        correctDefinition = currentTargetWord.getDefinition();

        // STEP2 : CHON CAC NGHIA CUA CAC TU KHAC TU DC RANDOM
        List<WordDetails> otherWords = new ArrayList<>(wordList);
        otherWords.remove(targetIndex); // XOA TU DC RANDOM DE TRANH LAP TU / DINH NGHIA

        // SETEP 3 : TAO DANH SACH LUA CHON DINH NGHIA
        List<String> currentOptions = new ArrayList<>();
        currentOptions.add(correctDefinition); // THEM DINH NGHIA DUNG TRUOC

        // LAY DINH NGHIA SAI
        for (WordDetails otherWord : otherWords) {
            if (currentOptions.size() >= REQUIRED_WORDS) break; // DU 4 LUA CHON THI DUNG
            String incorrectDef = otherWord.getDefinition();
            if (incorrectDef != null && !incorrectDef.trim().isEmpty() && !incorrectDef.equals(correctDefinition) && !currentOptions.contains(incorrectDef)) {
                currentOptions.add(incorrectDef);
            }
        }

        //STEP 4 : TRAO CAC DAP AN VA CHO HIEN THI
        Collections.shuffle(currentOptions);
        wordLabel.setText(currentTargetWord.getWord()); // HIEN THI TU CAN TIM DINH NGHIA

        for (int i = 0; i < optionButtons.size(); i++) {
            if (i < currentOptions.size()) {
                optionButtons.get(i).setText(currentOptions.get(i)); // DAT CAC DINH NGHIA CHO CAC NUT LUA CHON
                optionButtons.get(i).setVisible(true);            // HIEN THI
            } else {
                optionButtons.get(i).setVisible(false);
            }
        }
    }

    /**
     * TODO : KHI USER CHON 1 DAP AN.
     *
     * @param event KHI CHON DAP AN
     */
    @FXML
    public void handleOptionSelected(ActionEvent event) {
        Button selectedButton = (Button) event.getSource(); // NUT DUOC CHON
        String selectedDefinition = selectedButton.getText(); // DINH NGHIA DUOC CHON

        enableOptionButtons(false); // XOA CAC NUT
        nextButton.setVisible(true); // CO THE CHUYEN SANG NUT MOI

        boolean isCorrect = selectedDefinition.equals(correctDefinition); //KIEM TRA DAP AN

        if (isCorrect) { // CHON DUNG DAP AN
            feedbackLabel.setText("Chính xác!");
            score++; // CONG 1 DIEM
            updateScoreLabel(); // CAP NHAT DIEM
        } else { // CHON SAI DAP AN
            feedbackLabel.setText("Không đúng.\nĐịnh nghĩa đúng là:\n" + correctDefinition);
        }
    }

    /**
     * TODO : CHUYEN SANG TU MOI.
     *
     * @param event KHI AN NUT
     */
    @FXML
    public void handleNextQuestion(ActionEvent event) {
        displayNewQuestion();
    }

    /**
     * TODO : QUAY VE MAN HINH CHINH.
     *
     * @param event SU KIEN AN.
     */
    @FXML
    public void handleBack(ActionEvent event) {
        try {
            String viewPath = "/com/example/wordly/View/GameView.fxml";
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(viewPath), "Không tìm thấy file FXML"));
            Parent gameViewParent = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(gameViewParent);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    // BAT-TAT LUA CHON
    private void enableOptionButtons(boolean enable) {
        for (Button button : optionButtons) {
            button.setDisable(!enable);
        }
    }

    // CAU HOI MOI
    private void resetUIState() {
        feedbackLabel.setText("");
        enableOptionButtons(true);
        nextButton.setVisible(false);
    }

    // CAP NHAT DIEM
    private void updateScoreLabel() {
        scoreLabel.setText("Điểm: " + score);
    }

    // CAP NHAT BO DEM CAU HOI
    private void updateQuestionCounter() {
        questionCounterLabel.setText("Câu hỏi " + currentQuestion + " / " + totalQuestions);
    }

    // HIEN THI LOI
    private void showErrorAndDisable(String message) {
        System.err.println(message);
        if (wordLabel != null) wordLabel.setText("ERROR");
        if (feedbackLabel != null) {
            feedbackLabel.setText(message);
        }
        enableOptionButtons(false); //TAT CHON
        if (nextButton != null) nextButton.setDisable(true); // KHONG DUOC CHUYEN

        // SHOW LOI LEN ALERT
        showErrorAlert(message);
    }

    // ALERT HIEN RA KHI LOI
    private void showErrorAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi Khởi Tạo Trò Chơi");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // HIEN THI THONG BAO KET THUC TRO CHOI
    private void showEndGameMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Kết thúc trò chơi");
        alert.setHeaderText(null);
        alert.setContentText("Bạn đã hoàn thành tất cả các câu hỏi!\nĐiểm của bạn: " + score + " / " + totalQuestions);
        alert.showAndWait();
    }
}