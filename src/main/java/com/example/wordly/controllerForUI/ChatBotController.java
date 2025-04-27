package com.example.wordly.controllerForUI;

import com.example.wordly.ChatBotModel.GeminiResponse;


import com.example.wordly.ChatBotModel.Message;
import com.example.wordly.ChatBotModel.MessageCell;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ChatBotController extends BaseController {
    @FXML
    public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/view/MainView.fxml");
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
    public void handleGotoGame(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml");
    }

    @FXML
    public void handleGoToTranslateAndTTS(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/TranslateAndTTS.fxml");
    }

    @FXML
    public void handleGoToHistory(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/HistoryView.fxml");
    }

    @FXML
    public void handleGotoEdit(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/EditWordView.fxml");
    }

    @FXML private ListView<Message> chatListView;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;

    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    private final HttpClient http = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    private final Gson gson = new Gson();

    private static final String API_KEY = "AIzaSyC18uMuXLaa4V3JGRj1KrcEEf7TfMg-Fdk";
    private static final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    @FXML
    public void initialize() {
        chatListView.setItems(messages);
        chatListView.setCellFactory(lv -> new MessageCell());
        sendButton.setOnAction(e -> handleSendMessage());
        messageInput.setOnAction(e -> handleSendMessage());
    }

    @FXML
    private void handleSendMessage() {
        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;
        messages.add(new Message(text, true));
        messageInput.clear();
        sendButton.setDisable(true);

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                if (API_KEY == null || API_KEY.isBlank())
                    throw new IllegalStateException("Thiếu GEMINI_API_KEY");
                // build URL với key query param
                String url = ENDPOINT + "?key=" + API_KEY;
                // body theo spec: { "contents": [...] }
                String bodyJson = gson.toJson(Map.of(
                        "contents",
                        List.of(Map.of("parts", List.of(Map.of("text", text))))
                ));

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(15))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                        .build();
                HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() != 200) {
                    throw new RuntimeException("API lỗi " + resp.statusCode() + ": " + resp.body());
                }
                // parse simple response
                GeminiResponse respObj = gson.fromJson(resp.body(), GeminiResponse.class);
                StringBuilder sb = new StringBuilder();
                for (var cand : respObj.getCandidates()) {
                    for (var content : cand.getContents()) {
                        for (var part : content.getParts()) {
                            sb.append(part.getText());
                        }
                    }
                }

                String botReply = sb.toString();
                return botReply;
            }

            @Override
            protected void succeeded() {
                messages.add(new Message(getValue(), false));
                scrollToEnd();
                sendButton.setDisable(false);
            }

            @Override
            protected void failed() {
                Throwable ex = getException();
                ex.printStackTrace();  // **xem log chi tiết**
                messages.add(new Message("⚠️ Lỗi khi kết nối API: "
                        + ex.getMessage(), false));
                scrollToEnd();
                sendButton.setDisable(false);
            }
        };

        new Thread(task).start();
    }

    private void scrollToEnd() {
        Platform.runLater(() -> chatListView.scrollTo(messages.size()-1));
    }

    // lớp parse JSON
    private static class ResponseWrapper {
        Candidate[] candidates;
        static class Candidate { Output output; }
        static class Output { Content[] content; }
        static class Content { Part[] parts; }
        static class Part { String text; }
    }
}
