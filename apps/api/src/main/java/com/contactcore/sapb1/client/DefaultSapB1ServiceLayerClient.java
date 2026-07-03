// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.client;

import com.contactcore.sapb1.model.SapB1ConnectorConfiguration;
import com.contactcore.sapb1.model.SapB1Session;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultSapB1ServiceLayerClient implements SapB1ServiceLayerClient {
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Autowired
    public DefaultSapB1ServiceLayerClient(ObjectMapper objectMapper) {
        this(objectMapper, HttpClient.newBuilder().build());
    }

    DefaultSapB1ServiceLayerClient(ObjectMapper objectMapper, HttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public SapB1Session login(SapB1ConnectorConfiguration configuration, String username, String password) {
        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "CompanyDB", configuration.companyDb(),
                    "UserName", username,
                    "Password", password
            ));
            HttpRequest request = HttpRequest.newBuilder(URI.create(configuration.endpoint("Login")))
                    .timeout(configuration.timeout())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ensureSuccessfulLogin(response);
            String b1Session = cookieValue(response, "B1SESSION");
            if (b1Session.isBlank()) {
                throw new SapB1ServiceLayerException("SAP B1 login did not return a session cookie.");
            }
            return new SapB1Session(username.trim(), b1Session, cookieValue(response, "ROUTEID"), Instant.now());
        } catch (SapB1ServiceLayerException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new SapB1ServiceLayerException("Could not connect to SAP B1 Service Layer.");
        }
    }

    @Override
    public void logout(SapB1ConnectorConfiguration configuration, SapB1Session session) {
        try {
            HttpRequest request = baseRequest(configuration, session, "Logout")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception ignored) {
            // Best-effort logout only. The local session is cleared by the caller regardless of upstream response.
        }
    }

    @Override
    public <T> T get(SapB1ConnectorConfiguration configuration, SapB1Session session, String resourcePath, Class<T> responseType) {
        try {
            String body = getBody(configuration, session, resourcePath);
            return objectMapper.readValue(body, responseType);
        } catch (SapB1ServiceLayerException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new SapB1ServiceLayerException("Could not deserialize SAP B1 Service Layer response.");
        }
    }

    @Override
    public <T> T post(SapB1ConnectorConfiguration configuration,
                      SapB1Session session,
                      String resourcePath,
                      Object requestBody,
                      Class<T> responseType) {
        try {
            String body = objectMapper.writeValueAsString(requestBody);
            HttpRequest request = baseRequest(configuration, session, resourcePath)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ensureSuccessfulRead(response);
            return objectMapper.readValue(response.body(), responseType);
        } catch (SapB1ServiceLayerException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new SapB1ServiceLayerException("Could not call SAP B1 Service Layer resource.");
        }
    }

    private String getBody(SapB1ConnectorConfiguration configuration, SapB1Session session, String resourcePath) {
        try {
            HttpRequest request = baseRequest(configuration, session, resourcePath)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ensureSuccessfulRead(response);
            return response.body();
        } catch (SapB1ServiceLayerException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new SapB1ServiceLayerException("Could not call SAP B1 Service Layer resource.");
        }
    }

    private HttpRequest.Builder baseRequest(SapB1ConnectorConfiguration configuration, SapB1Session session, String path) {
        return HttpRequest.newBuilder(URI.create(configuration.endpoint(path)))
                .timeout(configuration.timeout())
                .header("Accept", "application/json")
                .header("Cookie", session.cookieHeader());
    }

    private void ensureSuccessfulLogin(HttpResponse<String> response) {
        if (response.statusCode() == 401 || response.statusCode() == 403) {
            throw new SapB1ServiceLayerException("SAP B1 login failed. Check the selected company, username, and password.");
        }
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new SapB1ServiceLayerException("SAP B1 Service Layer login returned HTTP " + response.statusCode() + ".");
        }
    }

    private void ensureSuccessfulRead(HttpResponse<String> response) {
        if (response.statusCode() == 401 || response.statusCode() == 403) {
            throw new SapB1SessionExpiredException();
        }
        if (response.statusCode() == 404) {
            throw new SapB1ServiceLayerException("SAP B1 record was not found.");
        }
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new SapB1ServiceLayerException("SAP B1 Service Layer returned HTTP " + response.statusCode() + ".");
        }
    }

    private String cookieValue(HttpResponse<?> response, String cookieName) {
        return response.headers().allValues("set-cookie").stream()
                .map(header -> extractCookieValue(header, cookieName))
                .filter(value -> !value.isBlank())
                .findFirst()
                .orElse("");
    }

    private String extractCookieValue(String header, String cookieName) {
        if (header == null || header.isBlank()) {
            return "";
        }
        for (String part : header.split(";")) {
            String trimmed = part.trim();
            String prefix = cookieName + "=";
            if (trimmed.startsWith(prefix)) {
                return trimmed.substring(prefix.length()).trim();
            }
        }
        return "";
    }
}
