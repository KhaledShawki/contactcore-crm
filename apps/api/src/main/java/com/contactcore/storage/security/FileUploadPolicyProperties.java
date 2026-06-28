// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.security;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "contactcore.upload-policy")
public record FileUploadPolicyProperties(
        @Min(1024) long maxProfileImageBytes,
        @Min(1024) long maxBusinessDocumentBytes
) {}
