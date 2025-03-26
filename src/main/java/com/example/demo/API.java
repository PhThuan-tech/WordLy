package com.example.demo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class API {

    private static final Logger logger = LoggerFactory.getLogger(API.class);

    public static wordDetails searchingUsingAPI(String input) {
        wordDetails details = new wordDetails(); // Initialize even if API fails

        try {
            String url = "https://api.dictionaryapi.dev/api/v2/entries/en/" + input;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.error("API request failed with status code: {}", response.statusCode());
                return null; // Returning null indicates an API error
            }

            String jsonStr = response.body();
            JSONArray jsonArr = new JSONArray(jsonStr);
            JSONObject firstEntry = jsonArr.getJSONObject(0);

            details.setWord(getString(firstEntry, "word")); // Use helper function for safety

            // Phonetics
            if (firstEntry.has("phonetics")) {
                JSONArray phonetics = firstEntry.getJSONArray("phonetics");
                if (phonetics.length() > 0) {
                    JSONObject firstPhonetic = phonetics.getJSONObject(0);
                    details.setPronunciation(getString(firstPhonetic, "text")); // Use helper function
                }
            }

            // Meanings
            if (firstEntry.has("meanings")) {
                JSONArray meanings = firstEntry.getJSONArray("meanings");
                if (meanings.length() > 0) {
                    JSONObject meaning = meanings.getJSONObject(0);
                    details.setType(getString(meaning, "partOfSpeech")); //Use helper

                    if (meaning.has("definitions")) {
                        JSONArray definitions = meaning.getJSONArray("definitions");
                        if (definitions.length() > 0) {
                            JSONObject definitionObj = definitions.getJSONObject(0);
                            details.setUsage(getString(definitionObj, "definition"));//Use Helper
                            details.setExamples(getString(definitionObj, "example")); //use Helper

                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Exception during API call", e);
            return null; // or throw a custom exception.
        } catch (Exception e) {
            logger.error("Unexpected exception during API call", e);
            return null;
        }

        return details;
    }

    public static void pronunciationUsingAPI(String input) {
        try {
            String url_str = "https://api.dictionaryapi.dev/media/pronunciations/en/" + input + "-us.mp3";
            URL audioUrl = new URL(url_str);

            HttpURLConnection connect = (HttpURLConnection) audioUrl.openConnection();
            connect.setRequestMethod("GET");

            Media media = new Media(url_str);

            MediaPlayer mediaPlayer = new MediaPlayer(media);

            mediaPlayer.play();
        } catch (Exception e) {
            logger.error("error", e);
        }
    }

    private static String getString(JSONObject jsonObject, String key) {
        if (jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key)) {
            String value = jsonObject.getString(key);
            return (value != null && !value.isEmpty()) ? value : null;
        }
        return null;
    }


}