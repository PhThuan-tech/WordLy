package com.example.wordly.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class TextToSpeechController {
    @FXML
    private TextArea inputText;
    @FXML
    private TextArea outputText;
    @FXML
    private ComboBox<String> sourceLang;
    @FXML
    private ComboBox<String> targetLang;

    public void handleTranslate() {
        String text = inputText.getText();
        if (text.isEmpty()) {
            System.out.println("Vui lòng nhập văn bản để dịch!");
            return;
        }
        // Lấy ngôn ngữ từ ComboBox
        String source = sourceLang.getValue();
        String target = targetLang.getValue();

        if (source == null || target == null) {
            System.out.println("Vui lòng chọn ngôn ngữ!");
            return;
        }

        // Dịch văn bản
        String translatedText = GoogleTranslate.translate(text, source, target);
        outputText.setText(translatedText);
    }

    public void handleTextToSpeech(ActionEvent actionEvent) {

    }
}
