package com.example.wordly.History;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WordEntry {
    private final StringProperty word;
    private final StringProperty type;
    private final StringProperty pronunciation;
    private final StringProperty definition;

    public WordEntry(String word, String type, String pronunciation, String meaning) {
        this.word = new SimpleStringProperty(word);
        this.type = new SimpleStringProperty(type);
        this.pronunciation = new SimpleStringProperty(pronunciation);
        this.definition = new SimpleStringProperty(meaning);
    }

    public String getWord() {
        return word.get();
    }

    public StringProperty wordProperty() {
        return word;
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public String getPronunciation() {
        return pronunciation.get();
    }

    public StringProperty pronunciationProperty() {
        return pronunciation;
    }

    public String getDefinition() {
        return definition.get();
    }

    public StringProperty definitionProperty() {
        return definition;
    }
}
