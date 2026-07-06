// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import java.util.List;

public record UiScreen(
        String key,
        String title,
        String entityKind,
        String listEndpoint,
        String detailEndpoint,
        String createEndpoint,
        String updateEndpoint,
        String archiveEndpoint,
        String documentEndpoint,
        UiScreenLayout layout,
        List<UiField> fields,
        List<UiFormRule> validationRules,
        UiResourceCapabilities capabilities
) {}
