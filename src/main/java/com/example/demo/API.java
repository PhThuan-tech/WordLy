package com.example.demo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class API {
    public static void apiRequest(String input) throws IOException, InterruptedException {
        String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + input;

        HttpClient cilent = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).header("Accept", "application").build();

        HttpResponse<String> response = cilent.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String bodyWord = response.body();
            System.out.println(bodyWord);
        } else if (response.statusCode() == 404) {
            System.out.println("Word not found");
        } else {
            System.out.println("Error");
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        String inp = sc.next();
        apiRequest(inp);
    }
}
