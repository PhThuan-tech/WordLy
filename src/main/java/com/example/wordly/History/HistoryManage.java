package com.example.wordly.History;

import com.example.wordly.getWord.WordDetails;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class HistoryManage {
    private static final String HISTORY_FILE = "src/history.txt";
    private static final String GAME_FILE = "src/main/resources/game_data.txt";
    private static final String WORD_TRIE = "src/main/resources/com/example/wordly/ListOfWord";

    //Ghi thong tin vao file
    public void saveToHistory(WordDetails details) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(HISTORY_FILE, true))) {
            String line = formatWord(details);
            bw.write(line);
            bw.newLine();
            System.out.println("Ghi thanh cong " + details.getWord());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void saveToGame(WordDetails details) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(GAME_FILE, true))) {
            String line = formatWord(details);
            bw.write(line);
            bw.newLine();
            System.out.println("Ghi thanh cong " + details.getWord());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void saveWordToTrieFile(String word) {
        if (word == null || word.trim().isEmpty()) {
            System.out.println("Khong luu tu rong hoac null vao file Trie.");
            return;
        }

        String wordToSave = word.trim().toLowerCase();
        if (!wordToSave.matches("^[a-z]+$")) {
            System.err.println("Khong luu tu '" + word + "' vao file Trie: Chua ky tu khong hop le.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(WORD_TRIE, true), StandardCharsets.UTF_8))) {
            bw.write(wordToSave);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Lỗi khi tra từ trong file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Sua dinh dang cua tu
    private String formatWord(WordDetails details) {
        String word = escapeSpecialCharacter(details.getWord());
        String type = escapeSpecialCharacter(details.getType());
        String phonetic = escapeSpecialCharacter(details.getPhonetic());
        String definition = escapeSpecialCharacter(details.getDefinition());

        return String.join("\t", word, type, phonetic, definition);
            }

    private String escapeSpecialCharacter(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\t", " ").replace("\n", " ").replace("\r", " ");
    }
}
