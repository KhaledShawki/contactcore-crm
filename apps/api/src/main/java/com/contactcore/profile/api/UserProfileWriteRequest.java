// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.profile.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfileWriteRequest(
        @NotBlank @Size(max = 255) String displayName,
        @Size(max = 64) String phone,
        @Size(max = 128) String jobTitle,
        String bio,
        @NotBlank @Size(max = 32) String locale,
        @NotBlank @Size(max = 64) String timezone
) {}
