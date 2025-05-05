package com.example.wordly.GameController.ScrambleWord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

// lop nay  dung de doc file txt lay tu vung
// va duyet tu theo do dai (tung dong mot)

public class WordProvider {
    public List<String> loadWords() {
        List<String> words = new ArrayList<>();
        try (InputStream inputStream = getClass().getResourceAsStream("/word4scramble.txt")) {
            if (inputStream == null) {
                System.out.println("Không tìm thấy file words.txt trong resources.");
                return words;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            words = reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Đọc file word4scramble.txt thất bại.");
            e.printStackTrace();
        }
        return words;
    }
}
