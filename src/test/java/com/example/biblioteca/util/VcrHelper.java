package com.example.biblioteca.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Utilitário VCR para gravar e reproduzir interações HTTP nos testes.
 * Baseado no padrão Ruby VCR gem adaptado para Java/OkHttp.
 */
public class VcrHelper {

    private MockWebServer mockWebServer;
    private final String cassettePath;
    private final String cassetteName;
    private final boolean recordMode;
    private final ObjectMapper objectMapper;

    public VcrHelper(String cassettePath, String cassetteName, boolean recordMode) {
        this.cassettePath = cassettePath;
        this.cassetteName = cassetteName;
        this.recordMode = recordMode;
        this.mockWebServer = new MockWebServer();
        this.objectMapper = new ObjectMapper();
    }

    public void start() throws IOException {
        mockWebServer.start();
        if (!recordMode) {
            loadCassette();
        }
    }

    public void stop() throws IOException {
        if (recordMode) {
            saveCassette();
        }
        mockWebServer.shutdown();
    }

    public String getUrl(String path) {
        return mockWebServer.url(path).toString();
    }

    public OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    public String getMockServerBaseUrl() {
        return "http://localhost:" + mockWebServer.getPort();
    }

    private void loadCassette() throws IOException {
        Path cassetteFile = Paths.get(cassettePath, cassetteName + ".json");
        if (!Files.exists(cassetteFile)) {
            throw new IOException("Cassette não encontrado: " + cassetteFile);
        }
        String content = Files.readString(cassetteFile);
        enqueueResponseFromCassette(content);
    }

    private void saveCassette() throws IOException {
        Path dir = Paths.get(cassettePath);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        Files.writeString(dir.resolve(cassetteName + ".json"), "{}");
    }

    public void enqueueResponse(int statusCode, String body, String contentType) {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(statusCode)
                .setHeader("Content-Type", contentType)
                .setBody(body));
    }

    public void enqueueJsonResponse(int statusCode, String jsonBody) {
        enqueueResponse(statusCode, jsonBody, "application/json");
    }

    public RecordedRequest takeRequest() throws InterruptedException {
        return mockWebServer.takeRequest(5, TimeUnit.SECONDS);
    }

    private void enqueueResponseFromCassette(String content) {
        try {
            JsonNode cassette = objectMapper.readTree(content);
            JsonNode interactions = cassette.get("http_interactions");
            if (interactions != null && interactions.isArray() && !interactions.isEmpty()) {
                for (JsonNode interaction : interactions) {
                    JsonNode response = interaction.get("response");
                    if (response != null) {
                        int code = response.get("status").get("code").asInt();
                        String body = response.get("body").asText();
                        String ct = "application/json";
                        if (response.has("headers") && response.get("headers").has("Content-Type")) {
                            ct = response.get("headers").get("Content-Type").asText();
                        }
                        mockWebServer.enqueue(new MockResponse()
                                .setResponseCode(code)
                                .setHeader("Content-Type", ct)
                                .setBody(body));
                    }
                }
            }
        } catch (Exception e) {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(content));
        }
    }

    public MockWebServer getMockWebServer() { return mockWebServer; }
}
