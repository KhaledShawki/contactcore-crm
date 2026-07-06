// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import java.util.List;
import java.util.Objects;

public record UiScreenFilter(
        String key,
        String type,
        String label,
        String labelKey,
        String defaultValue,
        List<String> options,
        Integer min,
        Integer max
) {
    public UiScreenFilter {
        key = require(key, "key");
        type = require(type, "type");
        label = require(label, "label");
        labelKey = normalizeOptional(labelKey);
        defaultValue = normalizeOptional(defaultValue);
        options = options == null ? List.of() : List.copyOf(options);
        if (min != null && max != null && min > max) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
    }

    public static UiScreenFilter date(String key, String label, String labelKey) {
        return new UiScreenFilter(key, "date", label, labelKey, null, List.of(), null, null);
    }

    public static UiScreenFilter select(String key, String label, String labelKey, String defaultValue, List<String> options) {
        return new UiScreenFilter(key, "select", label, labelKey, defaultValue, options, null, null);
    }

    public static UiScreenFilter number(String key, String label, String labelKey, String defaultValue, int min, int max) {
        return new UiScreenFilter(key, "number", label, labelKey, defaultValue, List.of(), min, max);
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
