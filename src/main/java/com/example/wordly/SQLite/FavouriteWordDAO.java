package com.example.wordly.SQLite;

import com.example.wordly.getWord.WordEntry;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavouriteWordDAO {
    public FavouriteWordDAO() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = """
            CREATE TABLE IF NOT EXISTS favourite_words (
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

    // pt add words
    public boolean addFavouriteWord(String word, String phonetic, String word_type, String meaning) {
        String sql = """
                INSERT OR IGNORE INTO favourite_words
                                (word, phonetic, word_type, meaning)
                            VALUES (?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, word);
            stmt.setString(2, phonetic);
            stmt.setString(3, word_type);
            stmt.setString(4, meaning);
            return stmt.executeUpdate() > 0; // check hanh dong co xay ra? -> true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // pt xoa tu
    public boolean removeFavouriteWord(String word) {
        String sql = "Delete from favourite_words where word = ?";
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, word);
            return stmt.executeUpdate() > 0; // check
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // pt kiem tra tu nam trong ds yeu thich
    public boolean isFavouriteWord(String word) {
        String sql = "Select 1 from favourite_words where word = ?";
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, word);
            ResultSet result = stmt.executeQuery();
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // pt truy cap tu yeu thich
    public List<WordEntry> getAllFavourites() {
        List<WordEntry> words = new ArrayList<>();
        String sql = "SELECT * FROM favourite_words";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String word = rs.getString("word");
                String pronunciation = rs.getString("phonetic");
                String wordType = rs.getString("word_type");
                String meaning = rs.getString("meaning");
                words.add(new WordEntry(word, pronunciation, wordType, meaning));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return words;
    }
}
