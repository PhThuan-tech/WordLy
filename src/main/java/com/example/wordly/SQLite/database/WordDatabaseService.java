package com.example.wordly.SQLite.database;

import com.example.wordly.getWord.WordDetails;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WordDatabaseService {
    private static final String DB_URL = "jdbc:sqlite:wordly.db";
    public WordDatabaseService() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found. Ensure it's in your classpath.");
            e.printStackTrace();
        }
    }

    private void setupDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS new_added_words (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "word TEXT NOT NULL UNIQUE," +
                "type TEXT," +
                "pronunciation TEXT," +
                "definition TEXT," + // Match your column name!
                "example TEXT," +
                "audio_link TEXT" +
                ");";

        String createIndexSQL = "CREATE INDEX IF NOT EXISTS idx_word_prefix ON new_added_words (word);";


        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Execute CREATE TABLE statement
            stmt.execute(createTableSQL);
            System.out.println("Table 'new_added_words' checked/created.");

            // Execute CREATE INDEX statement
            stmt.execute(createIndexSQL);
            System.out.println("Index 'idx_word_prefix' checked/created.");

        } catch (SQLException e) {
            System.err.println("Error setting up database tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String> getWordsStartingWith(String prefix, int limit) {
        List<String> matches = new ArrayList<>();
        if (prefix == null || prefix.trim().isEmpty()) {
            return matches;
        }
        String sql = "SELECT word FROM new_added_words WHERE word LIKE ? COLLATE NOCASE LIMIT ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, prefix.trim() + "%");
            pstmt.setInt(2, limit); // Set limit
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                matches.add(rs.getString("word"));
            }
        } catch (SQLException e) {
            System.err.println("Database error during prefix search: " + e.getMessage());
        }
        return matches;
    }

    public WordDetails getWordDetails(String word) {
        WordDetails details = null;
        if (word == null || word.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT word, type, pronunciation, definition, example, audio_link " +
                "FROM new_added_words WHERE word = ? COLLATE NOCASE";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, word.trim()); // Trim the word
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                details = new WordDetails(
                        rs.getString("word"),
                        rs.getString("type"),
                        rs.getString("pronunciation"),
                        rs.getString("definition"),
                        rs.getString("example"),
                        rs.getString("audio_link")
                );
            }

        } catch (SQLException e) {
            System.err.println("Database error fetching word details: " + e.getMessage());
        }
        return details;
    }
}