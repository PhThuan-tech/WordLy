package com.example.wordly.API;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ChatService {
    private static final String API_KEY = "EiIbTHoA0DRlG5f35TOSraET7jSorzUrsuC7OKYDy5JpU0UIPnglJQQJ99BDACfhMk5XJ3w3AAAAACOGtWcU";
    private static final String ENDPOINT = "https://phamd-ma2tvlvx-swedencentral.cognitiveservices.azure.com/";
    private static final String DEPLOYMENT_ID = "gpt-4.1";
    private static final String API_VERSION = "2024-02-15-preview";

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static String sendMessage(String userInput) throws Exception {
        String url = String.format("%s/openai/deployments/%s/chat/completions?api-version=%s",
                ENDPOINT, DEPLOYMENT_ID, API_VERSION);

        // Tạo body JSON theo OpenAI format
        Map<String, Object> body = Map.of(
                "messages", List.of(
                        Map.of("role", "system", "content", """
            Bạn là một trợ lý giúp mình học tiếng Anh nói tiếng Việt. Bạn tự xưng là quái vật Tiếng Anh. 
            Nhiệm vụ của bạn là hỗ trợ người dùng học từ vựng, ngữ pháp, phát âm và luyện nói tiếng Anh.
            Hãy trả lời hoàn toàn bằng tiếng Việt, nhưng sử dụng ví dụ tiếng Anh rõ ràng.
            Luôn tỏ ra nguy hiểm giống như quái vật,nhưng giải thích dễ hiểu, và đưa ra ví dụ thực tế khi có thể.
            Hãy bắt đầu cuộc hội thoại trước bằng một lời chào và hỏi người dùng muốn học gì hôm nay như một con quái vật Tiếng Anh.
            Nhớ hãy tỏ ra thật nguy hiểm.
        """)
                ),
                "temperature", 0.7
        );
        String bodyJson = gson.toJson(body);

        // Tạo request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .header("api-key", API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Azure API error: " + response.statusCode() + "\n" + response.body());
        }

        // Parse kết quả
        Map<?, ?> responseMap = gson.fromJson(response.body(), Map.class);
        List<?> choices = (List<?>) responseMap.get("choices");
        if (choices == null || choices.isEmpty()) {
            return "Không có phản hồi từ Azure.";
        }

        Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
        return (String) message.get("content");
    }
}
