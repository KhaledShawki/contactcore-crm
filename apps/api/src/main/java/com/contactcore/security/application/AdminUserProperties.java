// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.application;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "contactcore.admin")
public record AdminUserProperties(
        @NotBlank String username,
        @Email String email,
        @NotBlank String password,
        @NotBlank String displayName
) {}
