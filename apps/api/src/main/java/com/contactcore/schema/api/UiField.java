// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import java.util.List;

public record UiField(
        String key,
        String label,
        String labelKey,
        String valueKind,
        String type,
        boolean required,
        boolean listVisible,
        boolean formVisible,
        boolean readOnly,
        String defaultValue,
        List<String> options,
        UiValidation validation
) {}
