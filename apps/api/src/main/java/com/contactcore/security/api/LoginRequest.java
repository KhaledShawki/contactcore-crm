// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.api;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {}
