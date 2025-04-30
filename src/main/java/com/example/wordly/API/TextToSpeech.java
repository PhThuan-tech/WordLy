package com.example.wordly.API;

import com.microsoft.cognitiveservices.speech.*;

public class TextToSpeech {
    private static final String API_KEY = "7yjH0bCYVrqCNfQ1YVTPKny3YiXc1BdjT7kwhoohEwRF3EKB6xkRJQQJ99BDACqBBLyXJ3w3AAAYACOGqnah";
    private static final String REGION = "southeastasia";

    private static SpeechSynthesizer synthesizer;

    static {
        try {
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(API_KEY, REGION);

            speechConfig.setSpeechSynthesisVoiceName("vi-VN-HoaiMyNeural");
            synthesizer = new SpeechSynthesizer(speechConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void speak(String text, String voiceName) {
        if (text == null || text.isBlank()) return;
        try {
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(API_KEY, REGION);

            speechConfig.setSpeechSynthesisVoiceName(voiceName);
            SpeechSynthesizer synthesizer = new SpeechSynthesizer(speechConfig);
            synthesizer.SpeakTextAsync(text).get();
            synthesizer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
