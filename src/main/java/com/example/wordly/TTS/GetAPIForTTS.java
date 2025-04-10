package com.example.wordly.TTS;

import jdk.security.jarsigner.JarSigner;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GetAPIForTTS {
    private static final String BASE_URL = "https://libretranslate.com/translate";
    private static final String SOURCE = "auto";
    private static final String TARGET = "vi";
    private String TEXT = null;

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static String Translating(String TEXT) {
        if (TEXT == null  || TEXT.isEmpty()) {
            System.err.println("Trong");
            return TEXT;
        }

        try {
            JSONObject reqBody = new JSONObject();
            reqBody.put("q", TEXT);
            reqBody.put("source", SOURCE);
            reqBody.put("target", TARGET);
            reqBody.put("format", "text");
            //reqBody.put("alternatives", 3);
            //reqBody.put("api_key", "");

            String JSON_String = reqBody.toString();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(JSON_String, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            int code = response.statusCode();
            String responseBody = response.body();

            if (code == 200) {
                JSONObject jsonRes = new JSONObject(responseBody);
                if (jsonRes.has("translatedText")) {
                    return jsonRes.getString("translatedText");
                } else {
                    return TEXT;
                }
            } else {
                System.err.println(code);
                return TEXT;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
