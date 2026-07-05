// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record IamConditionBlock(Map<IamConditionOperator, Map<String, List<String>>> clauses) {
    public static final IamConditionBlock EMPTY = new IamConditionBlock(Map.of());

    public IamConditionBlock {
        clauses = copy(clauses);
    }

    public static IamConditionBlock empty() {
        return EMPTY;
    }

    public boolean isEmpty() {
        return clauses.isEmpty();
    }

    private static Map<IamConditionOperator, Map<String, List<String>>> copy(Map<IamConditionOperator, Map<String, List<String>>> value) {
        if (value == null || value.isEmpty()) {
            return Map.of();
        }
        Map<IamConditionOperator, Map<String, List<String>>> copied = new LinkedHashMap<>();
        value.forEach((operator, conditions) -> {
            IamConditionOperator safeOperator = Objects.requireNonNull(operator, "condition operator must not be null");
            Map<String, List<String>> copiedConditions = new LinkedHashMap<>();
            Objects.requireNonNull(conditions, "conditions must not be null").forEach((key, values) -> {
                String safeKey = Objects.requireNonNull(key, "condition key must not be null").trim();
                if (safeKey.isBlank()) {
                    throw new IllegalArgumentException("condition key must not be blank");
                }
                List<String> safeValues = Objects.requireNonNull(values, "condition values must not be null").stream()
                        .map(item -> Objects.requireNonNull(item, "condition value must not be null"))
                        .toList();
                if (safeValues.isEmpty()) {
                    throw new IllegalArgumentException("condition values must not be empty");
                }
                copiedConditions.put(safeKey, List.copyOf(safeValues));
            });
            copied.put(safeOperator, Map.copyOf(copiedConditions));
        });
        return Map.copyOf(copied);
    }
}
