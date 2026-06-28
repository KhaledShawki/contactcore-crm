// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.infrastructure;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "contactcore.s3")
public record S3StorageProperties(
        @NotBlank String endpoint,
        @NotBlank String publicEndpoint,
        @NotBlank String region,
        @NotBlank String bucket,
        @NotBlank String accessKey,
        @NotBlank String secretKey,
        boolean pathStyleAccess
) {}
