package com.example.wordly.controllerForUI;

import com.example.wordly.API.ChatService;
import com.example.wordly.ChatBotModel.Message;
import com.example.wordly.ChatBotModel.MessageCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

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

    @FXML
    public void initialize() {
        // Bind ListView và cell factory
        chatListView.setItems(messages);
        chatListView.setCellFactory(lv -> new MessageCell());

        // Các listener gửi tin nhắn của người dùng
        sendButton.setOnAction(e -> handleSendMessage());
        messageInput.setOnAction(e -> handleSendMessage());

        // Thread riêng để gọi API lấy lời chào
        new Thread(() -> {
            try {
                String greeting = ChatService.sendMessage(""); // Lời chào từ AI
                showTypingEffect(greeting, false); // Hiển thị lời chào dần dần
            } catch (Exception e) {
                Platform.runLater(() ->
                        messages.add(new Message("Lỗi khi lấy lời chào.", false))
                );
                e.printStackTrace();
            }
        }).start();
    }

    // Hàm hiển thị "typing effect" cho tin nhắn
    private void showTypingEffect(String messageContent, boolean isUser) {
        new Thread(() -> {
            Platform.runLater(() -> {
                messages.add(new Message("", isUser)); // Gửi tin nhắn rỗng để AI phản hồi trước
            });

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < messageContent.length(); i++) {
                final String currentText = messageContent.substring(0, i + 1);
                Platform.runLater(() -> {
                    if (!messages.isEmpty()) {
                        messages.get(messages.size() - 1).setContent(currentText);
                        chatListView.refresh();
                    }
                });
                try {
                    // Tốc độ gõ chữ
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(this::scrollToEnd);

        }).start();
    }
    @FXML
    private void handleSendMessage() {
        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;

        // Thêm tin nhắn người dùng vào danh sách
        Message userMsg = new Message(text, true);
        messages.add(userMsg);
        messageInput.clear();

        // Gọi API và hiển thị phản hồi từ trợ lý dần dần
        new Thread(() -> {
            try {
                String reply = ChatService.sendMessage(text); // Gửi tin nhắn tới API
                showTypingEffect(reply, false); // Hiển thị phản hồi từ AI dần dần
            } catch (Exception e) {
                Platform.runLater(() ->
                        messages.add(new Message("Lỗi khi lấy phản hồi từ trợ lý.", false))
                );
                e.printStackTrace();
            }
        }).start();
    }

    private void scrollToEnd() {
        Platform.runLater(() -> chatListView.scrollTo(messages.size() - 1));
    }

}
