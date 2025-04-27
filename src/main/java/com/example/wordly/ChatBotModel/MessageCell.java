package com.example.wordly.ChatBotModel;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class MessageCell extends ListCell<Message> {
    private final HBox hbox = new HBox();
    private final Label bubble = new Label();

    public MessageCell() {
        bubble.setWrapText(true);
        bubble.getStyleClass().add("message-bubble");
        hbox.getChildren().add(bubble);
    }

    @Override
    protected void updateItem(Message msg, boolean empty) {
        super.updateItem(msg, empty);
        if (empty || msg == null) {
            setGraphic(null);
        } else {
            bubble.setText(msg.getContent());
            // căn lề tùy user/bot
            if (msg.isUser()) {
                hbox.setAlignment(Pos.CENTER_RIGHT);
                bubble.getStyleClass().removeAll("bot-bubble");
                bubble.getStyleClass().add("user-bubble");
            } else {
                hbox.setAlignment(Pos.CENTER_LEFT);
                bubble.getStyleClass().removeAll("user-bubble");
                bubble.getStyleClass().add("bot-bubble");
            }
            setGraphic(hbox);
        }
    }
}
