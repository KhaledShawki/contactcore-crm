// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.application;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamResource;
import java.util.Objects;

public record UiCapabilityDefinition(
        String resourceKey,
        String capability,
        IamAction action,
        IamResource resource
) {
    public UiCapabilityDefinition {
        resourceKey = require(resourceKey, "resourceKey");
        capability = require(capability, "capability");
        action = Objects.requireNonNull(action, "action must not be null");
        resource = Objects.requireNonNull(resource, "resource must not be null");
    }

    private static String require(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
