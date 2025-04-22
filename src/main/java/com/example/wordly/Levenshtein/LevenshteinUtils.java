package com.example.wordly.Levenshtein;

public class LevenshteinUtils {
    /**
     * tính kc levenshtein giữa 2 chuỗi a, b.
     */
    public static int levenshteinDistance(String a, String b) {
        int n = a.length();
        int m = b.length();

        // mảng lưu kết quả số thao tác cần chuyển
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        // mảng thao tác kết quả
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                char charA = a.charAt(i - 1);
                char charB = b.charAt(j - 1);
                // nếu 2 ký tự giống nhau, không cần thay thế
                int cost;
                if (charA == charB) {
                    cost = 0;
                } else {
                    cost = 1; // 2 kí tự khác nhau thì thay
                }

                // thay thế
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1,     // xóa từ a
                                dp[i][j - 1] + 1),     // chèn vào a
                        dp[i - 1][j - 1] + cost);  // thay thế
            }
        }
        return dp[n][m];

    }

    /**
     * hàm check 2 chuỗi có gần giống nhau không dùng levenshteinDistance
     */
    public static boolean inThreshold(String src, String target, int threshold) {
        if (src == null || target == null) return false;
        int distance = levenshteinDistance(src.toLowerCase(), target.toLowerCase());
        return distance <= threshold;
    }
}
