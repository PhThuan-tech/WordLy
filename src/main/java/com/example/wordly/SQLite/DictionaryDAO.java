package com.example.wordly.SQLite;

import com.example.wordly.getWord.WordEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DictionaryDAO {

    public DictionaryDAO() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = """
                CREATE TABLE IF NOT EXISTS dictionary (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    word TEXT UNIQUE NOT NULL,
                    phonetic TEXT,
                    meaning TEXT
                );
            """;
            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thêm từ vào bảng dictionary
    public boolean addToDictionary(String word, String phonetic, String meaning) {
        String sql = """
            INSERT OR IGNORE INTO dictionary (word, phonetic, meaning)
            VALUES (?, ?, ?)
        """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, word);
            stmt.setString(2, phonetic);
            stmt.setString(3, meaning);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xoá từ khỏi dictionary
    public boolean removeFromDictionary(String word) {
        String sql = "DELETE FROM dictionary WHERE word = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, word);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kiểm tra từ đã có trong dictionary chưa
    public boolean isInDictionary(String word) {
        String sql = "SELECT 1 FROM dictionary WHERE word = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, word);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Truy vấn tất cả từ trong dictionary
    public List<WordEntry> getAllWords() {
        List<WordEntry> words = new ArrayList<>();
        String sql = "SELECT id, word, phonetic, meaning FROM dictionary";  // Lấy dữ liệu không có cột word_type

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String word = rs.getString("word");
                String pronunciation = rs.getString("phonetic");
                String meaning = rs.getString("meaning");
                // Không cần xử lý wordType nữa, vì nó đã bị loại bỏ

                words.add(new WordEntry(word, pronunciation,null, meaning));  // Chỉ truyền các tham số còn lại
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return words;
    }

}
