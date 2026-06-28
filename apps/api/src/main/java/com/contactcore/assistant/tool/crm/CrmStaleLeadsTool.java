// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import com.contactcore.assistant.tool.AssistantTool;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.assistant.tool.AssistantToolExecutionContext;
import com.contactcore.assistant.tool.AssistantToolResult;
import org.springframework.stereotype.Component;

@Component
public class CrmStaleLeadsTool extends AbstractCrmAssistantTool implements AssistantTool {
    private final CrmAssistantQueryService queries;

    public CrmStaleLeadsTool(CrmAssistantQueryService queries) {
        super("crm.listStaleLeads", "List non-qualified active leads that have not been updated within the requested stale-day threshold.");
        this.queries = queries;
    }

    @Override
    public AssistantToolResult execute(AssistantToolCall call, AssistantToolExecutionContext context) {
        int staleDays = Math.clamp(call.intArgument("staleDays", 14), 1, 365);
        String source = call.stringArgument("source");
        return result("Non-qualified leads older than " + staleDays + " days.",
                queries.staleLeads(staleDays, source, boundedLimit(context.maxResults(), 20)));
    }
}
