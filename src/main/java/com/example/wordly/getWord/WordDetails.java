package com.example.wordly.getWord;

import java.util.Objects;

public class WordDetails {
    private String word;
    private String type; // partOfSpeech
    private String phonetic; // phonetic text
    private String definition; // first definition
    private String example; // first example
    private String audioLink; // link to audio file

    // Constructor đầy đủ (tùy chọn cập nhật)
    public WordDetails(String word, String type, String phonetic, String definition, String example, String audioLink) {
        this.word = word;
        this.type = type;
        this.phonetic = phonetic;
        this.definition = definition;
        this.example = example;
        this.audioLink = audioLink;
    }

    public WordDetails() {
    }

    public WordDetails(String word, String wordType, String pronunciation, String definition) {
        this.word = word;
        this.type = wordType;
        this.phonetic = pronunciation;
        this.definition = definition;
    }

    // --- Getters and Setters ---
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(String audioLink) {
        this.audioLink = audioLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordDetails that = (WordDetails) o;
        return Objects.equals(word, that.word) &&
                Objects.equals(type, that.type) &&
                Objects.equals(phonetic, that.phonetic) &&
                Objects.equals(definition, that.definition) &&
                Objects.equals(example, that.example) &&
                Objects.equals(audioLink, that.audioLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, type, phonetic, definition, example, audioLink);
    }
}