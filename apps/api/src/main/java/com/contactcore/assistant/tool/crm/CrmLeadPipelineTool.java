// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import com.contactcore.assistant.tool.AssistantTool;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.assistant.tool.AssistantToolExecutionContext;
import com.contactcore.assistant.tool.AssistantToolResult;
import org.springframework.stereotype.Component;

@Component
public class CrmLeadPipelineTool extends AbstractCrmAssistantTool implements AssistantTool {
    private final CrmAssistantQueryService queries;

    public CrmLeadPipelineTool(CrmAssistantQueryService queries) {
        super("crm.getLeadPipeline", "Summarize leads by status, stale count, and missing-contact count.");
        this.queries = queries;
    }

    @Override
    public AssistantToolResult execute(AssistantToolCall call, AssistantToolExecutionContext context) {
        return result("Lead pipeline status summary.", queries.leadPipeline());
    }
}
