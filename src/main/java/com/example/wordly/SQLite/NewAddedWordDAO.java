package com.example.wordly.SQLite;

import com.example.wordly.getWord.WordEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class NewAddedWordDAO {
    public NewAddedWordDAO() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS new_added_words (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    word TEXT UNIQUE NOT NULL,
                    phonetic TEXT,
                    word_type TEXT,
                    meaning TEXT                  
                    );
                    """;
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // pt add word
    public boolean addNewWord(String word, String phonetic, String word_type, String meaning) {
        String sql = """
                INSERT OR IGNORE INTO new_added_words (word, phonetic, word_type, meaning)
                VALUES (?, ?, ?, ?);
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, word);
            stmt.setString(2, phonetic);
            stmt.setString(3, word_type);
            stmt.setString(4, meaning);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //pt xoa tu
    public boolean removeAddedWord(String word) {
        String sql = "DELETE FROM new_added_words WHERE word = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, word);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //check tu da them hay chua?
    public boolean isAddedWord(String word) {
        String sql = "SELECT 1 FROM new_added_words WHERE word = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, word);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // pt lay toan bo tu da them
    public List<WordEntry> getAddedWords() {
        List<WordEntry> wordAddeds = new ArrayList<>();
        String sql = "SELECT * FROM new_added_words";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);) {

            while (rs.next()) {
                String word = rs.getString("word");
                String phonetic = rs.getString("phonetic");
                String word_type = rs.getString("word_type");
                String meaning = rs.getString("meaning");
                wordAddeds.add(new WordEntry(word, phonetic, word_type, meaning));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wordAddeds;
    }
}
