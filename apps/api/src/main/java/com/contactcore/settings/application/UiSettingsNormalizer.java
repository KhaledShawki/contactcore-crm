// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.settings.application;

import com.contactcore.settings.api.UiSettingsWriteRequest;
import com.contactcore.shared.api.InvalidRequestException;
import java.util.Set;

record NormalizedUiSettingsInput(
        String theme,
        String textSize,
        String density,
        String sidebarMode,
        boolean reduceMotion,
        boolean highContrast,
        String defaultLandingPage
) {}

final class UiSettingsNormalizer {
    private static final Set<String> THEMES = Set.of("light", "dark", "ocean", "graphite");
    private static final Set<String> TEXT_SIZES = Set.of("compact", "comfortable", "large");
    private static final Set<String> DENSITIES = Set.of("compact", "comfortable", "spacious");
    private static final Set<String> SIDEBAR_MODES = Set.of("expanded", "compact");
    private static final Set<String> LANDING_PAGES = Set.of("/dashboard", "/customers", "/leads", "/suppliers", "/reports");

    private UiSettingsNormalizer() {}

    static NormalizedUiSettingsInput normalize(UiSettingsWriteRequest request) {
        String theme = oneOf(trimLower(request.theme()), THEMES, "theme");
        String textSize = oneOf(trimLower(request.textSize()), TEXT_SIZES, "text size");
        String density = oneOf(trimLower(request.density()), DENSITIES, "density");
        String sidebarMode = oneOf(trimLower(request.sidebarMode()), SIDEBAR_MODES, "sidebar mode");
        String defaultLandingPage = oneOf(trim(request.defaultLandingPage()), LANDING_PAGES, "default landing page");
        return new NormalizedUiSettingsInput(
                theme,
                textSize,
                density,
                sidebarMode,
                request.reduceMotion(),
                request.highContrast(),
                defaultLandingPage
        );
    }

    private static String oneOf(String value, Set<String> allowedValues, String field) {
        if (!allowedValues.contains(value)) {
            throw new InvalidRequestException("Unsupported " + field + ": " + value);
        }
        return value;
    }

    private static String trimLower(String value) {
        return trim(value).toLowerCase();
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
