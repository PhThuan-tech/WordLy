

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
    private boolean[] revealedPositions; // Tracks positions revealed by positional hints
    private int positionalHintCount; // Count of positional hints used

    private List<String> currentWordList; // Words available for the current difficulty length
    private List<String> allWords; // Full dictionary for guess validation

    private final int MAX_POSITIONAL_HINTS = 2; // Max allowed positional hints per game

    /**
     * Constructor for WordleGame.
     * @param allWords The full list of words loaded from the dictionary file.
     */
    public WordleGame(List<String> allWords) {
        this.allWords = allWords;
        // Initial values (will be set correctly by setDifficulty)
        this.wordLength = 5;
        this.maxAttempts = 6;
        this.currentWordList = new ArrayList<>();
        this.revealedPositions = new boolean[wordLength];
        this.positionalHintCount = 0;
    }

    /**
     * Sets the game difficulty, filters the word list accordingly, and resets the game.
     * @param length The desired word length.
     * @param attempts The maximum number of attempts.
     * @param filteredWordList The list of words matching the desired length.
     */
    public void setDifficulty(int length, int attempts, List<String> filteredWordList) {
        this.wordLength = length;
        this.maxAttempts = attempts;
        this.currentWordList = filteredWordList;
        this.revealedPositions = new boolean[wordLength]; // Re-initialize based on new length
        reset(); // Reset game state for the new difficulty
    }

    /**
     * Resets the game state for a new round/level.
     * Selects a new secret word, resets attempt counter, hint count, and revealed positions.
     */
    public void reset() {
        currentAttempt = 0;
        positionalHintCount = 0;
        revealedPositions = new boolean[wordLength]; // Re-initialize array for the current wordLength

        // Select a new secret word from the current difficulty's list
        if (currentWordList != null && !currentWordList.isEmpty()) {
            secretWord = currentWordList.get(new Random().nextInt(currentWordList.size()));
        } else {
            // Fallback if no words for this length - this should ideally not happen
            // if data loading and filtering work correctly, but good for safety.
            System.err.println("Error: Cannot select secret word. Filtered word list is empty for length " + wordLength);
            secretWord = "error".substring(0, Math.min(5, wordLength)); // Use a fallback word
            System.err.println("Using fallback word: " + secretWord);
        }
        System.out.println("🤫 Từ bí mật: " + secretWord); // Log secret word for debugging
    }

    /**
     * Processes a player's guess against the secret word.
     * This method increments the attempt counter.
     * @param guess The guessed word (lowercase).
     * @return An array of LetterStatus indicating the status of each letter in the guess.
     *         Returns null if the guess length is incorrect or game is logically over (though controller should prevent this).
     */
    public LetterStatus[] processGuess(String guess) {
        if (guess == null || guess.length() != wordLength || currentAttempt >= maxAttempts) {
            // This indicates a logic error in the controller calling processGuess when game is over or guess invalid length
            System.err.println("Error: Called processGuess with invalid state (guess length or attempts).");
            return null;
        }

        LetterStatus[] results = new LetterStatus[wordLength];
        // Create a mutable copy of the secret word for marking letters that have been matched
        StringBuilder secretWordCopy = new StringBuilder(secretWord);

        // First pass: Check for correct letters (correct position)
        for (int i = 0; i < wordLength; i++) {
            if (guess.charAt(i) == secretWord.charAt(i)) {
                results[i] = LetterStatus.CORRECT;
                // Replace the matched character in the copy with a placeholder (e.g., *)
                // to prevent it from being matched again in the 'present' check
                secretWordCopy.setCharAt(i, '*');
            }
        }

        // Second pass: Check for present letters (wrong position)
        for (int i = 0; i < wordLength; i++) {
            if (results[i] == LetterStatus.CORRECT) {
                continue; // Already handled in the first pass
            }

            char guessChar = guess.charAt(i);
            int secretIndex = secretWordCopy.indexOf(String.valueOf(guessChar));

            if (secretIndex != -1) {
                results[i] = LetterStatus.PRESENT;
                // Replace the matched character in the copy with a placeholder
                secretWordCopy.setCharAt(secretIndex, '*');
            } else {
                results[i] = LetterStatus.ABSENT; // Not found in the secret word (or already matched)
            }
        }

        currentAttempt++; // Increment the attempt count AFTER processing the guess
        return results;
    }

    /**
     * Checks if the guess is a valid word in the full dictionary.
     * @param guess The word to check (lowercase).
     * @return true if the word exists in the dictionary, false otherwise.
     */
    public boolean isValidWord(String guess) {
        if (allWords == null || allWords.isEmpty()) {
            // Cannot validate, maybe assume valid or add more specific error handling
            System.err.println("Warning: Cannot validate guess '" + guess + "'. Dictionary not loaded.");
            return true; // Or false, depending on desired strictness - true is less frustrating for user
        }
        return allWords.contains(guess.toLowerCase());
    }


    /**
     * Checks if the player has won the game.
     * @param guess The final guess (lowercase).
     * @return true if the guess exactly matches the secret word, false otherwise.
     */
    public boolean isWin(String guess) {
        if (secretWord == null || guess == null) return false;
        return secretWord.equals(guess.toLowerCase());
    }

    /**
     * Checks if the game is over (either won or out of attempts).
     * Note: This doesn't check win condition based on the *current* guess,
     * it checks if max attempts are reached. The controller handles win check separately
     * right after processing the guess.
     * @return true if max attempts are reached, false otherwise.
     */
    public boolean isGameOver() {
        return currentAttempt >= maxAttempts;
    }

    /**
     * Calculates the score for the current level based on remaining attempts and time.
     * @param timeRemaining The time remaining in seconds for the current level.
     * @return The calculated score.
     */
    public int calculateScore(int timeRemaining) {
        // Ensure remaining attempts and time are non-negative
        int attemptsLeft = Math.max(0, maxAttempts - currentAttempt);
        int validTimeRemaining = Math.max(0, timeRemaining);

        int attemptsBonus = attemptsLeft * 20; // Points per remaining attempt
        int timeBonus = validTimeRemaining / 5; // Points per remaining second (scaled down)

        return Math.max(10, attemptsBonus + timeBonus); // Minimum score per level
    }

    /**
     * Provides a positional hint by revealing the index of an unrevealed character.
     * Marks the position as revealed and increments the hint count.
     * @return The index (0-based) of a character to reveal, or -1 if no hints left or all positions already revealed.
     */
    public int getPositionalHintIndex() {
        if (positionalHintCount >= MAX_POSITIONAL_HINTS) {
            return -1; // No hints left
        }

        List<Integer> unrevealed = new ArrayList<>();
        for (int i = 0; i < wordLength; i++) {
            if (!revealedPositions[i]) {
                unrevealed.add(i);
            }
        }

        if (unrevealed.isEmpty()) {
            return -1; // All positions already revealed
        }

        // Choose a random unrevealed position
        int indexToReveal = unrevealed.get(new Random().nextInt(unrevealed.size()));
        revealedPositions[indexToReveal] = true; // Mark as revealed

        positionalHintCount++; // Increment positional hint usage count
        return indexToReveal;
    }

    /**
     * Checks if a positional hint can currently be used.
     * @return true if hints are available and there are unrevealed positions, false otherwise.
     */
    public boolean canUsePositionalHint() {
        if (positionalHintCount >= MAX_POSITIONAL_HINTS) return false;

        // Check if there's any position left to reveal
        for(boolean revealed : revealedPositions) {
            if (!revealed) return true; // Found at least one unrevealed position
        }
        return false; // All positions already revealed
    }

    // --- Getters for WordleController to access game state ---

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

    // This getter is potentially useful for displayFileHint in the controller
    public List<String> getCurrentWordList() {
        return currentWordList;
    }

    // Optional: Get the full list if needed elsewhere (maybe not in Controller)
    public List<String> getAllWords() {
        return allWords;
    }
}