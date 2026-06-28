// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.config;

import com.contactcore.assistant.application.AssistantProperties;
import com.contactcore.shared.api.InvalidRequestException;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AssistantProviderConfigurationValidator implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssistantProviderConfigurationValidator.class);

    private final AssistantProperties properties;

    public AssistantProviderConfigurationValidator(AssistantProperties properties) {
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.enabled()) {
            LOGGER.info("ContactCore Assistant enabled=false provider={} model={}", properties.provider(), properties.model());
            return;
        }
        switch (properties.provider()) {
            case "noop" -> validateNoOp();
            case "generic-http" -> validateGenericHttp();
            default -> throw new InvalidRequestException("Unsupported assistant provider: " + properties.provider());
        }
        LOGGER.info(
                "ContactCore Assistant enabled=true provider={} model={} endpoint={}",
                properties.provider(),
                properties.model(),
                safeEndpoint()
        );
    }

    private void validateNoOp() {
        if (properties.model().isBlank()) {
            throw new InvalidRequestException("Assistant model must not be blank.");
        }
    }

    private void validateGenericHttp() {
        if (properties.endpoint() == null || properties.endpoint().isBlank()) {
            throw new InvalidRequestException("Assistant HTTP endpoint must be configured for provider generic-http.");
        }
        URI endpoint = URI.create(properties.endpoint());
        if (!"http".equalsIgnoreCase(endpoint.getScheme()) && !"https".equalsIgnoreCase(endpoint.getScheme())) {
            throw new InvalidRequestException("Assistant HTTP endpoint must start with http:// or https://.");
        }
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            throw new InvalidRequestException("Assistant API key must be configured for provider generic-http.");
        }
        if (properties.model().isBlank()) {
            throw new InvalidRequestException("Assistant model must be configured for provider generic-http.");
        }
    }

    private String safeEndpoint() {
        return properties.endpoint() == null || properties.endpoint().isBlank() ? "not-configured" : properties.endpoint();
    }
}
