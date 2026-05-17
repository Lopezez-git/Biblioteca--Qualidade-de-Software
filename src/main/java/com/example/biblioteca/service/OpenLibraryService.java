package com.example.biblioteca.service;

import com.example.biblioteca.entity.BookInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Serviço para buscar informações de livros na Open Library API.
 * Utilizado com o padrão VCR nos testes de integração.
 */
@Service
public class OpenLibraryService {

    private static final String OPEN_LIBRARY_URL = "https://openlibrary.org/api/books";

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OpenLibraryService() {
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // Constructor para injeção de cliente customizado (usado no VCR)
    public OpenLibraryService(OkHttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Busca informações de um livro pelo ISBN na Open Library API.
     */
    public BookInfo buscarPorIsbn(String isbn) throws IOException {
        String isbnLimpo = isbn.replaceAll("[^0-9X]", "");
        if (isbnLimpo.length() != 10 && isbnLimpo.length() != 13) {
            throw new IllegalArgumentException("ISBN inválido: deve ter 10 ou 13 dígitos");
        }

        String url = OPEN_LIBRARY_URL + "?bibkeys=ISBN:" + isbnLimpo + "&format=json&jscmd=data";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro na chamada à Open Library: " + response.code());
            }

            String body = response.body().string();
            return parseBookInfo(body, isbnLimpo);
        }
    }

    private BookInfo parseBookInfo(String json, String isbn) throws IOException {
        JsonNode root = objectMapper.readTree(json);
        String key = "ISBN:" + isbn;

        if (!root.has(key)) {
            throw new IOException("Livro não encontrado para ISBN: " + isbn);
        }

        JsonNode bookNode = root.get(key);
        BookInfo info = new BookInfo();
        info.setTitulo(bookNode.path("title").asText(null));

        if (bookNode.has("authors") && bookNode.get("authors").isArray()) {
            info.setAutor(bookNode.get("authors").get(0).path("name").asText(null));
        }

        if (bookNode.has("publishers") && bookNode.get("publishers").isArray()) {
            info.setEditora(bookNode.get("publishers").get(0).path("name").asText(null));
        }

        if (bookNode.has("publish_date")) {
            String dateStr = bookNode.get("publish_date").asText();
            try {
                // Tenta extrair apenas o ano
                String anoStr = dateStr.replaceAll("[^0-9]", "").substring(0, 4);
                info.setAnoPublicacao(Integer.parseInt(anoStr));
            } catch (Exception ignored) {}
        }

        if (bookNode.has("cover") && bookNode.get("cover").has("medium")) {
            info.setCapa(bookNode.get("cover").get("medium").asText(null));
        }

        return info;
    }
}
