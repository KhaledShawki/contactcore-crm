// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public record IamAction(String value) {
    private static final Pattern ACTION_PATTERN = Pattern.compile("(\\*|(\\*|[a-z][a-z0-9-]*):(\\*|[A-Za-z][A-Za-z0-9*]*))");

    public IamAction {
        value = normalize(value);
        if (!ACTION_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("IAM action must use service:Operation format or *");
        }
    }

    public static IamAction of(String value) {
        return new IamAction(value);
    }

    public String service() {
        if ("*".equals(value)) {
            return "*";
        }
        return value.substring(0, value.indexOf(':'));
    }

    public String operation() {
        if ("*".equals(value)) {
            return "*";
        }
        return value.substring(value.indexOf(':') + 1);
    }

    private static String normalize(String value) {
        String normalized = Objects.requireNonNull(value, "value must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("IAM action must not be blank");
        }
        if ("*".equals(normalized)) {
            return normalized;
        }
        int separator = normalized.indexOf(':');
        if (separator < 0) {
            return normalized;
        }
        String service = normalized.substring(0, separator).toLowerCase(Locale.ROOT);
        String operation = normalized.substring(separator + 1);
        return service + ":" + operation;
    }
}
