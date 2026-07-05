// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.Objects;

public record IamActionDescriptor(
        IamAction action,
        String description,
        IamActionRiskLevel riskLevel
) {
    public IamActionDescriptor {
        action = Objects.requireNonNull(action, "action must not be null");
        description = require(description, "description");
        riskLevel = Objects.requireNonNull(riskLevel, "riskLevel must not be null");
        if ("*".equals(action.value())) {
            throw new IllegalArgumentException("IAM action descriptors must reference concrete actions");
        }
    }

    public String service() {
        return action.service();
    }

    public String operation() {
        return action.operation();
    }

    public static IamActionDescriptor read(IamAction action, String description) {
        return new IamActionDescriptor(action, description, IamActionRiskLevel.READ);
    }

    public static IamActionDescriptor write(IamAction action, String description) {
        return new IamActionDescriptor(action, description, IamActionRiskLevel.WRITE);
    }

    public static IamActionDescriptor admin(IamAction action, String description) {
        return new IamActionDescriptor(action, description, IamActionRiskLevel.ADMIN);
    }

    public static IamActionDescriptor system(IamAction action, String description) {
        return new IamActionDescriptor(action, description, IamActionRiskLevel.SYSTEM);
    }

    private static String require(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
