// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.settings.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UiSettingsWriteRequest(
        @NotBlank @Size(max = 32) String theme,
        @NotBlank @Size(max = 32) String textSize,
        @NotBlank @Size(max = 32) String density,
        @NotBlank @Size(max = 32) String sidebarMode,
        boolean reduceMotion,
        boolean highContrast,
        @NotBlank @Size(max = 120) String defaultLandingPage
) {}
