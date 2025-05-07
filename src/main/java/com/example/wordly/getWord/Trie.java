package com.example.wordly.getWord;

import java.util.ArrayList;
import java.util.List;

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode curr = root;
        for(char ch : word.toLowerCase().toCharArray()) {
            // Kiểm tra xem ký tự có phải là chữ cái hay không
            if (ch < 'a' || ch > 'z') {
                continue;  // Nếu không phải chữ cái, bỏ qua ký tự này
            }
            int i = ch - 'a';
            if (curr.children[i] == null) {
                curr.children[i] = new TrieNode();
            }
            curr = curr.children[i];
        }
        curr.isEndofWord = true;
    }

    public List<String> getSuggestions(String prefix) {
        List<String> results = new ArrayList<>();

        // Lọc từ chỉ giữ lại các chữ cái không dấu
        StringBuilder filteredPrefix = new StringBuilder();
        for (char ch : prefix.toLowerCase().toCharArray()) {
            if (ch >= 'a' && ch <= 'z') {
                filteredPrefix.append(ch);
            }
        }
        if (filteredPrefix.length() == 0) return results;

        TrieNode curr = root;
        for (char ch : filteredPrefix.toString().toLowerCase().toCharArray()) {
            int i = ch - 'a';
            // Nếu không tìm thấy ký tự trong trie, trả về danh sách rỗng
            if (curr.children[i] == null) {
                return results;  // Nếu không tìm thấy, return ngay lập tức
            }
            curr = curr.children[i];
        }
        // Tiếp tục tìm các từ từ nút hiện tại
        findWords(curr, filteredPrefix.toString(), results);
        return results;
    }

    private void findWords(TrieNode node, String prefix, List<String> results) {
        if (node == null) return; // Nếu node là null, kết thúc luôn.

        if (node.isEndofWord) {
            results.add(prefix);
        }

        for (char ch = 'a'; ch <= 'z'; ch++) {
            int i = ch - 'a';
            if (node.children[i] != null) {
                findWords(node.children[i], prefix + ch, results);
            }
        }
    }
}

