package com.example.wordly.ChatBotMessage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;


public class MessageCell extends ListCell<Message> {

    private final HBox content;
    private final Label messageLabel;

    public MessageCell() {
        super();
        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400); // Giới hạn chiều rộng bong bóng chat

        content = new HBox();
        content.setPadding(new Insets(5));
    }

    @Override
    protected void updateItem(Message msg, boolean empty) {
        super.updateItem(msg, empty);

        if (empty || msg == null) {
            setGraphic(null);
        } else {
            messageLabel.setText(msg.getContent());

            if (msg.isUser()) {
                // Tin nhắn người dùng
                messageLabel.setStyle("-fx-background-color: #DCF8C6; -fx-background-radius: 12px; -fx-padding: 8px;");
                content.setAlignment(Pos.CENTER_RIGHT);
                content.getChildren().setAll(messageLabel);
            } else {
                // Tin nhắn của bot
                messageLabel.setStyle("-fx-background-color: #F1F0F0; -fx-background-radius: 12px; -fx-padding: 8px;");
                content.setAlignment(Pos.CENTER_LEFT);
                content.getChildren().setAll(messageLabel);
            }

            setGraphic(content);
        }
    }
}
