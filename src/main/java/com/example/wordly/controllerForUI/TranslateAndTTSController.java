package com.example.wordly.controllerForUI;

import com.example.wordly.TTS.LibreTranslator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles; // For logger name
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger; // For logging

/**
 * Controller for the Translate and Text-To-Speech view (TranslateTTSView.fxml).
 * Handles user interactions for translating text using LibreTranslator,
 * displaying the translation, providing text-to-speech functionality (placeholder buttons),
 * and navigating to other application views.
 */
public class TranslateAndTTSController extends BaseController {
    // --- Logger ---
    // Use java.util.logging (JUL) for simplicity, or SLF4J if preferred in the project
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    //Ông ko cần comment ở phần FXML đâu á.
    // vì cái này khi tạo hàm view.fxml là nó tự tạo thôi. mình tự hiểu được.

    @FXML private TextArea needToTrans;

    @FXML private TextArea translated;

    @FXML private Button transButton;

    @FXML private Button speak1Button; // Placeholder for future TTS

    @FXML private Button speak2Button; // Placeholder for future TTS

    /**
     * Xử lí chuyển đổi giữa các giao diện.
     */
    @FXML
    public void initialize() {
    }

    // Tôi sửa phần chuyển đổi giao diện thôi nhé.
    // Phần nào cần sự trợ giúp thì ông hú tôi cái tôi nghĩ cùng.
    @FXML
    public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/MainView.fxml");
    }

    @FXML
    public void handleGoToSearch(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }

    @FXML
    public void handleGoToFavourite(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/FavouriteView.fxml");
    }

    @FXML
    public void handleGotoEdit(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/EditWordView.fxml");
    }

    @FXML
    public void handleGotoGame(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml");
    }

    @FXML
    public void handleGoToHistory(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/HistoryView.fxml");
    }

    @FXML
    public void handleGoToChat(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/ChatBot.fxml");
    }
    // --- Core Functionality Event Handlers ---

    /**
     * Handles the action event when the 'Translate' button is clicked.
     * Retrieves text from the input TextArea, validates it, and initiates
     * an asynchronous translation task using {@link LibreTranslator}.
     * Updates the UI with the result or displays an error message.
     * Disables the translation button during the operation.
     *
     * @param actionEvent The event triggered by clicking the button.
     */
    @FXML
    public void handleTranslating(ActionEvent actionEvent) {
        try {
            String needToTrans = this.needToTrans.getText();
            String targetLang = "vi";
            String sourceLang = "auto";

            String scriptUrl = "https://script.google.com/macros/s/AKfycbyonS6sLu4FvvR9ve4ZC1QSKZIH9zMtoZ9RgA7uzt4KpJdDf-EE7QQ-WCqILh6XEy0nVw/exec"; // link của bạn
            String urlStr = scriptUrl
                    + "?text=" + URLEncoder.encode(needToTrans, StandardCharsets.UTF_8)
                    + "&source=" + sourceLang
                    + "&target=" + targetLang;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String translatedText = br.readLine();
            conn.disconnect();

            translated.setText(translatedText);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Displays an informational alert dialog to the user.
     * Ensures the alert is shown on the JavaFX Application Thread.
     *
     * @param title   The title of the alert window.
     * @param content The main message content of the alert.
     */
    private void showInfoAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * Extracts a concise, user-friendly message from a Throwable.
     * Prefers the Throwable's message, falls back to the class name if the message is null/blank.
     *
     * @param t The throwable exception.
     * @return A non-null, non-blank error message string.
     */
    private String getConciseErrorMessage(Throwable t) {
        if (t == null) {
            return "An unknown error occurred.";
        }
        String message = t.getMessage();
        if (message != null && !message.isBlank()) {
            return message;
        } else {
            // If no message, return the exception type as a basic indicator
            return "Error type: " + t.getClass().getSimpleName();
        }
    }

    // --- Placeholder Methods for TTS ---
    // TODO: Implement actual Text-To-Speech logic for speak1Button and speak2Button

    @FXML
    private void handleSpeakOriginal(ActionEvent event) {
        String text = needToTrans.getText();
        if (text != null && !text.isBlank()) {
            // Add TTS implementation here
            showInfoAlert("TTS Placeholder", "Speaking original text: \"" + text.substring(0, Math.min(text.length(), 30)) + "...\"");
        } else {
            showInfoAlert("TTS Unavailable", "No original text available to speak.");
        }
    }

    @FXML
    private void handleSpeakTranslated(ActionEvent event) {
        String text = translated.getText();
        if (text != null && !text.isBlank()) {
            // Add TTS implementation here
            showInfoAlert("TTS Placeholder", "Speaking translated text: \"" + text.substring(0, Math.min(text.length(), 30)) + "...\"");
        } else {
            showInfoAlert("TTS Unavailable", "No translated text available to speak.");
        }
    }
}