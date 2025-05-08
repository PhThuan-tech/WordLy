package com.example.wordly.API;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Translator {
    private static final String API_KEY = "6mmnlV67g5ndE3grbHYBMMUDMpN0jalMBIPktVIOszGUnUmgjBGrJQQJ99BDACqBBLyXJ3w3AAAbACOGD9zi";
    private static final String ENDPOINT = "https://api.cognitive.microsofttranslator.com";
    private static final String REGION = "southeastasia";

    public static String translate(String text, String from , String to) {
        try {
            String route;
            if (from == null || from.isEmpty() || from.equalsIgnoreCase("auto")) {
                route = "/translate?api-version=3.0&to=" + to;
            } else {
                route = "/translate?api-version=3.0&from=" + from + "&to=" + to;
            }

            URL url = new URL(ENDPOINT + route);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Ocp-Apim-Subscription-Key", API_KEY);
            conn.setRequestProperty("Ocp-Apim-Subscription-Region", REGION);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            JsonArray requestBodyArray = new JsonArray();
            JsonObject textObj = new JsonObject();
            textObj.addProperty("text", text);
            requestBodyArray.add(textObj);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBodyArray.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                throw new IOException("HTTP " + responseCode + ": " + errorResponse.toString());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }

            JsonArray jsonArray = JsonParser.parseString(responseBuilder.toString()).getAsJsonArray();
            JsonObject translation = jsonArray.get(0).getAsJsonObject()
                    .getAsJsonArray("translations").get(0).getAsJsonObject();
            return translation.get("text").getAsString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during translation: " + e.getMessage();
        }
    }
}
