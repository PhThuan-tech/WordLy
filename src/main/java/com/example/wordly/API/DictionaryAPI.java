package com.example.wordly.API;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class DictionaryAPI {

    // DictionaryDev nay la ban mini cua tbminh y tuong cung dung nhu datamuse
    // fetch duong dan, dung duong dan do de in ra meaning, type va example

    // khac la in ra het cac tu loai cua 1 tu luu vao danh sach
    public static String getDefinition(String word) {
        // su dung cu phap khac voi datamuse la + duoi word( tu ma minh nhap vao)
        String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
        StringBuilder result = new StringBuilder();

        // thuong se xay ra ngoai le MalformedURLException: Duong dan sai ki tu
        // Loi mang(khi ko co internet)
        // JSONException : dung duong dan nhung trang web bi loi (error 404 not found)
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // xu li yeu cau la lay du lieu = GET

            // doc tung dong luu vao respone
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // doc thi lai chia nho thanh cac y chinh : meanings, partOfSpeech va definitions (vi du)
            JSONArray arr = new JSONArray(response.toString());
            JSONObject obj = arr.getJSONObject(0);
            JSONArray meanings = obj.getJSONArray("meanings");

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
}