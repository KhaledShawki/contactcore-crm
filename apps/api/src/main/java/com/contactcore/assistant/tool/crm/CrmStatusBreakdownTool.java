// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import com.contactcore.assistant.tool.AssistantTool;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.assistant.tool.AssistantToolExecutionContext;
import com.contactcore.assistant.tool.AssistantToolResult;
import org.springframework.stereotype.Component;

@Component
public class CrmStatusBreakdownTool extends AbstractCrmAssistantTool implements AssistantTool {
    private final CrmAssistantQueryService queries;

    public CrmStatusBreakdownTool(CrmAssistantQueryService queries) {
        super("crm.getStatusBreakdown", "Summarize active CRM records by kind and status.");
        this.queries = queries;
    }

    @Override
    public AssistantToolResult execute(AssistantToolCall call, AssistantToolExecutionContext context) {
        return result("CRM status breakdown by kind.", queries.statusBreakdown());
    }
}
