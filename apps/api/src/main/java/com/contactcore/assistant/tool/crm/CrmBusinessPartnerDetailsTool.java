// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import com.contactcore.assistant.tool.AssistantTool;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.assistant.tool.AssistantToolExecutionContext;
import com.contactcore.assistant.tool.AssistantToolResult;
import org.springframework.stereotype.Component;

@Component
public class CrmBusinessPartnerDetailsTool extends AbstractCrmAssistantTool implements AssistantTool {
    private final CrmAssistantQueryService queries;

    public CrmBusinessPartnerDetailsTool(CrmAssistantQueryService queries) {
        super("crm.getBusinessPartnerDetails", "Fetch compact detail records for customers, leads, or suppliers matching a code, name, email, or primary contact.");
        this.queries = queries;
    }

    @Override
    public AssistantToolResult execute(AssistantToolCall call, AssistantToolExecutionContext context) {
        String query = call.stringArgument("query");
        String kindCode = call.stringArgument("kindCode");
        return result("Business-partner details for query='" + query + "'.",
                queries.businessPartnerDetails(query, kindCode, boundedLimit(context.maxResults(), 10)));
    }
}
