package com.example.wordly.getWord;

import javafx.scene.media.MediaPlayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.print.attribute.standard.Media;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GetAPI {
    private static final String API_BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/"; // URL goc de lay api
    private static final int CONNECT_TIMEOUT = 50000; // fifty seconds
    private static final int READ_TIMEOUT = 100000; // 1 thousand seconds

    /**
     * Get word details from API.
     *
     * @param word need to get
     * @return WordDetails
     * @throws IOException Error
     */
    public static WordDetails fetchWordDetails(String word) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // Make connection to API
            String encodedWord = URLEncoder.encode(word, StandardCharsets.UTF_8);
            String fullUrl = API_BASE_URL + encodedWord;
            System.out.println(fullUrl);
            URL url = new URL(fullUrl);

            //Open connection
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            //Get Response Code
            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);

            //Handle Response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return parseJsonResponse(response.toString(), word);
            } else {
                throw new IOException(word);
            }
        } catch (JSONException e) {
            throw new IOException(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            if (connection == null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Lay details tu file JSON cua api.
     *
     * @param jsonString   khong biet
     * @param originalWord tu
     * @return details
     * @throws JSONException loi
     */
    private static WordDetails parseJsonResponse(String jsonString, String originalWord) throws JSONException {
        WordDetails details = new WordDetails();
        //Đổi chuỗi thành JsonArray
        JSONArray jsonArray = new JSONArray(jsonString);

        // 2. Kiểm tra xem kết quả có rỗng không
        if (!jsonArray.isEmpty()) {
            // 3. Lấy đối tượng JSON đầu tiên (thường chỉ có 1 cho mỗi từ)
            JSONObject firstEntry = jsonArray.getJSONObject(0);

            // 4. Lấy từ (word) - thường dùng lại từ gốc hoặc lấy từ API nếu có
            details.setWord(firstEntry.optString("word", originalWord));

            // 5. Xử lý phiên âm (Phonetic) và Audio
            String phoneticText = null; // Khởi tạo là null để ưu tiên lấy từ "text"
            String audioLink = "";      // Khởi tạo là rỗng

            // Thử lấy từ mảng "phonetics" trước tiên
            JSONArray phoneticsArray = firstEntry.optJSONArray("phonetics");
            if (phoneticsArray != null) {
                for (int i = 0; i < phoneticsArray.length(); i++) {
                    JSONObject ph = phoneticsArray.getJSONObject(i);
                    // Ưu tiên lấy phiên âm từ key "text" nếu nó tồn tại và chưa tìm thấy
                    String currentPhonetic = ph.optString("text", null);
                    if (phoneticText == null && currentPhonetic != null && !currentPhonetic.isEmpty()) {
                        phoneticText = currentPhonetic;
                    }
                    // Lấy link audio đầu tiên tìm thấy và không rỗng
                    String currentAudio = ph.optString("audio", "");
                    if (audioLink.isEmpty() && !currentAudio.isEmpty()) {
                        audioLink = currentAudio;
                    }
                    // Có thể dừng nếu đã tìm thấy cả hai thông tin cần thiết
                    if (phoneticText != null && !audioLink.isEmpty()) {
                        break;
                    }
                }
            }

            // Nếu không tìm thấy phonetic từ key "text" trong mảng, thử lấy từ key "phonetic" cấp cao nhất
            if (phoneticText == null) {
                phoneticText = firstEntry.optString("phonetic", "N/A"); // Giá trị dự phòng
            }

            // Gán giá trị vào đối tượng details
            details.setPhonetic(phoneticText); // Đã có giá trị hoặc "N/A"
            details.setAudioLink(audioLink); // Có link hoặc chuỗi rỗng

            // 6. Xử lý Nghĩa (Meanings), Loại từ (Type), Định nghĩa (Definition), Ví dụ (Example)
            JSONArray meaningsArray = firstEntry.optJSONArray("meanings");
            if (meaningsArray != null && meaningsArray.length() > 0) {
                // Lấy đối tượng meaning đầu tiên
                JSONObject firstMeaning = meaningsArray.getJSONObject(0);
                details.setType(firstMeaning.optString("partOfSpeech", "N/A")); // Lấy loại từ

                // Lấy mảng definitions từ bên trong meaning
                JSONArray definitionsArray = firstMeaning.optJSONArray("definitions");
                if (definitionsArray != null && definitionsArray.length() > 0) {
                    // Lấy đối tượng definition đầu tiên
                    JSONObject firstDefinitionObj = definitionsArray.getJSONObject(0);
                    details.setDefinition(firstDefinitionObj.optString("definition", "N/A")); // Lấy định nghĩa
                    details.setExample(firstDefinitionObj.optString("example", "")); // Lấy ví dụ, mặc định rỗng nếu không có
                } else {
                    // Trường hợp không có mảng definitions
                    details.setDefinition("Không tìm thấy định nghĩa.");
                    details.setExample("");
                }
            } else {
                // Trường hợp không có mảng meanings
                details.setType("N/A");
                details.setDefinition("Không tìm thấy định nghĩa.");
                details.setExample("");
            }
        } else {
            throw new JSONException("API trả về dữ liệu rỗng.");
        }
        return details;
    }

    public static class WordNotFoundException extends IOException {
        public WordNotFoundException(String message) {
            super(message);
        }
    }

    public static void speakingUsingAPI(WordDetails details) {
        String audioLink = details.getAudioLink();
    }
}
