package com.example.wordly.API;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

//API datamuse ngoai tim tu dong nghia, trai nghia thi con co tim tu dong am, tu nhieu nghia
// su dung fetch du lieu giong API dictionaryDEV.

public class DatamuseService {
    // fetch duong dan lay ra danh sach cac tu dong nghia.
    public static List<String> getSynonyms(String word) {
        return fetchWords("https://api.datamuse.com/words?rel_syn=" + word);
    }

    // fetch duong dan lay ra danh sach cac tu trai nghia.
    public static List<String> getAntonyms(String word) {
        return fetchWords("https://api.datamuse.com/words?rel_ant=" + word);
    }

    /**
     * Lay URL de su dung
     * @param apiUrl su dung URL, HttpURLConnection.
     * @return tra ve danh sach cac tu theo phan hoi.
     */
    private static List<String> fetchWords(String apiUrl) {
        List<String> words = new ArrayList<>();
        // thuong se xay ra ngoai le MalformedURLException: Duong dan sai ki tu
        // Loi mang(khi ko co internet)
        // JSONException : dung duong dan nhung trang web bi loi (error 404 not found)
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // xu li yeu cau lay = GET

            // doc du lieu thanh tung dong de lay ra cac tu, sau do append vao line.
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // them doi tuong vao words de tra ve danh sach cac tu.
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
