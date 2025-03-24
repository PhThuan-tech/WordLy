package com.example.demo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DictionaryService {
    private static final Map<String, wordDetails> data = new HashMap<>();

    static {
        data.put("calculate", new wordDetails( "calculate", "verb", "/ˈkæl.kjə.leɪt/",
                "to judge the number or amount of something by using the information that you already have, and adding, taking away, multiplying, or dividing numbers",
                "The cost is calculated as £5 million."));
        data.put("apply", new wordDetails("apply", "verb", "/ə-ˈplī/", "to put to use especially for some practical purpose", "We applied the ointment to the cut."));
        data.put("calculation", new wordDetails("calculation", "noun", "/ˌkal-kyə-ˈlā-shən/", "the process or act of calculating", "The calculation showed a profit of $500."));
        data.put("apple", new wordDetails("apple", "noun", "/ˈa-pəl/", "the fleshy usually rounded often rosy-colored edible pome fruit of a tree (genus Malus) of the rose family", "Would you like an apple?"));
        data.put("app", new wordDetails("app", "noun", "/ˈap/", "application; especially : an application designed for a mobile device (such as a smartphone)", "I downloaded a new app for my phone."));
        data.put("keyboard", new wordDetails("keyboard", "noun", "/ˈkē-ˌbȯrd/", "a rack of keys: such as a row of keys on a piano or organ or a usually manual device (such as on a typewriter) for originating keyboard input for entry into a computer", "She learned to play the keyboard at a young age."));
    }

    /**
     * cho danh sách 5 từ gần nhất (tìm kiếm thời gian thực).
     * @param input -> word need to be searched
     * @return list of 5 words
     */
    public static List<String> suggestWords(String input) {
        return data.keySet().stream().filter(word->word.startsWith(input)).limit(5).collect(Collectors.toList());
    }

    /**
     * Get information of searched word.
     * @param word input
     * @return exactly word
     */
    public static wordDetails search (String word) {
        return data.get(word);
    }

    public static void addHistory (String word) {
        try (BufferedWriter bw = new BufferedWriter( new FileWriter("history.txt", true))) {
            bw.write(word);
            bw.newLine();
            System.out.println("Da ghi " + word + " vao file");
        } catch (IOException e) {
            System.err.println("Khong the ghi");
        }
    }
}
