package com.example.wordly.TTS;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LibreTranslator {

    private static String apiKey = "";

    // Constructor mặc định với apiKey rỗng
    public LibreTranslator() {
        this.apiKey = "";
    }

    // Constructor cho phép truyền apiKey nếu cần
    public LibreTranslator(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Dịch đoạn văn sang tiếng Việt bằng API LibreTranslate.
     *
     * @param text đoạn văn cần dịch
     * @return đoạn văn đã dịch (translatedText)
     * @throws IOException nếu xảy ra lỗi mạng hoặc phản hồi không hợp lệ
     */
    public static String translate(String text) throws IOException {
        // Khởi tạo OkHttpClient
        OkHttpClient client = new OkHttpClient();
        String url = "https://libretranslate.com/translate";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // Tạo request body dưới dạng Map
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("q", text);          // Đoạn văn cần dịch
        bodyMap.put("source", "auto");   // Ngôn ngữ nguồn: tự động phát hiện
        bodyMap.put("target", "vi");     // Ngôn ngữ đích: tiếng Việt
        bodyMap.put("format", "text");   // Định dạng văn bản
        bodyMap.put("alternatives", 3);  // Số lượng bản dịch thay thế (theo ví dụ)
        bodyMap.put("api_key", apiKey);  // API key (mặc định rỗng)

        // Chuyển Map thành chuỗi JSON
        String json = new ObjectMapper().writeValueAsString(bodyMap);
        RequestBody requestBody = RequestBody.create(json, JSON);

        // Xây dựng yêu cầu HTTP
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // Thực thi yêu cầu và xử lý phản hồi
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Mã lỗi không mong muốn: " + response);
            }

            // Lấy nội dung phản hồi và phân tích JSON
            String responseBody = response.body().string();
            JsonNode jsonNode = new ObjectMapper().readTree(responseBody);

            // Kiểm tra và trả về translatedText
            if (jsonNode.has("translatedText")) {
                return jsonNode.get("translatedText").asText();
            } else {
                throw new RuntimeException("Không tìm thấy translatedText trong phản hồi");
            }
        }
    }
}