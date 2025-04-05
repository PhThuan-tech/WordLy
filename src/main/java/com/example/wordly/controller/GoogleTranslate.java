package com.example.wordly.controller;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GoogleTranslate {
    private static final String GOOGLE_TRANSLATE_URL = "https://translate.googleapis.com/translate_a/single";

    public static String translate(String text, String sourceLang, String targetLang) {
        try {
            // ma hoa van ban de dua vao URL
            String endcodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String url = GOOGLE_TRANSLATE_URL
                    + "?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + endcodedText;

            // request de yeu cau http get
            HttpResponse<JsonNode> response = Unirest.get(url).asJson();

            // xu li du lieu tra ve
            if (response.isSuccess()) {
                return response.getBody().getArray().getJSONArray(0).getJSONArray(0).getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  "Không thể dịch được đoạn văn";
    }
}
