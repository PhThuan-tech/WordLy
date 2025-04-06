package com.example.wordly.getWord;

public interface SearchUIUpdate {
    String getSearchTerm();

    void updateStatus(String message);

    void displayResult(WordDetails details);

    void clearResult();

    void resetSearchButton(boolean searching);
}
