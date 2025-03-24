package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DictionaryService {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryService.class);

    // Changed the name to match more to what this does. Also removed the `throws Exception`
    public static wordDetails search(String word) {
        try {
            return API.searchingUsingAPI(word);
        } catch (Exception e) {
            logger.error("Error occurred during API search for word: {}", word, e);
            return null; // Returning null indicates that the search failed
        }
    }

    // Added better error handling and logging.
    public static void addHistory(String word) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("history.txt", true))) {
            bw.write(word);
            bw.newLine();
            logger.info("Successfully wrote word '{}' to history file.", word);
        } catch (IOException e) {
            logger.error("Could not write word '{}' to history file", word, e);
        }
    }

    //This now looks almost unused, is there a valid case for this function? if so it can be kept. if not, this can be removed to make the codebase easier to maintain.
    public static void searchUseAPI (String word){
        API.searchingUsingAPI(word);
    }
}