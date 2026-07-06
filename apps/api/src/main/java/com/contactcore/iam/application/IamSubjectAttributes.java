// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record IamSubjectAttributes(
        Long userId,
        String username,
        String email,
        String displayName,
        Map<String, String> values
) {
    public static final IamSubjectAttributes EMPTY = new IamSubjectAttributes(null, null, null, null, Map.of());

    public IamSubjectAttributes {
        username = blankToNull(username);
        email = blankToNull(email);
        displayName = blankToNull(displayName);
        values = copy(values);
    }

    public static IamSubjectAttributes empty() {
        return EMPTY;
    }

    public static IamSubjectAttributes user(Long userId, String username, String email) {
        return user(userId, username, email, null);
    }

    public static IamSubjectAttributes user(Long userId, String username, String email, String displayName) {
        return new IamSubjectAttributes(
                Objects.requireNonNull(userId, "userId must not be null"),
                username,
                email,
                displayName,
                Map.of()
        );
    }

    private static Map<String, String> copy(Map<String, String> values) {
        if (values == null || values.isEmpty()) {
            return Map.of();
        }
        Map<String, String> copied = new LinkedHashMap<>();
        values.forEach((key, value) -> {
            String safeKey = Objects.requireNonNull(key, "attribute key must not be null").trim();
            if (safeKey.isBlank()) {
                throw new IllegalArgumentException("attribute key must not be blank");
            }
            String safeValue = Objects.requireNonNull(value, "attribute value must not be null").trim();
            copied.put(safeKey, safeValue);
        });
        return Map.copyOf(copied);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
