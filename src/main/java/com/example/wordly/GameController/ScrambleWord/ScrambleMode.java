package com.example.wordly.GameController.ScrambleWord;

import java.util.*;

public class ScrambleMode implements GameMode {
    private String currentWord;
    private List<String> wordList;
    private Random random = new Random();

    public ScrambleMode(List<String> wordList) {
        this.wordList = wordList;
    }

    @Override
    public void loadNewWord(String difficulty) {
        int min, max;
        if (difficulty.equals("Dễ")) {
            min = 3;
            max = 5;
        } else if (difficulty.equals("Khó")) {
            min = 7;
            max = 10;
        } else {
            max = 6;
            min = 3;
        }

        List<String> filtered = wordList.stream()
                .filter(w -> w.length() >= min && w.length() <= max)
                .toList();

        if (filtered.isEmpty()) currentWord = "error";
        else currentWord = filtered.get(random.nextInt(filtered.size()));
    }

    @Override
    public String getScrambledWord() {
        List<Character> chars = new ArrayList<>();
        for (char c : currentWord.toCharArray()) chars.add(c);
        Collections.shuffle(chars);
        StringBuilder sb = new StringBuilder();
        for (char c : chars) sb.append(c);
        return sb.toString();
    }

    @Override
    public boolean checkAnswer(String input) {
        return input.equalsIgnoreCase(currentWord);
    }

    @Override
    public String getCurrentWord() {
        return currentWord;
    }
}
