package com.example.wordly.SQLite;

import com.example.wordly.getWord.WordEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DictionaryViEnDAO {

    public DictionaryViEnDAO() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = """
                CREATE TABLE IF NOT EXISTS "dictionaryViEn" (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    word TEXT,
                    html TEXT,
                    description TEXT,
                    pronounce TEXT
                );
            """;
            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<WordEntry> getAllWords() {
        List<WordEntry> words = new ArrayList<>();
        String sql = "SELECT word, description, pronounce FROM \"dictionaryViEn\"";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String word = rs.getString("word");
                String meaning = rs.getString("description");
                String phonetic = rs.getString("pronounce");

                words.add(new WordEntry(word, phonetic, null, meaning));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return words;
    }

    // Thêm từ vào bảng dictionaryViEn
    public boolean addToDictionaryViEn(String word, String meaning) {
        String sql = """
            INSERT OR IGNORE INTO dictionaryViEn (word, description)
            VALUES (?, ?)
        """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, word);
            stmt.setString(2, meaning);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xoá từ khỏi dictionary
    public boolean removeFromDictionaryViEn(String word) {
        String sql = "DELETE FROM dictionaryViEn WHERE word = ?";
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
    public boolean isInDictionaryViEn(String word) {
        String sql = "SELECT 1 FROM dictionaryViEn WHERE word = ?";
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

}
