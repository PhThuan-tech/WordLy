package com.example.wordly.getWord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TipsProvider {
    private static final List<String> TIPS = List.of(
            "Listen to English every day – podcasts, songs, or movies!",
            "Learn new words by topic – it helps memory stick.",
            "Speak out loud – don’t be afraid to make mistakes.",
            "Repeat to remember – repetition is key.",
            "Use flashcards or apps to review vocabulary.",
            "Watch movies with English subtitles.",
            "Make sentences with new words you learn.",
            "Spend just 15 minutes a day learning English.",
            "Change your phone’s language to English.",
            "Learn with games or fun learning apps!"
    );

    // do lay ngau nhien 3 y trong nhieu muc co the bi trung lap
    // nen la ko dung random ma dung shuffle tron cac cau lai r lay 3.
    public static List<String> getRandomTips(int count) {
        List<String> shuffled = new ArrayList<>(TIPS);
        Collections.shuffle(shuffled); // trộn ngẫu nhiên
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }
}

