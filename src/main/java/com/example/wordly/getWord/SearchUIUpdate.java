package com.example.wordly.getWord;

import java.io.IOException;

public interface SearchUIUpdate {
    String getSearchTerm();

    void updateStatus(String message);

    void displayResult(WordDetails details) throws IOException;

    void clearResult();

    void resetSearchButton(boolean searching);
}
