package com.example.wordly.controllerForUI;

import com.example.wordly.API.TextToSpeech;
import com.example.wordly.API.Translator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.invoke.MethodHandles; // For logger name
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
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
    @FXML private Button laodImageButton;
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
     * Updates the UI with the result or displays an error message.
     * Disables the translation button during the operation.
     *
     * @param actionEvent The event triggered by clicking the button.
     */
    @FXML
    public void handleTranslating(ActionEvent actionEvent) {
        try {
            String textToTranslate = needToTrans.getText();
            String sourceLang = "auto"; // Azure vẫn tự nhận dạng nếu set auto
            String targetLang = "vi";

            String translatedText = Translator.translate(textToTranslate, sourceLang, targetLang);
            translated.setText(translatedText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleImageToText(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file == null) {
            showInfoAlert("No File Selected", "Please select an image file to extract text.");
            return;
        }

        try {
            String endpoint = "https://phamthaic3.cognitiveservices.azure.com/";
            String subscriptionKey = "PYdx4cObbMBRA7HnXlw5OkigfrGJ14ORRnp15JywiJzAx2e1UmFSJQQJ99BDACqBBLyXJ3w3AAAFACOGVj20";
            String uri = endpoint + "/vision/v3.2/ocr?language=unk&detectOrientation=true";

            byte[] imageBytes = Files.readAllBytes(file.toPath());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Ocp-Apim-Subscription-Key", subscriptionKey)
                    .header("Content-Type", "application/octet-stream")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(imageBytes))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::extractTextFromOCR)
                    .exceptionally(e -> {
                        e.printStackTrace();
                        showInfoAlert("Error", "Failed to extract text: " + e.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            e.printStackTrace();
            showInfoAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private void extractTextFromOCR(String jsonResponse) {
        Platform.runLater(() -> {
            try {
                JSONObject json = new JSONObject(jsonResponse);
                JSONArray regions = json.getJSONArray("regions");
                StringBuilder resultText = new StringBuilder();

                for (int i = 0; i < regions.length(); i++) {
                    JSONArray lines = regions.getJSONObject(i).getJSONArray("lines");
                    for (int j = 0; j < lines.length(); j++) {
                        JSONArray words = lines.getJSONObject(j).getJSONArray("words");
                        for (int k = 0; k < words.length(); k++) {
                            resultText.append(words.getJSONObject(k).getString("text")).append(" ");
                        }
                        resultText.append("\n");
                    }
                }

                needToTrans.setText(resultText.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                showInfoAlert("Parse Error", "Could not parse OCR result.");
            }
        });
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
            TextToSpeech.speak(text, "en-US-GuyNeural");
        } else {
            showInfoAlert("TTS Unavailable", "No original text available to speak.");
        }
    }

    @FXML
    private void handleSpeakTranslated(ActionEvent event) {
        String text = translated.getText();
        if (text != null && !text.isBlank()) {
            TextToSpeech.speak(text, "vi-VN-NamMinhNeural");
        } else {
            showInfoAlert("TTS Unavailable", "No translated text available to speak.");
        }
    }
}