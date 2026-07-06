// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import java.util.List;
import java.util.Objects;

public record UiLayoutSection(
        String key,
        String title,
        String titleKey,
        int columns,
        List<UiWidget> widgets,
        UiCapabilityReference requiredCapability,
        boolean visible
) {
    public UiLayoutSection {
        key = require(key, "key");
        title = normalizeOptional(title);
        titleKey = normalizeOptional(titleKey);
        if (columns < 1 || columns > 4) {
            throw new IllegalArgumentException("columns must be between 1 and 4");
        }
        widgets = widgets == null ? List.of() : List.copyOf(widgets);
    }

    public static UiLayoutSection of(String key, String title, String titleKey, int columns, List<UiWidget> widgets) {
        return new UiLayoutSection(key, title, titleKey, columns, widgets, null, true);
    }

    private static String require(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
    }
}
