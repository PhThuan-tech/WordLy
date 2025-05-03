package com.example.wordly.controllerForUI;

import com.azure.ai.vision.imageanalysis.ImageAnalysisClient;
import com.azure.ai.vision.imageanalysis.ImageAnalysisClientBuilder;
import com.azure.ai.vision.imageanalysis.models.ImageAnalysisOptions;
import com.azure.ai.vision.imageanalysis.models.ImageAnalysisResult;
import com.azure.ai.vision.imageanalysis.models.ReadResult;
import com.azure.ai.vision.imageanalysis.models.VisualFeatures;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.util.BinaryData;
import com.example.wordly.API.TextToSpeech;
import com.example.wordly.API.Translator;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TranslateAndTTSController extends BaseController {
    @FXML public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/MainView.fxml");
    }
    @FXML public void handleGoToSearch(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }
    @FXML public void handleGoToFavourite(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/FavouriteView.fxml");
    }
    @FXML public void handleGotoEdit(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/EditWordView.fxml");
    }
    @FXML public void handleGotoGame(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml");
    }
    @FXML public void handleGoToHistory(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/HistoryView.fxml");
    }
    @FXML public void handleGoToChat(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/ChatBot.fxml");
    }
    @FXML public void backToAdvance(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/Advance_Features.fxml");
    }

    @FXML private TextArea needToTrans;
    @FXML private TextArea translated;
    @FXML private Button transButton;
    @FXML private Button speak1Button;
    @FXML private Button speak2Button;
    @FXML private Button stopButton;
    @FXML private Button loadImageButton;

    private ImageAnalysisClient imageAnalysisClient;
    private ExecutorService executorService;
    private Future<?> currentSpeechTask;

    private static final String AZURE_VISION_ENDPOINT = "https://phamthaic3.cognitiveservices.azure.com/";
    private static final String AZURE_VISION_SUBSCRIPTION_KEY = "PYdx4cObbMBRA7HnXlw5OkigfrGJ14ORRnp15JywiJzAx2e1UmFSJQQJ99BDACqBBLyXJ3w3AAAFACOGVj20";

    @FXML
    public void initialize() {
        executorService = Executors.newCachedThreadPool();

        // Setup Azure Vision client
        if (AZURE_VISION_SUBSCRIPTION_KEY.contains("YOUR_API_KEY") || AZURE_VISION_ENDPOINT.contains("YOUR_ENDPOINT")) {
            loadImageButton.setDisable(true);
            showInfoAlert("Cấu hình thiếu", "Vui lòng thiết lập Azure Vision API Key và Endpoint.");
        } else {
            imageAnalysisClient = new ImageAnalysisClientBuilder()
                    .credential(new AzureKeyCredential(AZURE_VISION_SUBSCRIPTION_KEY))
                    .endpoint(AZURE_VISION_ENDPOINT)
                    .buildClient();
            loadImageButton.setDisable(false);
        }

        // Handle stop button
        stopButton.setOnAction(e -> handleStopSpeaking());
    }

    @FXML
    public void handleTranslating(ActionEvent actionEvent) {
        String textToTranslate = needToTrans.getText();
        if (textToTranslate == null || textToTranslate.trim().isEmpty()) {
            showInfoAlert("Thiếu văn bản", "Vui lòng nhập văn bản cần dịch.");
            return;
        }

        Task<String> translateTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return Translator.translate(textToTranslate, "auto", "vi");
            }
            @Override
            protected void succeeded() {
                translated.setText(getValue());
            }
            @Override
            protected void failed() {
                showInfoAlert("Lỗi dịch", getException().getMessage());
                translated.setText("Không thể dịch văn bản.");
            }
        };
        executorService.submit(translateTask);
    }

    @FXML
    private void handleLoadImage(ActionEvent event) {
        if (imageAnalysisClient == null) {
            showInfoAlert("Lỗi", "Dịch vụ phân tích ảnh chưa sẵn sàng.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
        );
        Stage stage = (Stage) loadImageButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            needToTrans.setText("Đang xử lý hình ảnh, vui lòng chờ...");
            Task<String> ocrTask = new Task<>() {
                @Override
                protected String call() throws Exception {
                    try {
                        BinaryData imageData = BinaryData.fromFile(selectedFile.toPath());
                        List<VisualFeatures> features = List.of(VisualFeatures.READ);
                        ImageAnalysisResult result = imageAnalysisClient.analyze(imageData, features, null);
                        ReadResult readResult = result.getRead();
                        StringBuilder extractedText = new StringBuilder();
                        if (readResult != null && readResult.getBlocks() != null) {
                            readResult.getBlocks().forEach(block -> {
                                if (block.getLines() != null) {
                                    block.getLines().forEach(line -> {
                                        if (line.getText() != null) {
                                            extractedText.append(line.getText()).append("\n");
                                        }
                                    });
                                }
                            });
                        }
                        return extractedText.toString().trim();
                    } catch (Exception e) {
                        String message = "Lỗi phân tích ảnh.";
                        if (e instanceof IOException || e.getCause() instanceof IOException) {
                            message = "Không thể đọc file ảnh.";
                        } else if (e instanceof HttpResponseException httpEx) {
                            message = "Lỗi từ Azure API: " + httpEx.getMessage();
                        }
                        throw new Exception(message, e);
                    }
                }
                @Override
                protected void succeeded() {
                    String result = getValue();
                    needToTrans.setText(result.isEmpty() ? "Không phát hiện văn bản trong ảnh." : result);
                }
                @Override
                protected void failed() {
                    showInfoAlert("Lỗi xử lý ảnh", getException().getMessage());
                    needToTrans.setText("Không thể xử lý ảnh.");
                }
            };
            executorService.submit(ocrTask);
        }
    }

    @FXML
    private void handleSpeakOriginal(ActionEvent event) {
        String text = needToTrans.getText();
        if (text != null && !text.isBlank()) {
            // cancel previous and stop audio
            if (currentSpeechTask != null && !currentSpeechTask.isDone()) {
                currentSpeechTask.cancel(true);
                TextToSpeech.stop();
            }
            currentSpeechTask = executorService.submit(() -> TextToSpeech.speak(text, "en-US-GuyNeural"));
        } else {
            showInfoAlert("Không có văn bản", "Không có văn bản gốc để đọc.");
        }
    }

    @FXML
    private void handleSpeakTranslated(ActionEvent event) {
        String text = translated.getText();
        if (text != null && !text.isBlank()) {
            if (currentSpeechTask != null && !currentSpeechTask.isDone()) {
                currentSpeechTask.cancel(true);
                TextToSpeech.stop();
            }
            currentSpeechTask = executorService.submit(() -> TextToSpeech.speak(text, "vi-VN-NamMinhNeural"));
        } else {
            showInfoAlert("Không có bản dịch", "Không có văn bản dịch để đọc.");
        }
    }

    @FXML
    private void handleStopSpeaking() {
        if (currentSpeechTask != null && !currentSpeechTask.isDone()) {
            currentSpeechTask.cancel(true);
        }
        TextToSpeech.stop();
    }

    private void showInfoAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}