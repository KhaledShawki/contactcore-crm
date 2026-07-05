// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.List;
import java.util.Objects;

public record IamPolicyDocument(String version, List<IamPolicyStatement> statements) {
    public static final String CURRENT_VERSION = "2026-07-05";

    public IamPolicyDocument {
        version = version == null || version.isBlank() ? CURRENT_VERSION : version.trim();
        statements = statements == null ? List.of() : statements.stream()
                .map(statement -> Objects.requireNonNull(statement, "statement must not be null"))
                .toList();
    }

    public static IamPolicyDocument empty() {
        return new IamPolicyDocument(CURRENT_VERSION, List.of());
    }
}
