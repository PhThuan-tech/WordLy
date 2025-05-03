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
    @FXML public void backToAdvance(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/Advance_Features.fxml");
    }

    @FXML private ListView<Message> chatListView;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;

    private final ObservableList<Message> messages = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        chatListView.setItems(messages);
        chatListView.setCellFactory(lv -> new MessageCell());

        // Các listener gửi tin nhắn của người dùng
        sendButton.setOnAction(e -> handleSendMessage());
        messageInput.setOnAction(e -> handleSendMessage());

        // Thread riêng để gọi API lấy lời chào
        new Thread(() -> {
            try {
                String greeting = ChatService.sendMessage("");
                Message botMsg = new Message("", false);
                Platform.runLater(() -> messages.add(botMsg));
                showTypingEffect(greeting, botMsg);
            } catch (Exception e) {
                Platform.runLater(() -> messages.add(new Message("Lỗi khi lấy lời chào.", false)));
                e.printStackTrace();
            }
        }).start();

    }

    // Hàm hiển thị "typing effect" cho tin nhắn
    private void showTypingEffect(String messageContent, Message targetMessage) {
        new Thread(() -> {
            for (int i = 0; i < messageContent.length(); i++) {
                final String currentText = messageContent.substring(0, i + 1);
                Platform.runLater(() -> {
                    targetMessage.setContent(currentText);
                    chatListView.refresh();
                });
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Platform.runLater(() -> {
                scrollToEnd();
                isBotTyping = false;
                sendButton.setDisable(false);
                sendButton.setText("Gửi");
            });
        }).start();
    }

    private volatile boolean isBotTyping = false;
    @FXML
    private void handleSendMessage() {
        if (isBotTyping) {
            System.out.println("Bot đang trả lời, không thể gửi tin nhắn lúc này.");
            return;
        }

        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;

        messages.add(new Message(text, true));
        messageInput.clear();

        new Thread(() -> {
            try {
                // Chặn gửi mới
                isBotTyping = true;
                Platform.runLater(() -> {
                    sendButton.setDisable(true);
                    sendButton.setText("...");
                });

                String reply = ChatService.sendMessage(text);
                Message botMsg = new Message("", false);
                Platform.runLater(() -> messages.add(botMsg));
                showTypingEffect(reply, botMsg);
            } catch (Exception e) {
                Platform.runLater(() -> messages.add(new Message("Lỗi khi lấy phản hồi từ trợ lý.", false)));
                e.printStackTrace();
                isBotTyping = false;
                Platform.runLater(() -> {
                    sendButton.setDisable(false);
                    sendButton.setText("Gửi");
                });
            }
        }).start();
    }

    private void scrollToEnd() {
        Platform.runLater(() -> chatListView.scrollTo(messages.size() - 1));
    }
}
