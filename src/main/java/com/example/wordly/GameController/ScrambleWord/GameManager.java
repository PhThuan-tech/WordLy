package com.example.wordly.GameController.ScrambleWord;

public class GameManager {
    private GameMode gameMode;

    /*
    Tạo các chức năng chính trong game như:
    + Chế độ chơi
    + Đọc từ trong file
    + Xáo trộn từ vừa đọc
    + Kiểm tra đáp án
    + Lựa chọn từ theo độ dài => phù hợp với mức độ chơi
     */

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
