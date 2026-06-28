// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "contactcore.security.jwt")
public record JwtProperties(
        @NotBlank String issuer,
        @NotBlank String secret,
        @Min(5) long ttlMinutes
) {}
