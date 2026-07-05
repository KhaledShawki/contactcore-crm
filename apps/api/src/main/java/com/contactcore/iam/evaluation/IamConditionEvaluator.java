// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

import com.contactcore.iam.domain.IamConditionBlock;
import com.contactcore.iam.domain.IamConditionOperator;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class IamConditionEvaluator {
    private final IamMatcher matcher;

    public IamConditionEvaluator(IamMatcher matcher) {
        this.matcher = matcher;
    }

    public boolean matches(IamConditionBlock conditionBlock, IamRequestContext context) {
        if (conditionBlock == null || conditionBlock.isEmpty()) {
            return true;
        }
        for (Map.Entry<IamConditionOperator, Map<String, List<String>>> operatorEntry : conditionBlock.clauses().entrySet()) {
            for (Map.Entry<String, List<String>> condition : operatorEntry.getValue().entrySet()) {
                if (!matches(operatorEntry.getKey(), condition.getKey(), condition.getValue(), context)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean matches(IamConditionOperator operator, String key, List<String> expectedValues, IamRequestContext context) {
        return switch (operator) {
            case STRING_EQUALS -> stringEquals(key, expectedValues, context);
            case STRING_NOT_EQUALS -> stringNotEquals(key, expectedValues, context);
            case STRING_LIKE -> stringLike(key, expectedValues, context);
            case BOOL -> boolEquals(key, expectedValues, context);
            case DATE_GREATER_THAN -> dateGreaterThan(key, expectedValues, context);
            case DATE_LESS_THAN -> dateLessThan(key, expectedValues, context);
            case FOR_ANY_VALUE_STRING_EQUALS -> stringEquals(key, expectedValues, context);
            case FOR_ALL_VALUES_STRING_EQUALS -> allStringValuesEqual(key, expectedValues, context);
        };
    }

    private boolean stringEquals(String key, List<String> expectedValues, IamRequestContext context) {
        List<String> actualValues = context.stringValues(key);
        return !actualValues.isEmpty() && actualValues.stream().anyMatch(expectedValues::contains);
    }

    private boolean stringNotEquals(String key, List<String> expectedValues, IamRequestContext context) {
        List<String> actualValues = context.stringValues(key);
        return !actualValues.isEmpty() && actualValues.stream().noneMatch(expectedValues::contains);
    }

    private boolean allStringValuesEqual(String key, List<String> expectedValues, IamRequestContext context) {
        List<String> actualValues = context.stringValues(key);
        return !actualValues.isEmpty() && actualValues.stream().allMatch(expectedValues::contains);
    }

    private boolean stringLike(String key, List<String> expectedValues, IamRequestContext context) {
        List<String> actualValues = context.stringValues(key);
        return !actualValues.isEmpty()
                && actualValues.stream().anyMatch(actual -> expectedValues.stream().anyMatch(expected -> matcher.wildcardMatch(expected, actual)));
    }

    private boolean boolEquals(String key, List<String> expectedValues, IamRequestContext context) {
        Boolean actual = context.boolValue(key);
        return actual != null && expectedValues.stream()
                .map(this::parseBoolean)
                .flatMap(java.util.Optional::stream)
                .anyMatch(actual::equals);
    }

    private boolean dateGreaterThan(String key, List<String> expectedValues, IamRequestContext context) {
        Instant actual = context.instantValue(key);
        return actual != null && expectedValues.stream()
                .map(this::parseInstant)
                .flatMap(java.util.Optional::stream)
                .anyMatch(actual::isAfter);
    }

    private boolean dateLessThan(String key, List<String> expectedValues, IamRequestContext context) {
        Instant actual = context.instantValue(key);
        return actual != null && expectedValues.stream()
                .map(this::parseInstant)
                .flatMap(java.util.Optional::stream)
                .anyMatch(actual::isBefore);
    }

    private java.util.Optional<Boolean> parseBoolean(String value) {
        if (value == null) {
            return java.util.Optional.empty();
        }
        String normalized = value.trim().toLowerCase(java.util.Locale.ROOT);
        return switch (normalized) {
            case "true" -> java.util.Optional.of(true);
            case "false" -> java.util.Optional.of(false);
            default -> java.util.Optional.empty();
        };
    }

    private java.util.Optional<Instant> parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return java.util.Optional.empty();
        }
        try {
            return java.util.Optional.of(Instant.parse(value));
        } catch (java.time.format.DateTimeParseException ignored) {
            return java.util.Optional.empty();
        }
    }
}
