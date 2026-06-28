// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import java.util.List;

public record UiFormRule(
        String type,
        List<String> fields,
        String message
) {}
