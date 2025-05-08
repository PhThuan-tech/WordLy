package com.example.wordly.GameController.Wordle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordDataLoader {
    // 2 duong dan luu file txt de lay word va hint
    // bo dictionarydev vi nghia no hoi kho
    private static final String WORDLIST_PATH = "/Word4WordleGame.txt";
    private static final String HINTS_PATH = "/Hints4WordleGame.txt";

    // gio can load cac tu truoc trong wordlist.
    public List<String> loadWordList() {
        List<String> words = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream(WORDLIST_PATH);
            if (inputStream == null) {
                System.out.println("File " + WORDLIST_PATH + " Không tìm thấy, mày tạo file chưa.");
                return words;
            }

            // neu co file thi dung buffeedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;    // doc tung dong de lay tu
            while((line = reader.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
            reader.close(); // doc xong thi close
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    // tuong tu voi file hint
    // do ko dung dictionarydev va hint la sau tu wordlist nen dung map
    public Map<String, String> loadHints() {
        Map<String, String> hints = new HashMap<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream(HINTS_PATH);
            if(inputStream == null) {
                System.out.println("File " + HINTS_PATH + "Không tìm thấy !");
                return hints;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                // chia doi line thanh 2 phan word:hint
                String[] parts = line.trim().split(":", 2);
                if (parts.length == 2) {
                    hints.put(parts[0].trim().toLowerCase(), parts[1].trim());
                } else {
                    System.out.println("Dòng này đang chứa kí tự ko đúng trong " + HINTS_PATH + ":" + line);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hints;
    }

    // khi chon do kho so luong tu se thay doi nen can 1 phuong thuc de loc
    // tu theo do kho
    public List<String> filterWordByLength(List<String> allWords, int length) {
        List<String> filteredList = new ArrayList<>();
        if (allWords == null || allWords.isEmpty()) {
            System.out.println("Danh sách từ đang trống, cần thêm từ vào file!!");
            return filteredList;
        }

        // gio can duyet cac tu theo danh sach dung voi kich thuoc
        for(String word : allWords) {
            if (word.length() == length) {
                filteredList.add(word);
            }
        }
        // neu ko co tu co kich thuoc da cho thi dung nhu nay
        if (filteredList.isEmpty()) {
            System.out.println("Từ bạn tìm ko khớp với kích thước " + length + " trong danh sách.");
        }
        return filteredList;
    }

}
