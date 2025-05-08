package com.example.wordly.GameController.Wordle;

// dung Enum (Enumeration : kieu liet ke ) thay vi gan tung gia tri la :
// 0 la easy , 1 la medium, 2 la hard rat kho nho, nen dung enum la hop li ghi luon tung gia tri cu the.
public enum GameDifficulty {
    // tuy chon do kho, sua tai cho nay.
    EASY(5, 6, 90),
    MEDIUM(7, 6, 120),
    HARD(10, 6, 150);

    // cac tham so can khoi tao
    private final int wordLength;
    private final int maxAttempts;
    private final int timeLimitSeconds;

    GameDifficulty(int wordLength, int maxAttempts, int timeLimitSeconds) {
        this.wordLength = wordLength;
        this.maxAttempts = maxAttempts;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    // khong can set :v
    public int getWordLength() {
        return wordLength;
    }
    public int getMaxAttempts() {
        return maxAttempts;
    }
    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }
}
