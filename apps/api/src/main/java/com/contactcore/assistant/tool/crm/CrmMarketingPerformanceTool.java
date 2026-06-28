// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import com.contactcore.assistant.tool.AssistantTool;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.assistant.tool.AssistantToolExecutionContext;
import com.contactcore.assistant.tool.AssistantToolResult;
import org.springframework.stereotype.Component;

@Component
public class CrmMarketingPerformanceTool extends AbstractCrmAssistantTool implements AssistantTool {
    private final CrmAssistantQueryService queries;

    public CrmMarketingPerformanceTool(CrmAssistantQueryService queries) {
        super("crm.getMarketingSourcePerformance", "Summarize lead counts and qualification rates by marketing source.");
        this.queries = queries;
    }

    @Override
    public AssistantToolResult execute(AssistantToolCall call, AssistantToolExecutionContext context) {
        return result("Marketing-source lead performance.", queries.marketingSourcePerformance(boundedLimit(context.maxResults(), 30)));
    }
}
