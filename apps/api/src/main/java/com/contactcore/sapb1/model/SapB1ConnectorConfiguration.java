// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.model;

import com.contactcore.connector.domain.CrmConnectorInstance;
import com.contactcore.shared.api.InvalidRequestException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;

public record SapB1ConnectorConfiguration(
        String serviceLayerBaseUrl,
        String companyDb,
        Duration timeout
) {
    private static final int DEFAULT_TIMEOUT_MS = 30000;

    public static SapB1ConnectorConfiguration from(CrmConnectorInstance instance, ObjectMapper objectMapper) {
        try {
            JsonNode root = objectMapper.readTree(instance.getConfigJson());
            String baseUrl = text(root, "serviceLayerBaseUrl");
            String companyDb = text(root, "companyDb");
            int timeoutMs = root.path("timeoutMs").asInt(DEFAULT_TIMEOUT_MS);
            if (baseUrl.isBlank() || companyDb.isBlank()) {
                throw new InvalidRequestException("SAP B1 connector configuration is incomplete.");
            }
            return new SapB1ConnectorConfiguration(normalizeBaseUrl(baseUrl), companyDb, Duration.ofMillis(Math.max(1000, timeoutMs)));
        } catch (InvalidRequestException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new InvalidRequestException("SAP B1 connector configuration is invalid.");
        }
    }

    public String endpoint(String path) {
        String normalizedPath = path.startsWith("/") ? path.substring(1) : path;
        return serviceLayerBaseUrl + "/" + normalizedPath;
    }

    private static String text(JsonNode root, String field) {
        JsonNode node = root == null ? null : root.get(field);
        return node == null || node.isNull() ? "" : node.asText().trim();
    }

    private static String normalizeBaseUrl(String baseUrl) {
        String normalized = baseUrl.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
