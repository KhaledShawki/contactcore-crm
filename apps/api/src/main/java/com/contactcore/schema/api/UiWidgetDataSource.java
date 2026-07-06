// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record UiWidgetDataSource(
        String key,
        String endpoint,
        Map<String, String> defaultParams
) {
    public UiWidgetDataSource {
        key = require(key, "key");
        endpoint = require(endpoint, "endpoint");
        defaultParams = copy(defaultParams);
    }

    public static UiWidgetDataSource of(String key, String endpoint) {
        return new UiWidgetDataSource(key, endpoint, Map.of());
    }

    private static Map<String, String> copy(Map<String, String> values) {
        if (values == null || values.isEmpty()) {
            return Map.of();
        }
        Map<String, String> copied = new LinkedHashMap<>();
        values.forEach((key, value) -> copied.put(require(key, "default parameter key"), value == null ? "" : value));
        return Collections.unmodifiableMap(copied);
    }

    private static String require(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
