// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import com.contactcore.assistant.retrieval.AssistantSearchResult;
import com.contactcore.assistant.tool.AssistantToolResult;
import java.util.List;

abstract class AbstractCrmAssistantTool {
    private final String name;
    private final String description;

    protected AbstractCrmAssistantTool(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public final String name() {
        return name;
    }

    public final String description() {
        return description;
    }

    protected AssistantToolResult result(String summary, List<AssistantSearchResult> records) {
        return new AssistantToolResult(name, summary, records);
    }

    protected int boundedLimit(int requestedLimit, int contextLimit) {
        return Math.max(1, Math.min(requestedLimit, contextLimit));
    }
}
