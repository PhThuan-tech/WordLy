package com.example.wordly.ChatBotModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Message {
    private final StringProperty content;
    private final boolean isUser;

    public Message(String content, boolean isUser) {
        this.content = new SimpleStringProperty(content);
        this.isUser = isUser;
    }

    public StringProperty contentProperty() {
        return content;
    }

    public String getContent() {
        return content.get();
    }

    public void setContent(String newContent) {
        this.content.set(newContent); // Update the content
    }

    public boolean isUser() {
        return isUser;
    }
}