// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import com.contactcore.assistant.tool.AssistantTool;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.assistant.tool.AssistantToolExecutionContext;
import com.contactcore.assistant.tool.AssistantToolResult;
import org.springframework.stereotype.Component;

@Component
public class CrmSearchRecordsTool extends AbstractCrmAssistantTool implements AssistantTool {
    private final CrmAssistantQueryService queries;

    public CrmSearchRecordsTool(CrmAssistantQueryService queries) {
        super("crm.searchRecords", "Search active customers, leads, suppliers, contact methods, contact persons, and lead sources.");
        this.queries = queries;
    }

    @Override
    public AssistantToolResult execute(AssistantToolCall call, AssistantToolExecutionContext context) {
        String query = call.stringArgument("query");
        String kindCode = call.stringArgument("kindCode");
        String source = call.stringArgument("source");
        return result("Active CRM record search for query='" + query + "'.",
                queries.searchRecords(query, kindCode, source, boundedLimit(context.maxResults(), 20)));
    }
}
