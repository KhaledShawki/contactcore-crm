// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.settings.api;

public record UiSettingsResponse(
        Long id,
        Long userId,
        String theme,
        String textSize,
        String density,
        String sidebarMode,
        boolean reduceMotion,
        boolean highContrast,
        String defaultLandingPage
) {}
