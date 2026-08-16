package ch.snorpcorp.lmstudiosender.sender;

import ch.snorpcorp.lmstudiosender.sender.errorHandling.LMStudioRequestFailedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class HTTPHelper {
    private final HttpClient client;
    private final ObjectMapper mapper;
    private Duration timeoutDuration = Duration.ofMinutes(5);

    public HTTPHelper() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.mapper = new ObjectMapper();
    }

    public <T, R> R post(String url, T requestPayload, Class<R> responseType) {
        return post(url, requestPayload, responseType, null);
    }

    public <T, R> R post(String url, T requestPayload, Class<R> responseType, String authHeader) {
        String jsonBody;

        try {
            jsonBody = mapper.writeValueAsString(requestPayload);
        } catch (JsonProcessingException e) {
            throw new LMStudioRequestFailedException("Unable to Parse input record to JSON: " + e.getMessage(), e.getCause(), url);
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(timeoutDuration)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .version(HttpClient.Version.HTTP_1_1);

        if (authHeader != null && !authHeader.trim().isEmpty()) {
            requestBuilder.header("Authorization", authHeader);
        }

        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new LMStudioRequestFailedException("Thread error: " + e.getMessage(), e.getCause(), url, jsonBody);
        } catch (IOException e) {
            throw new LMStudioRequestFailedException("Unable to send request: " + e.getMessage(), e.getCause(), url, jsonBody);
        }


        if (response.statusCode() >= 400) {
            throw new LMStudioRequestFailedException("HTTP request failed with status: " + response.statusCode() + " body: " + response.body(), url, jsonBody, response.statusCode());
        }

        try {
            return mapper.readValue(response.body(), responseType);
        } catch (JsonProcessingException e) {
            throw new LMStudioRequestFailedException("Unable to Parse output to record: " + e.getMessage(), e.getCause(), url, jsonBody, response.statusCode());
        }

    }

    public Duration getTimeoutDuration() {
        return timeoutDuration;
    }

    public void setTimeoutDuration(Duration timeoutDuration) {
        this.timeoutDuration = timeoutDuration;
    }
}
