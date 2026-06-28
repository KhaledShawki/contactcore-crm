// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.retrieval;

import java.util.LinkedHashMap;
import java.util.Map;

public record AssistantSearchResult(
        AssistantRecordReference reference,
        Map<String, String> fields
) {
    public static AssistantSearchResult of(AssistantRecordReference reference, Map<String, String> fields) {
        return new AssistantSearchResult(reference, new LinkedHashMap<>(fields));
    }
}
