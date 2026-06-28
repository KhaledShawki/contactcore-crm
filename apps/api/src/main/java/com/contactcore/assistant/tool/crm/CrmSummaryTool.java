// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import com.contactcore.assistant.tool.AssistantTool;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.assistant.tool.AssistantToolExecutionContext;
import com.contactcore.assistant.tool.AssistantToolResult;
import org.springframework.stereotype.Component;

@Component
public class CrmSummaryTool extends AbstractCrmAssistantTool implements AssistantTool {
    private final CrmAssistantQueryService queries;

    public CrmSummaryTool(CrmAssistantQueryService queries) {
        super("crm.getCrmSummary", "Summarize active CRM record counts by customer, lead, and supplier kind.");
        this.queries = queries;
    }

    @Override
    public AssistantToolResult execute(AssistantToolCall call, AssistantToolExecutionContext context) {
        return result("Active CRM summary by record kind.", queries.crmSummary());
    }
}
