package com.example.wordly.GameController.ScrambleWord;

public class GameManager {
    private GameMode gameMode;

    public GameManager(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void loadNewWord(String difficulty) {
        gameMode.loadNewWord(difficulty);
    }

    public String getScrambledWord() {
        return gameMode.getScrambledWord();
    }

    public boolean checkAnswer(String input) {
        return gameMode.checkAnswer(input);
    }

    public String getCurrentWord() {
        return gameMode.getCurrentWord();
    }
}
