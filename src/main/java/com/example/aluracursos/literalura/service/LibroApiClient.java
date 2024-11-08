package com.example.aluracursos.literalura.service;

import com.example.aluracursos.literalura.model.Libro;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LibroApiClient {

    private static final String API_URL = "https://gutendex.com/books/";  // URL de la API
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public LibroApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper(); // Inicializa ObjectMapper de Jackson
    }

    public Libro[] obtenerLibros() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Libro[].class); // Convierte JSON a objetos Libro[]
        } else {
            System.out.println("Error: " + response.statusCode());
            return new Libro[0];
        }
    }
}
