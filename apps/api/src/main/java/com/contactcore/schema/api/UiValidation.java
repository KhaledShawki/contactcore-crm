// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

public record UiValidation(
        Integer minLength,
        Integer maxLength,
        String pattern,
        String patternMessage,
        String inputType,
        Integer minNumber,
        Integer maxNumber,
        String helpText
) {
    public static UiValidation none() {
        return new UiValidation(null, null, null, null, "text", null, null, null);
    }
}
