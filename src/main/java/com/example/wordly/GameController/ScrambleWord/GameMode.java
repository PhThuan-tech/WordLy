package com.example.wordly.GameController.ScrambleWord;

public interface GameMode {
    String getScrambledWord();
    boolean checkAnswer(String input);
    String getCurrentWord();
    void loadNewWord(String difficulty);
}
