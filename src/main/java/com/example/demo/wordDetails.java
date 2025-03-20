package com.example.demo;

public class wordDetails {
    private String word;
    private String type;
    private String pronunciation;
    private String usage;
    private String examples;

    public wordDetails(String word,String type,String pronunciation,String usage, String examples) {
        this.word = word;
        this.type = type;
        this.pronunciation = pronunciation;
        this.usage = usage;
        this.examples = examples;
    }

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

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getExamples() {
        return examples;
    }

    public void setExamples(String examples) {
        this.examples = examples;
    }
}
