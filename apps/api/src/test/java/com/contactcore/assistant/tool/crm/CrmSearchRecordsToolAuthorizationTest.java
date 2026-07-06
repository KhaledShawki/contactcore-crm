// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import com.contactcore.assistant.security.AssistantToolAuthorizationGuard;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.assistant.tool.AssistantToolExecutionContext;
import com.contactcore.connector.application.ConnectorBusinessPartnerQueryService;
import com.contactcore.connector.application.ConnectorSessionService;
import com.contactcore.connector.application.ConnectorSessionState;
import com.contactcore.connector.domain.CrmConnectorInstance;
import com.contactcore.connector.port.ConnectorAdapterSession;
import com.contactcore.connector.model.CrmBusinessPartnerSearchCriteria;
import com.contactcore.iam.application.IamAccessDeniedException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CrmSearchRecordsToolAuthorizationTest {
    private final CrmAssistantQueryService queries = mock(CrmAssistantQueryService.class);
    private final ConnectorSessionService connectorSessions = mock(ConnectorSessionService.class);
    private final ConnectorBusinessPartnerQueryService connectorQueries = mock(ConnectorBusinessPartnerQueryService.class);
    private final AssistantToolAuthorizationGuard authorization = mock(AssistantToolAuthorizationGuard.class);
    private final CrmSearchRecordsTool tool = new CrmSearchRecordsTool(queries, connectorSessions, connectorQueries, authorization);

    @Test
    void requiresConnectorToolAuthorizationBeforeUsingActiveConnectorSession() {
        AssistantToolCall call = AssistantToolCall.of("crm.searchRecords");
        AssistantToolExecutionContext context = new AssistantToolExecutionContext(42L, 20, AssistantRetrievalType.CRM_SEARCH);
        when(connectorSessions.activeSession(42L)).thenReturn(Optional.of(activeSession()));
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        org.mockito.Mockito.doThrow(denied).when(authorization).requireConnectorBusinessPartnerSearch(call, AssistantRetrievalType.CRM_SEARCH);

        assertThatThrownBy(() -> tool.execute(call, context))
                .isSameAs(denied);

        verify(connectorQueries, never()).search(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any(CrmBusinessPartnerSearchCriteria.class));
        verify(queries, never()).searchRecords(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyInt());
    }

    private static ConnectorSessionState activeSession() {
        return new ConnectorSessionState(42L, mock(CrmConnectorInstance.class), mock(ConnectorAdapterSession.class), Instant.now());
    }
}
