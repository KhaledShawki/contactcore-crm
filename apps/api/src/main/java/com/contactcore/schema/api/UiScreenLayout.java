// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import java.util.List;
import java.util.Objects;

public record UiScreenLayout(
        String type,
        List<UiLayoutSection> sections
) {
    public UiScreenLayout {
        type = require(type, "type");
        sections = sections == null ? List.of() : List.copyOf(sections);
    }

    public static UiScreenLayout dashboard(List<UiLayoutSection> sections) {
        return new UiScreenLayout("dashboard", sections);
    }

    private static String require(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
