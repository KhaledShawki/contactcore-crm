// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import java.util.Objects;

public record UiWidgetTableColumn(
        String key,
        String title,
        String titleKey,
        String valueKind
) {
    public UiWidgetTableColumn {
        key = require(key, "key");
        title = require(title, "title");
        titleKey = normalizeOptional(titleKey);
        valueKind = normalizeOptional(valueKind);
    }

    public static UiWidgetTableColumn of(String key, String title, String titleKey, String valueKind) {
        return new UiWidgetTableColumn(key, title, titleKey, valueKind);
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
