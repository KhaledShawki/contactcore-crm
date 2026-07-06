// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record UiResourceCapabilities(
        String resourceKey,
        Map<String, Boolean> capabilities
) {
    public UiResourceCapabilities {
        resourceKey = require(resourceKey, "resourceKey");
        capabilities = copyCapabilities(capabilities);
    }

    public static UiResourceCapabilities empty(String resourceKey) {
        return new UiResourceCapabilities(resourceKey, Map.of());
    }

    public boolean allows(String capability) {
        if (capability == null || capability.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(capabilities.get(capability.trim()));
    }

    private static Map<String, Boolean> copyCapabilities(Map<String, Boolean> values) {
        if (values == null || values.isEmpty()) {
            return Map.of();
        }
        Map<String, Boolean> copied = new LinkedHashMap<>();
        values.forEach((key, value) -> copied.put(require(key, "capability key"), Boolean.TRUE.equals(value)));
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
