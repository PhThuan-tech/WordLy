package com.example.wordly.GameController.ScrambleWord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class WordProvider {
    public List<String> loadWords() {
        List<String> words = new ArrayList<>();

        try (InputStream inputStream = getClass().getResourceAsStream("/words.txt")) {
            if (inputStream == null) {
                System.out.println("Không tìm thấy file words.txt trong resources.");
                return words;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            words = reader.lines().collect(Collectors.toList());

        } catch (IOException e) {
            System.out.println("Đọc file words.txt thất bại.");
            e.printStackTrace();
        }

        return words;
    }
}
