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

    /**
     * Chon che do cua game theo y muon cua nguoi choi.
     * @param gameMode Easy, Medium, Hard.
     */
    public GameManager(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * Load tu theo do kho cua game.
     * @param difficulty Easy, Medium, Hard.
     */
    public void loadNewWord(String difficulty) {
        gameMode.loadNewWord(difficulty);
    }

    /**
     * Tra ve tu can tim sau khi load tu file.
     * @return Word se bi xao tron.
     */
    public String getScrambledWord() {
        return gameMode.getScrambledWord();
    }

    /**
     * kiem tra du lieu nhap vao textfiel.
     * @param input tu duoc cho ngau nhien.
     * @return TRUE or FASLE.
     */
    public boolean checkAnswer(String input) {
        return gameMode.checkAnswer(input);
    }

    /**
     * Lay tu hien tai, xao tron.
     * @return Tu bi xao tron.
     */
    public String getCurrentWord() {
        return gameMode.getCurrentWord();
    }
}
