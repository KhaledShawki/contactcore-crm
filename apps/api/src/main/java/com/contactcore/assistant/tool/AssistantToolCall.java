// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool;

import java.util.LinkedHashMap;
import java.util.Map;

public record AssistantToolCall(
        String toolName,
        Map<String, Object> arguments
) {
    public AssistantToolCall {
        if (toolName == null || toolName.isBlank()) {
            throw new IllegalArgumentException("Tool name must not be blank.");
        }
        arguments = arguments == null ? Map.of() : Map.copyOf(arguments);
    }

    public static AssistantToolCall of(String toolName) {
        return new AssistantToolCall(toolName, Map.of());
    }

    public static AssistantToolCall of(String toolName, Map<String, Object> arguments) {
        return new AssistantToolCall(toolName, new LinkedHashMap<>(arguments));
    }

    public String stringArgument(String name) {
        Object value = arguments.get(name);
        return value == null ? "" : String.valueOf(value).trim();
    }

    public int intArgument(String name, int defaultValue) {
        Object value = arguments.get(name);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
