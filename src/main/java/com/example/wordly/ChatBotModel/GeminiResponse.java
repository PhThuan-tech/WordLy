package com.example.wordly.ChatBotModel;

import com.google.gson.annotations.SerializedName;

public class GeminiResponse {
    @SerializedName("candidates")
    public Candidate[] candidates;

    public Candidate[] getCandidates() {
        return candidates;
    }

    public static class Candidate {
        @SerializedName("output")
        public Output output;

        @SerializedName("content")
        public Content directContent;

        public Content[] getContents() {
            if (output != null && output.contents != null) return output.contents;
            if (directContent != null) return new Content[]{ directContent };
            return new Content[0];
        }

    }

    public static class Output {
        @SerializedName("contents")
        public Content[] contents;
    }

    public static class Content {
        @SerializedName("parts")
        public Part[] parts;

        public Part[] getParts() {
            return parts;
        }
    }

    public static class Part {
        @SerializedName("text")
        public String text;

        public String getText() {
            return text;
        }
    }
}
