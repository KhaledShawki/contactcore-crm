// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "contactcore.assistant")
public record AssistantProperties(
        boolean enabled,
        @NotBlank String provider,
        @NotBlank String model,
        String endpoint,
        String apiKey,
        @Min(1000) int timeoutMs,
        @Min(1000) @Max(60000) int maxContextChars,
        @Min(1) @Max(100) int maxResults,
        @Min(1) @Max(120) int rateLimitPerMinute
) {}
