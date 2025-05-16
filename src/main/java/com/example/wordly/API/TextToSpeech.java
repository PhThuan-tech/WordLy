package com.example.wordly.API;

import com.microsoft.cognitiveservices.speech.*;

import java.util.concurrent.ExecutionException;

public class TextToSpeech {
    private static final String API_KEY = "7yjH0bCYVrqCNfQ1YVTPKny3YiXc1BdjT7kwhoohEwRF3EKB6xkRJQQJ99BDACqBBLyXJ3w3AAAYACOGqnah";      // your key
    private static final String REGION  = "southeastasia";

    private static SpeechSynthesizer synthSynth;
    private static final Object lock = new Object();

    public static void speak(String text, String voiceName) {
        if (text == null || text.isBlank()) return;

        synchronized (lock) {
            if (synthSynth != null) {
                synthSynth.StopSpeakingAsync();
                synthSynth.close();
            }
            SpeechConfig config = SpeechConfig.fromSubscription(API_KEY, REGION);
            config.setSpeechSynthesisVoiceName(voiceName);
            synthSynth = new SpeechSynthesizer(config);
        }

        try {
            synthSynth.SpeakTextAsync(text).get();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        } finally {
            synchronized (lock) {
                if (synthSynth != null) {
                    synthSynth.close();
                    synthSynth = null;
                }
            }
        }

    }

    public static void stop() {
        synchronized (lock) {
            if (synthSynth != null) {
                synthSynth.StopSpeakingAsync();
            }
        }
    }
}
