package com.example.wordly.controllerForUI;

import com.azure.ai.vision.imageanalysis.ImageAnalysisClient;
import com.azure.ai.vision.imageanalysis.ImageAnalysisClientBuilder;
import com.azure.ai.vision.imageanalysis.models.ImageAnalysisResult;
import com.azure.ai.vision.imageanalysis.models.ReadResult;
import com.azure.ai.vision.imageanalysis.models.VisualFeatures;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.util.BinaryData;

import com.example.wordly.API.TextToSpeech;
import com.example.wordly.API.Translator;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.util.Duration;
import javafx.animation.PauseTransition;


public class TranslateAndTTSController extends BaseController {
    /* ============================ PHƯƠNG THỨC SWITCH VIEW ========================================================= */
    @FXML public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/MainView.fxml");
    }
    @FXML public void handleGotoGame(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml");
    }
    @FXML public void backToAdvance(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/Advance_Features.fxml");
    }

    /* ============================================================================================================== */




    /* ======================== API, ENDPOINT CỦA VISION VÀ SPEECH ================================================== */
    private static final String AZURE_VISION_ENDPOINT = "https://phamthaic3.cognitiveservices.azure.com/";
    private static final String AZURE_VISION_SUBSCRIPTION_KEY = "PYdx4cObbMBRA7HnXlw5OkigfrGJ14ORRnp15JywiJzAx2e1UmFSJQQJ99BDACqBBLyXJ3w3AAAFACOGVj20";
    private static final String AZURE_SPEECH_KEY = "7yjH0bCYVrqCNfQ1YVTPKny3YiXc1BdjT7kwhoohEwRF3EKB6xkRJQQJ99BDACqBBLyXJ3w3AAAYACOGqnah";
    private static final String AZURE_SPEECH_REGION = "southeastasia";

    /* ============================================================================================================== */




    /* ========================== PHƯƠNG THỨC KHỞI TẠO VIEW BAN ĐẦU ================================================= */
    @FXML private Button stopButton;
    @FXML private BorderPane rootPane;
    @FXML private Circle wave1;
    @FXML private Circle wave2;
    @FXML private Circle wave3;
    @FXML private Rectangle highlightBar;

    private final PauseTransition debounceTranslate = new PauseTransition(Duration.millis(500));



    @FXML
    public void initialize() {
        /** phần hiệu ứng hightlight của thanh language bar **/
        // Chọn mặc định “Phát hiện ngôn ngữ” đặt thanh hightlight
        ToggleButton firstBtn = (ToggleButton) sourceLangBox.getChildren().get(0);
        sourceLangGroup.selectToggle(firstBtn);

        // Đặt highlight ngay dưới nút đầu tiên sau khi layout xong
        Platform.runLater(() -> {
            // Đảm bảo rằng thanh highlight có chiều rộng bằng nút đầu tiên
            highlightBar.setWidth(firstBtn.getWidth());
            // Đặt thanh highlight đúng vị trí dưới nút đầu tiên
            highlightBar.setTranslateX(firstBtn.localToParent(0, 0).getX());
        });

        // Chọn mặc định "Việt" đặt thanh hightlight
        ToggleButton firstBt = (ToggleButton) targetLangBox.getChildren().get(0);
        targetLangGroup.selectToggle(firstBt); // Chọn ToggleButton đầu tiên

        // Đặt thanh highlight dưới nút đầu tiên sau khi layout xong
        Platform.runLater(() -> {
            // Đảm bảo rằng thanh highlight có chiều rộng bằng nút đầu tiên
            targetHighlightBar.setWidth(firstBt.getWidth());
            // Đặt thanh highlight đúng vị trí dưới nút đầu tiên
            targetHighlightBar.setTranslateX(firstBt.localToParent(0, 0).getX());
        });
        targetLangCode = "vi"; // Cập nhật mã ngôn ngữ đích mặc định là Tiếng Việt
        /** ------------------------------------------------------------------------------- **/

        /** hiệu ứng nút bấm khi di chuột cho view **/
        applyHoverEffectToAllButtons(rootPane);
        /** ------------------------------------------------------------------------------- **/

        /** ẩn các nút chức năng ban đầu chỉ hiện khi có yêu cầu tương ứng **/
        stopRecordingBtn.setVisible(false);
        copyButton.setVisible(false);
        clearInputButton.setVisible(false);
        /** ------------------------------------------------------------------------------- **/

        /** khởi tạo thread pool chạy nền để xử lý văn bản, dịch v.v **/
        executorService = Executors.newCachedThreadPool();
        /** ------------------------------------------------------------------------------- **/


        /** cặp nhật giao diện theo văn bản nhập vào **/
        // hiển thị nút xóa khi có văn bản
        needToTrans.textProperty().addListener((obs, oldText, newText) -> {
            clearInputButton.setVisible(!newText.isEmpty());
        });

        // hiểm thị nút copy khi có văn bản dịch
        translated.textProperty().addListener((obs, oldText, newText) -> {
            copyButton.setVisible(newText != null && !newText.isEmpty());
        });

        // gọi tự động dịch
        needToTrans.textProperty().addListener((obs, oldText, newText) -> {
            debounceTranslate.setOnFinished(e -> autoTranslate(newText));
            debounceTranslate.playFromStart();
        });
        /** ------------------------------------------------------------------------------- **/


        /** kiểm tra key api của đọc ảnh có chuẩn không **/
        if (AZURE_VISION_SUBSCRIPTION_KEY.contains("YOUR_API_KEY") || AZURE_VISION_ENDPOINT.contains("YOUR_ENDPOINT")) {
            if (dropPane != null) dropPane.setDisable(true);
            showInfoAlert("Cấu hình thiếu", "Vui lòng thiết lập Azure Vision API Key và Endpoint.");
        } else {
            imageAnalysisClient = new ImageAnalysisClientBuilder()
                    .credential(new AzureKeyCredential(AZURE_VISION_SUBSCRIPTION_KEY))
                    .endpoint(AZURE_VISION_ENDPOINT)
                    .buildClient();
            if (dropPane != null) dropPane.setDisable(false);
        }
        /** ------------------------------------------------------------------------------- **/


        /** xử lý sự kiện cho các nút bấm **/
        // phần pane chọn/kéo thả ảnh
        if (dropPane != null) {
            // gán sự kiện chọn ảnh
            dropPane.setOnMouseClicked(event -> openImageChooser());
            // gán sự kiện kéo thả ảnh
            dropPane.setOnDragOver(event -> {
                if (event.getGestureSource() != dropPane && event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
                }
                event.consume();
            });

            dropPane.setOnDragDropped(event -> {
                var db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    processImageFile(db.getFiles().get(0));
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }

        // gán sự kiện cho nút dừng nói
        stopButton.setOnAction(e -> handleStopSpeaking());
        /** ------------------------------------------------------------------------------- **/


        /** animation sóng của mic cho đẹp =)) **/
        createWaveAnimation(wave1, Duration.seconds(1), 0);
        createWaveAnimation(wave2, Duration.seconds(1), 300);
        createWaveAnimation(wave3, Duration.seconds(1), 600);
        /** ------------------------------------------------------------------------------- **/

    }

    /* ============================================================================================================== */





    /* =====================  PHƯƠNG THỨC XỬ LÝ HÌNH ẢNH  =========================================================== */
    @FXML private Button loadImageButton;
    @FXML private StackPane dropPane;
    private ImageAnalysisClient imageAnalysisClient;
    private ExecutorService executorService;



    // PHƯƠNG THỨC MỞ HÌNH ẢNH
    private void openImageChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
        );
        Stage stage = (Stage) rootPane.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            processImageFile(selectedFile);
        }
    }

    // XỬ LÝ ẢNH
    private void processImageFile(File file) {
        if (imageAnalysisClient == null) {
            showInfoAlert("Lỗi", "Dịch vụ phân tích ảnh chưa sẵn sàng.");
            return;
        }
        needToTrans.setText("Đang xử lý hình ảnh, vui lòng chờ...");

        // Task xử lý ảnh trong đa luồng
        Task<String> ocrTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                BinaryData imageData = BinaryData.fromFile(file.toPath());
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

    // PHƯƠNG THỨC XỬ LÝ LOGIC CỦA PANE DROP/ CHOOSE IMAGE
    @FXML
    private void showDropZone(ActionEvent event) {
        boolean isVisible = dropPane.isVisible();
        dropPane.setVisible(!isVisible);
        dropPane.setManaged(!isVisible);
    }

    /* ============================================================================================================== */





    /* ======================= PHƯƠNG THỨC XỬ LÝ LOGIC TRANSLATE ==================================================== */
    @FXML private TextArea needToTrans;
    @FXML private TextArea translated;
    @FXML private Button transButton;


    // Hàm xử lý khi user ấn nút dịch (Trong TH task auto translate không tự động dịch được)
    @FXML
    public void handleTranslating(ActionEvent actionEvent) {
        String textToTranslate = needToTrans.getText();
        if (textToTranslate == null || textToTranslate.trim().isEmpty()) {
            showInfoAlert("Thiếu văn bản", "Vui lòng nhập văn bản cần dịch.");
            return;
        }

        // Task String xử lý đa luồng
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


    // Phương thức tự động dịch từ ngôn ngữ nguồn sang ngôn ngữ đích cho giống google dịch :v
    private void autoTranslate(String text) {
        if (text == null || text.trim().isEmpty()) {
            translated.clear();
            return;
        }

        // Task String chạy bất đồng bộ xử lý đa luồng tránh treo máy
        Task<String> translateTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return Translator.translate(text, sourceLangCode, targetLangCode);
            }

            @Override
            protected void succeeded() {
                translated.setText(getValue());
            }

            @Override
            protected void failed() {
                translated.setText("Không thể dịch văn bản.");
            }
        };
        executorService.submit(translateTask);
    }

    /* ============================================================================================================== */





    /* ======================== PHƯƠNG THỨC XỬ LÝ TEXT TO SPEECH ==================================================== */
    @FXML private Button speak1Button;
    @FXML private Button speak2Button;
    // task xử lý đọc văn bản
    private Future<?> currentSpeechTask;


    // Hàm tts ở văn bản cần dịch
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

    // Hàm tts ở văn bản được dịch
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

    // PHƯƠNG THỨC NGẮT GIỌNG/ HỦY NÓI
    @FXML
    private void handleStopSpeaking() {
        if (currentSpeechTask != null && !currentSpeechTask.isDone()) {
            currentSpeechTask.cancel(true);
        }
        TextToSpeech.stop();
    }

    /* ============================================================================================================== */





    /* =================== PHƯƠNG THỨC XỬ LÝ GHI ÂM/ SPEECH TO TEXT ================================================= */
    private final StringBuilder recognitionBuffer = new StringBuilder();
    private SpeechRecognizer recognizer;
    @FXML private Button recordButton;
    @FXML private Button stopRecordingBtn;
    @FXML private AnchorPane recordingPane;
    @FXML private Label realTimeLabel;

    // task xử lý luồng ghi âm hiện tại
    private Future<?> currentRecognitionTask;



    // PHƯƠNG THỨC LOGIC RECORD
    @FXML
    private void handleStartRecording(ActionEvent event) {
        /** ẩn hiện các nút **/
        recordButton.setVisible(false);
        stopRecordingBtn.setVisible(true);
        recognitionBuffer.setLength(0); // reset buffer khi bắt đầu mới
        /** ------------------------------------------------------------------------------- **/

        /** cập nhật ui theo luồng **/
        Platform.runLater(() -> {
            recordingPane.setVisible(true);
            recordingPane.setManaged(true);
            realTimeLabel.setText("Đang nghe...");
            needToTrans.clear();
        });
        /** ------------------------------------------------------------------------------- **/


        /** task ghi âm **/
        // hủy task cũ nếu đang chạy
        if (currentRecognitionTask != null && !currentRecognitionTask.isDone()) {
            currentRecognitionTask.cancel(true);
        }

        // chạy task ghi âm mới
        currentRecognitionTask = executorService.submit(() -> {
            try {
                SpeechConfig config = SpeechConfig.fromSubscription(AZURE_SPEECH_KEY, AZURE_SPEECH_REGION);
                config.setSpeechRecognitionLanguage("en-US");

                AudioConfig audioConfig = AudioConfig.fromDefaultMicrophoneInput();
                recognizer = new SpeechRecognizer(config, audioConfig);

                // cập nhật ui cho user thấy đang nói gì
                recognizer.recognizing.addEventListener((s, e) -> {
                    String partial = e.getResult().getText();
                    Platform.runLater(() -> realTimeLabel.setText(partial));
                });

                // khi nói hoàn tất câu -> cập nhật vào recognitionbuffer -> cập nhật vào ui
                recognizer.recognized.addEventListener((s, e) -> {
                    String finalText = e.getResult().getText();
                    if (finalText != null && !finalText.isBlank()) {
                        recognitionBuffer.append(finalText).append(" ");
                        Platform.runLater(() -> {
                            realTimeLabel.setText(finalText);
                            needToTrans.setText(recognitionBuffer.toString().trim());
                        });
                    }
                });

                // nếu có lỗi hoặc bấm dừng -> dừng ghi âm
                recognizer.canceled.addEventListener((s, e) -> {
                    Platform.runLater(() -> {
                        realTimeLabel.setText("Đã huỷ hoặc có lỗi.");
                        stopListeningView();
                    });
                });

                // đóng giao diện pane ghi âm
                recognizer.sessionStopped.addEventListener((s, e) -> {
                    Platform.runLater(this::stopListeningView);
                });

                recognizer.startContinuousRecognitionAsync().get();
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showInfoAlert("Lỗi STT", e.getMessage()));
                stopListeningView();
            }
        });
    }

    // PHƯƠNG THỨC LOGIC DỪNG RECORD
    @FXML
    private void handleStopRecording() {
        stopRecordingBtn.setVisible(false);
        recordButton.setVisible(true);
        if (recognizer != null) {
            try {
                recognizer.stopContinuousRecognitionAsync().get();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                recognizer.close();
                recognizer = null;
            }
        }
        stopListeningView();
    }

    // PHƯƠNG THỨC ĐÓNG RECORD VIEW
    private void stopListeningView() {
        recordingPane.setVisible(false);
        recordingPane.setManaged(false);
    }

    // PHƯƠNG THỨC TẠO ANIMATION CỦA MICRO
    private void createWaveAnimation(Circle circle, Duration duration, int delayMillis) {
        ScaleTransition st = new ScaleTransition(duration, circle);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(2.5);
        st.setToY(2.5);
        st.setAutoReverse(false);

        FadeTransition ft = new FadeTransition(duration, circle);
        ft.setFromValue(0.3);
        ft.setToValue(0.0);

        ParallelTransition pt = new ParallelTransition(st, ft);
        pt.setCycleCount(Animation.INDEFINITE);
        pt.setDelay(Duration.millis(delayMillis));
        pt.play();
    }

    /* ============================================================================================================== */





    /* ====================== PHƯƠNG THỨC XỬ LÝ PHẦN CHỌN NGÔN NGỮ DỊCH ============================================= */
    @FXML private HBox sourceLangBox;
    @FXML private ToggleGroup sourceLangGroup;
    @FXML private MenuItem itemTrung;
    @FXML private MenuItem itemHan;
    private MenuItem selectedMenuItem = null;

    // Mã ngôn ngữ nguồn hiện tại, mặc định là detect
    private String sourceLangCode = "auto";


    @FXML private HBox targetLangBox;
    @FXML private Rectangle targetHighlightBar;
    @FXML private ToggleGroup targetLangGroup;

    // Mã ngôn ngữ đích mặc định
    private String targetLangCode = "vi";

    @FXML private MenuButton languageMenuButton;
    @FXML private MenuButton targetLangMenuButton;

    @FXML private Rectangle menuSourceHighlightBar;
    @FXML private Rectangle menuTargetHighlightBar;


    // Xử lý ngôn ngữ nguồn
    @FXML
    private void handleSourceLangSelection(ActionEvent event) {
        ToggleButton selected = (ToggleButton) event.getSource();

        // Tính khoảng cách X trong chính HBox của ToggleButton
        double newX = 0;
        for (Node node : sourceLangBox.getChildren()) {
            if (node == selected) break;
            newX += node.getBoundsInParent().getWidth() + sourceLangBox.getSpacing();
        }

        // Animate thanh highlight
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), highlightBar);
        tt.setToX(newX);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();

        highlightBar.setWidth(selected.getWidth());

        // Cập nhật mã ngôn ngữ nguồn
        switch (selected.getText()) {
            case "Anh" -> sourceLangCode = "en";
            case "Việt" -> sourceLangCode = "vi";
            case "Pháp" -> sourceLangCode = "fr";
            default -> sourceLangCode = "auto";
        }

        // Gọi dịch lại nếu có văn bản
        autoTranslate(needToTrans.getText());

        // Set lại hightlight bar khi được chọn lại
        highlightBar.setVisible(true);
    }

    // Xử lý khi chọn 1 MenuItem ở (Chọn ngôn ngữ khác)
    @FXML
    private void handleOtherLangSelection(ActionEvent event) {
        MenuItem clickedItem = (MenuItem) event.getSource();

        // Bỏ chọn toggle
        sourceLangGroup.selectToggle(null);
        highlightBar.setVisible(false);

        // Cập nhật label MenuButton
        languageMenuButton.setText(clickedItem.getText());

        // Cập nhật style chọn
        if (selectedMenuItem != null) {
            selectedMenuItem.getStyleClass().remove("selected-menu-item");
        }
        clickedItem.getStyleClass().add("selected-menu-item");
        selectedMenuItem = clickedItem;

        // Gán mã ngôn ngữ
        switch (clickedItem.getText()) {
            case "Trung" -> sourceLangCode = "zh";
            case "Hàn" -> sourceLangCode = "ko";
            case "Nhật" -> sourceLangCode = "ja";
            default -> sourceLangCode = "auto";
        }

        autoTranslate(needToTrans.getText());
    }


    @FXML private MenuItem selectedTargetMenuItem = null;
    // Xử lý ngôn ngữ đích
    @FXML
    private void handleTargetLangSelection(ActionEvent event) {
        ToggleButton selected = (ToggleButton) event.getSource();

        // Tính khoảng cách X trong chính HBox của ToggleButton
        double newX = 0;
        for (Node node : targetLangBox.getChildren()) {
            if (node == selected) break;
            newX += node.getBoundsInParent().getWidth() + targetLangBox.getSpacing();
        }

        // Animate thanh highlight
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), targetHighlightBar);
        tt.setToX(newX);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();

        targetHighlightBar.setWidth(selected.getWidth());

        // Gán mã ngôn ngữ đích
        switch (selected.getText()) {
            case "Việt" -> targetLangCode = "vi";
            case "Anh" -> targetLangCode = "en";
            case "Trung" -> targetLangCode = "zh";
            default -> targetLangCode = "auto";
        }

        // Gọi dịch lại nếu có văn bản
        autoTranslate(needToTrans.getText());

        // Hiện lại animation của hightlight bar khi đc chọn lại
        targetHighlightBar.setVisible(true);
    }

    @FXML
    private void handleOtherTargetLangSelection(ActionEvent event) {
        MenuItem clickedItem = (MenuItem) event.getSource();

        // Bỏ chọn toggle
        targetLangGroup.selectToggle(null);
        targetHighlightBar.setVisible(false);

        // Cập nhật label MenuButton
        targetLangMenuButton.setText(clickedItem.getText());

        // Cập nhật style nút
        if (selectedTargetMenuItem != null) {
            selectedTargetMenuItem.getStyleClass().remove("selected-menu-item");
        }
        clickedItem.getStyleClass().add("selected-menu-item");
        selectedTargetMenuItem = clickedItem;

        // Gán mã ngôn ngữ
        switch (clickedItem.getText()) {
            case "Pháp" -> targetLangCode = "fr";
            case "Hàn" -> targetLangCode = "ko";
            case "Nhật" -> targetLangCode = "ja";
            default -> targetLangCode = "auto";
        }

        autoTranslate(needToTrans.getText());
    }


    /* ============================================================================================================== */





    /* ==================== PHƯƠNG THỨC XỬ LÝ NÚT BẤM CỦA TEXT AREA ================================================= */
    @FXML private Button clearInputButton;
    @FXML private Button copyButton;
    @FXML private StackPane copyNotificationLabel;



    // Xử lý phím xóa văn bản
    @FXML
    private void handleClearInput() {
        needToTrans.clear();
    }

    // Xử lý phím copy văn bản
    @FXML
    private void handleCopy() {
        String text = translated.getText();
        if (text != null && !text.isEmpty()) {
            ClipboardContent content = new ClipboardContent();
            content.putString(text);
            Clipboard.getSystemClipboard().setContent(content);

            // Hiện label "Đã copy"
            copyNotificationLabel.setVisible(true);

            // Hiệu ứng mờ dần
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), copyNotificationLabel);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            PauseTransition stay = new PauseTransition(Duration.seconds(1.5));

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), copyNotificationLabel);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> copyNotificationLabel.setVisible(false));

            SequentialTransition sequence = new SequentialTransition(fadeIn, stay, fadeOut);
            sequence.play();
        }
    }

    /* ============================================================================================================== */






    // HIỆN CÁC THÔNG BÁO ALERT CẢNH BÁO
    private void showInfoAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}