// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import com.contactcore.assistant.tool.AssistantTool;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.assistant.tool.AssistantToolExecutionContext;
import com.contactcore.assistant.tool.AssistantToolResult;
import org.springframework.stereotype.Component;

@Component
public class CrmLeadsWithoutContactsTool extends AbstractCrmAssistantTool implements AssistantTool {
    private final CrmAssistantQueryService queries;

    public CrmLeadsWithoutContactsTool(CrmAssistantQueryService queries) {
        super("crm.listLeadsWithoutContactPersons", "List active leads that have no active contact person.");
        this.queries = queries;
    }

    @Override
    public AssistantToolResult execute(AssistantToolCall call, AssistantToolExecutionContext context) {
        return result("Active leads with no active contact person.",
                queries.leadsWithoutContactPersons(call.stringArgument("source"), boundedLimit(context.maxResults(), 20)));
    }
}
