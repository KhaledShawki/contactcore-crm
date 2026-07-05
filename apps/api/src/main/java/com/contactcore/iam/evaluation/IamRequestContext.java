// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record IamRequestContext(Map<String, Object> values) {
    public static final IamRequestContext EMPTY = new IamRequestContext(Map.of());

    public IamRequestContext {
        values = values == null ? Map.of() : Map.copyOf(values);
    }

    public static IamRequestContext empty() {
        return EMPTY;
    }

    public Object get(String key) {
        return values.get(key);
    }

    public List<String> stringValues(String key) {
        Object value = values.get(key);
        if (value == null) {
            return List.of();
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream()
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .toList();
        }
        return List.of(String.valueOf(value));
    }

    public Boolean boolValue(String key) {
        Object value = values.get(key);
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof String text) {
            String normalized = text.trim().toLowerCase(java.util.Locale.ROOT);
            if ("true".equals(normalized)) {
                return true;
            }
            if ("false".equals(normalized)) {
                return false;
            }
        }
        return null;
    }

    public Instant instantValue(String key) {
        Object value = values.get(key);
        if (value instanceof Instant instant) {
            return instant;
        }
        if (value instanceof String text && !text.isBlank()) {
            try {
                return Instant.parse(text);
            } catch (java.time.format.DateTimeParseException ignored) {
                return null;
            }
        }
        return null;
    }
}
