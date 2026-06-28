// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import com.contactcore.assistant.tool.AssistantTool;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.assistant.tool.AssistantToolExecutionContext;
import com.contactcore.assistant.tool.AssistantToolResult;
import org.springframework.stereotype.Component;

@Component
public class CrmContactCoverageTool extends AbstractCrmAssistantTool implements AssistantTool {
    private final CrmAssistantQueryService queries;

    public CrmContactCoverageTool(CrmAssistantQueryService queries) {
        super("crm.getContactCoverage", "Report contact-person coverage for customers, leads, and suppliers.");
        this.queries = queries;
    }

    @Override
    public AssistantToolResult execute(AssistantToolCall call, AssistantToolExecutionContext context) {
        return result("Contact-person coverage by CRM record kind.", queries.contactCoverage());
    }
}
