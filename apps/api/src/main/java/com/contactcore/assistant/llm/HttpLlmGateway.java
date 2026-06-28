// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.llm;

import com.contactcore.assistant.application.AssistantProperties;
import com.contactcore.shared.api.InvalidRequestException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "contactcore.assistant", name = "provider", havingValue = "generic-http")
public class HttpLlmGateway implements LlmGateway {
    private final AssistantProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public HttpLlmGateway(AssistantProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(properties.timeoutMs()))
                .build();
    }

    @Override
    public LlmResponse complete(LlmRequest request) {
        if (properties.endpoint() == null || properties.endpoint().isBlank()) {
            throw new InvalidRequestException("Assistant HTTP provider endpoint is not configured.");
        }
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            throw new InvalidRequestException("Assistant HTTP provider API key is not configured.");
        }

        try {
            String payload = objectMapper.writeValueAsString(Map.of(
                    "model", request.model(),
                    "temperature", 0.1,
                    "messages", List.of(
                            Map.of("role", "system", "content", request.systemPrompt()),
                            Map.of("role", "user", "content", request.contextText() + "\n\nUSER_QUESTION:\n" + request.userMessage())
                    )
            ));

            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(properties.endpoint()))
                    .timeout(Duration.ofMillis(properties.timeoutMs()))
                    .header("Authorization", "Bearer " + properties.apiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new InvalidRequestException("Assistant provider returned HTTP " + response.statusCode() + ".");
            }

            JsonNode root = objectMapper.readTree(response.body());
            String content = root.at("/choices/0/message/content").asText("").trim();
            if (content.isBlank()) {
                throw new InvalidRequestException("Assistant provider returned an empty response.");
            }
            String modelName = root.path("model").asText(request.model());
            return new LlmResponse(content, modelName);
        } catch (IOException exception) {
            throw new InvalidRequestException("Assistant provider communication failed.");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new InvalidRequestException("Assistant provider request was interrupted.");
        }
    }
}
