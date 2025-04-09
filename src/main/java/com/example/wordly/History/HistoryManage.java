package com.example.wordly.History;

import com.example.wordly.getWord.WordDetails;

import java.io.*;

/**
 * TODO : To save searched word into history file
 */
public class HistoryManage {
    private static final String HISTORY_FILE = "src/history.txt";

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
