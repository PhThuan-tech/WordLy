package com.example.wordly.getWord;

import javafx.beans.property.SimpleStringProperty;

public class WordEntry {
    private final SimpleStringProperty word;
    private final SimpleStringProperty pronunciation;
    private final SimpleStringProperty type;
    private final SimpleStringProperty meaning;

    public WordEntry(String word, String pronunciation, String type, String meaning) {
        this.word = new SimpleStringProperty(word);
        this.pronunciation = new SimpleStringProperty(pronunciation);
        this.type = new SimpleStringProperty(type != null ? type : ""); // Giá trị mặc định là chuỗi trống nếu null
        this.meaning = new SimpleStringProperty(meaning);
    }

    public String getWord() {
        return word.get();
    }


    public String getPronunciation() {
        return pronunciation.get();
    }

    public String getType() {
        return type.get().isEmpty() ? "N/A" : type.get(); // Nếu type là chuỗi trống, trả về "N/A"
    }

    public String getMeaning() {
        return meaning.get();
    }
}
