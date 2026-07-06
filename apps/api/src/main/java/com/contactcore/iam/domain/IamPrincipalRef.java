// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.Objects;

public record IamPrincipalRef(IamPrincipalType type, String id) {
    public IamPrincipalRef {
        type = Objects.requireNonNull(type, "type must not be null");
        id = Objects.requireNonNull(id, "id must not be null").trim();
        if (id.isBlank()) {
            throw new IllegalArgumentException("principal id must not be blank");
        }
    }

    public static IamPrincipalRef user(Long userId) {
        return new IamPrincipalRef(IamPrincipalType.USER, String.valueOf(Objects.requireNonNull(userId, "userId must not be null")));
    }

    public static IamPrincipalRef system(String id) {
        return new IamPrincipalRef(IamPrincipalType.SYSTEM, id);
    }
}
