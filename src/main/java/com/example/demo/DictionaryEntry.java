package com.example.demo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DictionaryEntry {
    private String word;
    private String phonetic;
    private List<Meaning> definition;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public List<Meaning> getDefinition() {
        return definition;
    }

    public void setDefinition(List<Meaning> definition) {
        this.definition = definition;
    }

    public static class Meaning{
        @JsonProperty("partOfSpeech")
        private String partOfSpeech;
        private List<Definition> definitions;

        public String getPartOfSpeech() {
            return partOfSpeech;
        }

        public void setPartOfSpeech(String partOfSpeech) {
            this.partOfSpeech = partOfSpeech;
        }

        public List<Definition> getDefinitions() {
            return definitions;
        }

        public void setDefinitions(List<Definition> definitions) {
            this.definitions = definitions;
        }
    }

    public static class Definition {
        private String definition;
        private String example;

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
    }
}
