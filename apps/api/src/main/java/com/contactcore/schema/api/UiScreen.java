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
        List<UiScreenFilter> filters,
        List<UiField> fields,
        List<UiFormRule> validationRules,
        UiResourceCapabilities capabilities
) {
    public UiScreen {
        filters = filters == null ? List.of() : List.copyOf(filters);
        fields = fields == null ? List.of() : List.copyOf(fields);
        validationRules = validationRules == null ? List.of() : List.copyOf(validationRules);
    }
}
