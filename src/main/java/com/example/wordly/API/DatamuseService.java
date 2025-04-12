package com.example.wordly.API;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class DatamuseService {
    /**
     * in ra tu dong nghia.
     * @param word tu can tim.
     * @return tra ve tu dong nghia.
     */
    public static List<String> getSynonyms(String word) {
        return fetchWords("https://api.datamuse.com/words?rel_syn=" + word);
    }

    /**
     * in ra tu trai nghia.
     * @param word tu can tim.
     * @return tra ve tu trai nghia.
     */
    public static List<String> getAntonyms(String word) {
        return fetchWords("https://api.datamuse.com/words?rel_ant=" + word);
    }

    private static List<String> fetchWords(String apiUrl) {
        List<String> words = new ArrayList<>();
        try {
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

            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                words.add(obj.getString("word"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return words;
    }
}
