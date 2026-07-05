// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.Objects;

public final class IamActions {
    private IamActions() {}

    public static IamAction of(String service, String operation) {
        String normalizedService = require(service, "service").toLowerCase(java.util.Locale.ROOT);
        String normalizedOperation = require(operation, "operation");
        return IamAction.of(normalizedService + ":" + normalizedOperation);
    }

    private static String require(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
