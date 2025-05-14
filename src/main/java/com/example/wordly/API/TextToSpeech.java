package com.example.wordly.API;

import com.microsoft.cognitiveservices.speech.*;

import java.util.concurrent.ExecutionException;

public class TextToSpeech {
    private static final String API_KEY = AppConfig.get("AZURE_TTS_KEY");
    private static final String REGION  = AppConfig.get("AZURE_TTS_REGION");

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
            return;
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
