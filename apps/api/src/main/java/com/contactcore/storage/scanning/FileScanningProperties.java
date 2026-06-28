// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.scanning;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "contactcore.file-scanning")
public record FileScanningProperties(
        boolean enabled,
        @NotBlank String host,
        @Min(1) @Max(65535) int port,
        @Min(100) int connectTimeoutMs,
        @Min(100) int readTimeoutMs,
        @Min(1024) int chunkSizeBytes
) {}
