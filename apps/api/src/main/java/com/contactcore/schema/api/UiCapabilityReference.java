// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import java.util.Objects;

public record UiCapabilityReference(
        String resourceKey,
        String capability
) {
    public UiCapabilityReference {
        resourceKey = require(resourceKey, "resourceKey");
        capability = require(capability, "capability");
    }

    public static UiCapabilityReference of(String resourceKey, String capability) {
        return new UiCapabilityReference(resourceKey, capability);
    }

    private static String require(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
