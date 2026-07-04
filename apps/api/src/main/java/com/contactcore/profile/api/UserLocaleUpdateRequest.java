// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.profile.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLocaleUpdateRequest(
        @NotBlank @Size(max = 32) String locale
) {}
