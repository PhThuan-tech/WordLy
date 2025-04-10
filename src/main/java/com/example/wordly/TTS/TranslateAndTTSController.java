package com.example.wordly.TTS;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class TranslateAndTTSController {
    public TextArea needToTrans;
    public TextArea translated;
    public Button transButton;
    public Button speak1Button;
    public Button speak2Button;

    @FXML
    public void handleBackMain(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/view/MainView.fxml"));
        Parent mainView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(mainView);
    }

    @FXML
    public void handleGoToSearch(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/SearchView.fxml"));
        Parent searchView = null;
        try {
            searchView = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(searchView);
    }

    public void handleGoToFavourite(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/FavouriteView.fxml"));
        Parent favouriteView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(favouriteView);
    }

    @FXML
    public void handleGotoEdit(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/EditWordView.fxml"));
        Parent EditWordView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(EditWordView);
    }

    @FXML
    public void handleGotoGame(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/GameView.fxml"));
        Parent GameView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(GameView);
    }

    @FXML
    public void handleGoToHistory(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wordly/View/HistoryView.fxml"));
        Parent historyView = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.getScene().setRoot(historyView);
    }

    @FXML
    public void handleTranslating(ActionEvent actionEvent) throws IOException {
        String TEXT = needToTrans.getText();
        if (TEXT == null) {
            showErrorAlert("EMPTY", "PLEASE FILL TEXT");
            return;
        }

        transButton.setDisable(true);

        Task<String> transTask = new Task<>() {
            @Override
            protected String call() {
                return GetAPIForTTS.Translating(TEXT);
            }
        };

        transTask.setOnSucceeded(event -> {
            String translatedText = transTask.getValue();
            if (translatedText != null && !translatedText.isEmpty()) {
                System.out.println("Dịch thành công.");
                translated.setText(translatedText);
            } else {

                System.err.println("API dịch trả về kết quả không hợp lệ.");

                showErrorAlert("Lỗi Dịch Thuật", "Không thể dịch văn bản. API không trả về kết quả hợp lệ hoặc có lỗi xảy ra.");
            }
        });

        // Xử lý khi tác vụ thất bại (ví dụ: lỗi mạng, lỗi nghiêm trọng)
        transTask.setOnFailed(event -> {
            Throwable exception = transTask.getException();
            System.err.println("Tác vụ dịch thất bại: " + exception.getMessage());
            exception.printStackTrace(); // In chi tiết lỗi ra console để debug
            showErrorAlert("Lỗi Kết Nối/API", "Đã xảy ra lỗi khi kết nối đến dịch vụ dịch:\n" + exception.getMessage());
        });

        // Bắt đầu chạy tác vụ trên luồng mới
        new Thread(transTask).start();
    }

    private void showErrorAlert(String title, String content) {
        // Đảm bảo chạy trên luồng JavaFX Application Thread
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

}
