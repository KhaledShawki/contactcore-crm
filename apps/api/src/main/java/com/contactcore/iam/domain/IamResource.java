// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.Objects;

public record IamResource(String value) {
    public static final IamResource ALL = new IamResource("*");

    public IamResource {
        value = Objects.requireNonNull(value, "value must not be null").trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException("IAM resource must not be blank");
        }
    }

    public static IamResource of(String value) {
        return new IamResource(value);
    }

    public static IamResource contactCore(String tenantId, String service, String resourceType, String resourceId) {
        return new IamResource("contactcore:%s:%s:%s/%s".formatted(
                required(tenantId, "tenantId"),
                required(service, "service"),
                required(resourceType, "resourceType"),
                required(resourceId, "resourceId")
        ));
    }

    public String tenantId() {
        String[] parts = parts();
        return parts.length >= 2 ? parts[1] : "*";
    }

    public String service() {
        String[] parts = parts();
        return parts.length >= 3 ? parts[2] : "*";
    }

    private String[] parts() {
        if ("*".equals(value)) {
            return new String[]{"*"};
        }
        return value.split(":", 4);
    }

    private static String required(String value, String name) {
        String normalized = Objects.requireNonNull(value, name + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return normalized;
    }
}
