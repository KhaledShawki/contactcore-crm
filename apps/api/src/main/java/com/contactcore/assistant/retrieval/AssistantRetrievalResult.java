// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.retrieval;

import com.contactcore.assistant.tool.AssistantToolResult;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record AssistantRetrievalResult(
        AssistantRetrievalType type,
        String userIntent,
        List<AssistantToolResult> toolResults
) {
    public AssistantRetrievalResult {
        userIntent = userIntent == null || userIntent.isBlank() ? type.name() : userIntent.trim();
        toolResults = toolResults == null ? List.of() : List.copyOf(toolResults);
    }

    public AssistantRetrievalResult(AssistantRetrievalType type, List<AssistantSearchResult> results) {
        this(type, type.name(), List.of(new AssistantToolResult(type.name(), type.name(), results)));
    }

    public List<AssistantSearchResult> results() {
        Map<String, AssistantSearchResult> recordsByReference = new LinkedHashMap<>();
        for (AssistantToolResult toolResult : toolResults) {
            for (AssistantSearchResult result : toolResult.records()) {
                AssistantRecordReference reference = result.reference();
                String key = reference.entityType() + ':' + reference.entityId() + ':' + reference.route();
                recordsByReference.putIfAbsent(key, result);
            }
        }
        return recordsByReference.values().stream().toList();
    }

    public List<AssistantRecordReference> references() {
        return results().stream()
                .map(AssistantSearchResult::reference)
                .toList();
    }
}
