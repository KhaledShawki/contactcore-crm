// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool;

import com.contactcore.assistant.retrieval.AssistantRecordReference;
import com.contactcore.assistant.retrieval.AssistantSearchResult;
import java.util.List;

public record AssistantToolResult(
        String toolName,
        String summary,
        List<AssistantSearchResult> records
) {
    public AssistantToolResult {
        records = records == null ? List.of() : List.copyOf(records);
        summary = summary == null ? "" : summary.trim();
    }

    public List<AssistantRecordReference> references() {
        return records.stream().map(AssistantSearchResult::reference).toList();
    }
}
