// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import com.contactcore.assistant.tool.AssistantTool;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.assistant.tool.AssistantToolExecutionContext;
import com.contactcore.assistant.tool.AssistantToolResult;
import org.springframework.stereotype.Component;

@Component
public class CrmRecentRecordsTool extends AbstractCrmAssistantTool implements AssistantTool {
    private final CrmAssistantQueryService queries;

    public CrmRecentRecordsTool(CrmAssistantQueryService queries) {
        super("crm.getRecentRecords", "List recently created active CRM records.");
        this.queries = queries;
    }

    @Override
    public AssistantToolResult execute(AssistantToolCall call, AssistantToolExecutionContext context) {
        return result("Recently created active CRM records.",
                queries.recentRecords(call.stringArgument("kindCode"), boundedLimit(context.maxResults(), 20)));
    }
}
