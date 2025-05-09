package com.example.wordly.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class DictionaryAPI {

    public static String getDefinition(String word) {
        String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
        StringBuilder result = new StringBuilder();

        try {
            JSONArray meanings = getObjects(apiUrl);

            for (int i = 0; i < meanings.length(); i++) {
                JSONObject meaning = meanings.getJSONObject(i);
                String partOfSpeech = meaning.getString("partOfSpeech");
                JSONArray definitions = meaning.getJSONArray("definitions");
                result.append("[").append(partOfSpeech).append("]\n");

                for (int j = 0; j < definitions.length(); j++) {
                    JSONObject def = definitions.getJSONObject(j);
                    result.append("- ").append(def.getString("definition")).append("\n");
                }
                result.append("\n");
            }

        } catch (Exception e) {
            result.append("Không tìm thấy nghĩa của từ: ").append(word);
        }

        return result.toString();
    }

    private static JSONArray getObjects(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONArray arr = new JSONArray(response.toString());
        JSONObject obj = arr.getJSONObject(0);
        JSONArray meanings = obj.getJSONArray("meanings");
        return meanings;
    }
}