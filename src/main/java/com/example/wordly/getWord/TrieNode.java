package com.example.wordly.getWord;

// leet code co phan khai bao
public class TrieNode {
    // tao doi tuong chua cac ki tu tu a -> z
    // o phan trie se co vai cai kha kho hieu :v
    TrieNode[] children = new TrieNode[26];
    boolean isEndofWord;        // bien check xem tu do da ket thuc chua

    public TrieNode() {
        isEndofWord = false;
    }
}
