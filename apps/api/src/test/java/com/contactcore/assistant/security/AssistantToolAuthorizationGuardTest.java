// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.connector.security.ConnectorIamActions;
import com.contactcore.crm.security.CrmIamActions;
import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import com.contactcore.iam.domain.IamResource;
import com.contactcore.iam.evaluation.IamRequestContext;
import com.contactcore.shared.api.InvalidRequestException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AssistantToolAuthorizationGuardTest {
    private final CurrentIamAuthorizationService authorization = mock(CurrentIamAuthorizationService.class);
    private final ContactCoreTenantContext tenantContext = mock(ContactCoreTenantContext.class);
    private final AssistantToolAuthorizationGuard guard = new AssistantToolAuthorizationGuard(
            authorization,
            tenantContext,
            new AssistantToolAuthorizationRegistry()
    );

    @BeforeEach
    void setUp() {
        when(tenantContext.currentTenantId()).thenReturn("tenant-1");
    }

    @Test
    void requiresAssistantAndCrmListPermissionForCrmSearchTool() {
        AssistantToolCall call = AssistantToolCall.of("crm.searchRecords", Map.of("query", "Acme"));

        guard.requireToolExecution(call, AssistantRetrievalType.CRM_SEARCH);

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(AssistantIamActions.USE_CRM_TOOLS),
                eq(IamResource.of("contactcore:tenant-1:assistant:tool/crm/crm.searchRecords")),
                context.capture()
        );
        verify(authorization).requireAllowed(
                eq(CrmIamActions.LIST_BUSINESS_PARTNERS),
                eq(IamResource.of("contactcore:tenant-1:crm:business-partner/*")),
                eq(context.getValue())
        );
        assertThat(context.getValue().get("toolName")).isEqualTo("crm.searchRecords");
        assertThat(context.getValue().get("toolCategory")).isEqualTo("crm");
        assertThat(context.getValue().get("query")).isEqualTo("Acme");
        assertThat(context.getValue().get("retrievalType")).isEqualTo("CRM_SEARCH");
    }

    @Test
    void requiresAssistantAndCrmReadPermissionForBusinessPartnerDetailsTool() {
        AssistantToolCall call = AssistantToolCall.of("crm.getBusinessPartnerDetails", Map.of("query", "Acme"));

        guard.requireToolExecution(call, AssistantRetrievalType.BUSINESS_PARTNER_DETAILS);

        verify(authorization).requireAllowed(
                eq(AssistantIamActions.USE_CRM_TOOLS),
                eq(IamResource.of("contactcore:tenant-1:assistant:tool/crm/crm.getBusinessPartnerDetails")),
                org.mockito.ArgumentMatchers.any(IamRequestContext.class)
        );
        verify(authorization).requireAllowed(
                eq(CrmIamActions.READ_BUSINESS_PARTNER),
                eq(IamResource.of("contactcore:tenant-1:crm:business-partner/*")),
                org.mockito.ArgumentMatchers.any(IamRequestContext.class)
        );
    }

    @Test
    void requiresConnectorToolAndConnectorBusinessPartnerPermissionForConnectorBackedSearch() {
        AssistantToolCall call = AssistantToolCall.of("crm.searchRecords", Map.of("query", "Acme"));

        guard.requireConnectorBusinessPartnerSearch(call, AssistantRetrievalType.CRM_SEARCH);

        verify(authorization).requireAllowed(
                eq(AssistantIamActions.USE_CONNECTOR_TOOLS),
                eq(IamResource.of("contactcore:tenant-1:assistant:tool/connector/crm.searchRecords")),
                org.mockito.ArgumentMatchers.any(IamRequestContext.class)
        );
        verify(authorization).requireAllowed(
                eq(ConnectorIamActions.READ_BUSINESS_PARTNERS),
                eq(IamResource.of("contactcore:tenant-1:connector:business-partner/*")),
                org.mockito.ArgumentMatchers.any(IamRequestContext.class)
        );
    }

    @Test
    void rejectsToolsWithoutAuthorizationRule() {
        assertThatThrownBy(() -> guard.requireToolExecution(AssistantToolCall.of("crm.unclassified"), AssistantRetrievalType.CRM_SEARCH))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("no IAM authorization rule");
    }
}
