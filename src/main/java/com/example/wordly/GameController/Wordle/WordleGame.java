

package com.example.wordly.GameController.Wordle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map; // Keep this for potential future hint types, or remove if not needed

public class WordleGame {

    public enum LetterStatus {
        ABSENT,
        PRESENT,
        CORRECT
    }

    private String secretWord;
    private int wordLength;
    private int maxAttempts;
    private int currentAttempt;
    private boolean[] revealedPositions;
    private int positionalHintCount;

    private List<String> currentWordList;
    private List<String> allWords;

    private final int MAX_POSITIONAL_HINTS = 2;

    public WordleGame(List<String> allWords) {
        this.allWords = allWords;
        this.wordLength = 5;
        this.maxAttempts = 6;
        this.currentWordList = new ArrayList<>();
        this.revealedPositions = new boolean[wordLength];
        this.positionalHintCount = 0;
    }

    public void setDifficulty(int length, int attempts, List<String> filteredWordList) {
        this.wordLength = length;
        this.maxAttempts = attempts;
        this.currentWordList = filteredWordList;
        this.revealedPositions = new boolean[wordLength];
        reset();
    }

    public void reset() {
        currentAttempt = 0;
        positionalHintCount = 0;
        revealedPositions = new boolean[wordLength];
        if (currentWordList != null && !currentWordList.isEmpty()) {
            secretWord = currentWordList.get(new Random().nextInt(currentWordList.size()));
        } else {
            System.err.println("Error: Cannot select secret word. Filtered word list is empty for length " + wordLength);
            secretWord = "error".substring(0, Math.min(5, wordLength));
            System.err.println("Using fallback word: " + secretWord);
        }
        System.out.println("🤫 Từ bí mật: " + secretWord);
    }

    public LetterStatus[] processGuess(String guess) {
        if (guess == null || guess.length() != wordLength || currentAttempt >= maxAttempts) {
            System.err.println("Error: Called processGuess with invalid state (guess length or attempts).");
            return null;
        }

        LetterStatus[] results = new LetterStatus[wordLength];
        StringBuilder secretWordCopy = new StringBuilder(secretWord);
        for (int i = 0; i < wordLength; i++) {
            if (guess.charAt(i) == secretWord.charAt(i)) {
                results[i] = LetterStatus.CORRECT;
                secretWordCopy.setCharAt(i, '*');
            }
        }
        for (int i = 0; i < wordLength; i++) {
            if (results[i] == LetterStatus.CORRECT) {
                continue;
            }
            char guessChar = guess.charAt(i);
            int secretIndex = secretWordCopy.indexOf(String.valueOf(guessChar));
            if (secretIndex != -1) {
                results[i] = LetterStatus.PRESENT;
                secretWordCopy.setCharAt(secretIndex, '*');
            } else {
                results[i] = LetterStatus.ABSENT;
            }
        }
        currentAttempt++;
        return results;
    }

    public boolean isWin(String guess) {
        if (secretWord == null || guess == null) return false;
        return secretWord.equals(guess.toLowerCase());
    }

    public boolean isGameOver() {
        return currentAttempt >= maxAttempts;
    }

    public int calculateScore(int timeRemaining) {
        int attemptsLeft = Math.max(0, maxAttempts - currentAttempt);
        int validTimeRemaining = Math.max(0, timeRemaining);
        int attemptsBonus = attemptsLeft * 20;
        int timeBonus = validTimeRemaining / 5;
        return Math.max(10, attemptsBonus + timeBonus);
    }

    public int getPositionalHintIndex() {
        if (positionalHintCount >= MAX_POSITIONAL_HINTS) {
            return -1;
        }
        List<Integer> unrevealed = new ArrayList<>();
        for (int i = 0; i < wordLength; i++) {
            if (!revealedPositions[i]) {
                unrevealed.add(i);
            }
        }

        if (unrevealed.isEmpty()) {
            return -1;
        }
        int indexToReveal = unrevealed.get(new Random().nextInt(unrevealed.size()));
        revealedPositions[indexToReveal] = true;
        positionalHintCount++;
        return indexToReveal;
    }

    public boolean canUsePositionalHint() {
        if (positionalHintCount >= MAX_POSITIONAL_HINTS) return false;
        for(boolean revealed : revealedPositions) {
            if (!revealed) return true;
        }
        return false;
    }

    public String getSecretWord() {
        return secretWord;
    }

    public int getWordLength() {
        return wordLength;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public int getCurrentAttempt() {
        return currentAttempt;
    }

    public int getPositionalHintCount() {
        return positionalHintCount;
    }

    public int getMaxPositionalHints() {
        return MAX_POSITIONAL_HINTS;
    }
}