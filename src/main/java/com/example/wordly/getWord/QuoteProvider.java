package com.example.wordly.getWord;

import java.util.List;
import java.util.Random;

public class QuoteProvider {
    private static final List<String> QUOTES = List.of(
            "The limits of my language mean the limits of my world.",
            "Learning another language is like becoming another person.",
            "One language sets you in a corridor for life. Two languages open every door.",
            "You live a new life for every new language you speak.",
            "To learn a language is to have one more window to look at the world",
            "Read more, learn more, be more.",
            "Every expert was once a beginner.",
            "Small steps every day lead to big results.",
            "Practice makes progress, not perfect!",
            "Tung Tung Tung Sahur, Bombardiro Crocodilo, Sigma Boi, skibitoilet, Ballerina Cappuccina",
            "Contributor is PhThuan-Tech, tbmdemi, thai",
            "Học đi má, đừng lười nữa"
    );

    private static final Random random = new Random();

    public static String getRandomQuote() {
        return QUOTES.get(random.nextInt(QUOTES.size()));
    }
}
