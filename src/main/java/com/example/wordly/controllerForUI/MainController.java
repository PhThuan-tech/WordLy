package com.example.wordly.controllerForUI;

import com.example.wordly.getWord.QuoteProvider;
import com.example.wordly.getWord.TipsProvider;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.Objects;

public class MainController extends BaseController {
    @FXML
    public Button menuScence;
    @FXML
    public Button HowToUse;
    @FXML
    public Button BuyPrenium;
    @FXML
    public Label quoteLabel;
    @FXML
    public VBox tipBox;
    @FXML
    private Label wordlyTitleLabel;
    private AudioClip clickSound;

    /**
     * Hàm khởi tạo CSS vào trong MainController.
     * Và tải âm thanh.
     */
    public void initialize() {
        loadStyles();

        loadAudio();

        randomTips();


        animations();


        if (menuScence != null) menuScence.setDisable(false);
        if (HowToUse != null) HowToUse.setDisable(false);
        if (BuyPrenium != null) BuyPrenium.setDisable(false);
    }

    private void loadStyles() {
        if (menuScence != null) {
            Scene scene = menuScence.getScene();
            if (scene != null) {
                scene.getStylesheets().add(Objects.requireNonNull(getClass()
                        .getResource("/com/example/wordly/styles/mainsce.css")).toExternalForm());
            }
        } else {
            System.out.println("FXML elements chưa được load đầy đủ khi initialize MainController.");
        }
    }

    private void loadAudio() {
        URL audioResource = getClass().getResource("/com/example/wordly/audio/TrollButton.mp3");
        if (audioResource != null) {
            clickSound = new AudioClip(audioResource.toString());
        } else {
            System.err.println("Không tìm thấy tài nguyên âm thanh: /com/example/wordly/audio/TrollButton.mp3");
        }
    }

    private void randomTips() {
        // quote daily them vao
        String randomQuote = QuoteProvider.getRandomQuote();
        quoteLabel.setText("\"" + randomQuote + "\"");

        // tips cho man hinh chinh
        List<String> randomTips = TipsProvider.getRandomTips(3);
        tipBox.getChildren().clear();
        tipBox.setStyle("-fx-font-size: 17px; -fx-text-fill: #8B7355;"); // to hơn

        for (String tip : randomTips) {
            Label tipLabel = new Label("💡 " + tip);
            tipLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8B7355;");
            tipLabel.setWrapText(true);
            tipBox.getChildren().add(tipLabel);
        }
    }

    private void animations() {
        // Áp dụng animation cho tiêu đề
        if (wordlyTitleLabel != null) {
            TranslateTransition titleTranslateTransition = new TranslateTransition(Duration.seconds(0.5), wordlyTitleLabel);
            titleTranslateTransition.setFromX(-800);
            titleTranslateTransition.setToX(0);
            FadeTransition titleFadeTransition = new FadeTransition(Duration.seconds(1), wordlyTitleLabel);
            titleFadeTransition.setFromValue(0);
            titleFadeTransition.setToValue(1);
            titleTranslateTransition.play();
            titleFadeTransition.play();
        } else {
            System.err.println("FXML element wordlyTitleLabel chưa được gán trong MainController.");
        }
        
        Duration buttonAnimationDuration = Duration.seconds(0.8);
        Duration delayBetweenButtons = Duration.seconds(0.2);

        if (menuScence != null) {
            menuScence.setTranslateY(-20);
            menuScence.setOpacity(0);

            TranslateTransition homeTranslate = new TranslateTransition(buttonAnimationDuration, menuScence);
            homeTranslate.setFromY(-20);
            homeTranslate.setToY(0);

            FadeTransition homeFade = new FadeTransition(buttonAnimationDuration, menuScence);
            homeFade.setFromValue(0);
            homeFade.setToValue(1);
            homeTranslate.play();
            homeFade.play();
        }

        if (HowToUse != null) {
            HowToUse.setTranslateY(-20);
            HowToUse.setOpacity(0);
            TranslateTransition instructionTranslate = new TranslateTransition(buttonAnimationDuration, HowToUse);
            instructionTranslate.setFromY(-20);
            instructionTranslate.setToY(0);
            FadeTransition instructionFade = new FadeTransition(buttonAnimationDuration, HowToUse);
            instructionFade.setFromValue(0);
            instructionFade.setToValue(1);
            instructionTranslate.setDelay(buttonAnimationDuration.add(delayBetweenButtons));
            instructionFade.setDelay(buttonAnimationDuration.add(delayBetweenButtons));
            instructionTranslate.play();
            instructionFade.play();

        }

        if (BuyPrenium != null) {
            BuyPrenium.setTranslateY(-20);
            BuyPrenium.setOpacity(0);

            TranslateTransition premiumTranslate = new TranslateTransition(buttonAnimationDuration, BuyPrenium);
            premiumTranslate.setFromY(-20);
            premiumTranslate.setToY(0);

            FadeTransition premiumFade = new FadeTransition(buttonAnimationDuration, BuyPrenium);
            premiumFade.setFromValue(0);
            premiumFade.setToValue(1);
            Duration totalDelayForPremium = buttonAnimationDuration.add(delayBetweenButtons).add(buttonAnimationDuration.add(delayBetweenButtons));
            premiumTranslate.setDelay(totalDelayForPremium);
            premiumFade.setDelay(totalDelayForPremium);
            premiumTranslate.play();
            premiumFade.play();
        } else {
            System.err.println("FXML element buyPremiumButton chưa được gán trong MainController.");
        }
    }

    // ===== XỬ LÍ KHI ẤN CÁC NUT =====
    public void switchToHomeScence(ActionEvent actionEvent) {
        stopSound();
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }

    public void handleGoToUsing(ActionEvent actionEvent) {
        stopSound();
        switchScene(actionEvent, "/com/example/wordly/View/UsingMehodView.fxml");
    }

    public void handleGoToSetting(ActionEvent actionEvent) {
        stopSound();
        switchScene(actionEvent, "/com/example/wordly/View/SettingView.fxml");
    }

    @FXML
    public void HandlePlaysound(ActionEvent actionEvent) {
        // Phát âm thanh
        if (clickSound != null) {
            clickSound.stop();
            clickSound.play();
        } else {
            System.err.println("Âm thanh chưa được load hoặc không tìm thấy tài nguyên âm thanh!");
        }
    }

    //PHUONG THUC DE DUNG NHAC
    private void stopSound() {
        if (clickSound != null && clickSound.isPlaying()) {
            clickSound.stop();
        }
    }
}