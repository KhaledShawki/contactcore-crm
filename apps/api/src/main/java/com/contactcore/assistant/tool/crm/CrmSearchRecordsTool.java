// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import com.contactcore.assistant.tool.AssistantTool;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.assistant.tool.AssistantToolExecutionContext;
import com.contactcore.assistant.tool.AssistantToolResult;
import com.contactcore.assistant.tool.connector.ConnectorAssistantResultMapper;
import com.contactcore.connector.application.ConnectorBusinessPartnerQueryService;
import com.contactcore.connector.application.ConnectorSessionService;
import com.contactcore.connector.model.CrmBusinessPartnerSearchCriteria;
import com.contactcore.connector.model.CrmBusinessPartnerType;
import org.springframework.stereotype.Component;

@Component
public class CrmSearchRecordsTool extends AbstractCrmAssistantTool implements AssistantTool {
    private final CrmAssistantQueryService queries;
    private final ConnectorSessionService connectorSessions;
    private final ConnectorBusinessPartnerQueryService connectorQueries;

    public CrmSearchRecordsTool(CrmAssistantQueryService queries,
                                ConnectorSessionService connectorSessions,
                                ConnectorBusinessPartnerQueryService connectorQueries) {
        super("crm.searchRecords", "Search CRM records through the active CRM connector when one is selected, otherwise the local ContactCore CRM.");
        this.queries = queries;
        this.connectorSessions = connectorSessions;
        this.connectorQueries = connectorQueries;
    }

    @Override
    public AssistantToolResult execute(AssistantToolCall call, AssistantToolExecutionContext context) {
        String query = call.stringArgument("query");
        String kindCode = call.stringArgument("kindCode");
        String source = call.stringArgument("source");
        int limit = boundedLimit(context.maxResults(), 20);
        if (connectorSessions.activeSession(context.userId()).isPresent()) {
            var criteria = new CrmBusinessPartnerSearchCriteria(query, connectorType(kindCode), 0, limit, "code_asc");
            var connectorResult = connectorQueries.search(context.userId(), criteria);
            return result("Active CRM connector business-partner search for query='" + query + "'.",
                    ConnectorAssistantResultMapper.businessPartners(connectorResult.items()));
        }
        return result("Active local CRM record search for query='" + query + "'.",
                queries.searchRecords(query, kindCode, source, limit));
    }

    private CrmBusinessPartnerType connectorType(String kindCode) {
        try {
            return CrmBusinessPartnerType.optional(kindCode);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
