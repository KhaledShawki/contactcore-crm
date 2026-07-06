// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.assistant.application.planning.AssistantIntent;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import com.contactcore.assistant.security.AssistantToolAuthorizationGuard;
import com.contactcore.iam.application.IamAccessDeniedException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class AssistantToolOrchestratorAuthorizationTest {
    private final AssistantTool tool = mock(AssistantTool.class);
    private final AssistantToolAuthorizationGuard authorization = mock(AssistantToolAuthorizationGuard.class);

    @Test
    void authorizesBeforeExecutingTool() {
        AssistantToolCall call = AssistantToolCall.of("crm.getCrmSummary");
        AssistantPlan plan = plan(call);
        AssistantToolOrchestrator orchestrator = orchestrator();
        when(tool.execute(org.mockito.ArgumentMatchers.eq(call), org.mockito.ArgumentMatchers.any()))
                .thenReturn(new AssistantToolResult("crm.getCrmSummary", "summary", List.of()));

        orchestrator.execute(42L, plan);

        InOrder inOrder = inOrder(authorization, tool);
        inOrder.verify(authorization).requireToolExecution(call, AssistantRetrievalType.CRM_SUMMARY);
        inOrder.verify(tool).execute(org.mockito.ArgumentMatchers.eq(call), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void skipsToolExecutionWhenAuthorizationFails() {
        AssistantToolCall call = AssistantToolCall.of("crm.getCrmSummary");
        AssistantPlan plan = plan(call);
        AssistantToolOrchestrator orchestrator = orchestrator();
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        org.mockito.Mockito.doThrow(denied).when(authorization).requireToolExecution(call, AssistantRetrievalType.CRM_SUMMARY);

        assertThatThrownBy(() -> orchestrator.execute(42L, plan))
                .isSameAs(denied);

        verify(tool, never()).execute(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    private AssistantToolOrchestrator orchestrator() {
        when(tool.name()).thenReturn("crm.getCrmSummary");
        return new AssistantToolOrchestrator(new AssistantToolRegistry(List.of(tool)), authorization);
    }

    private static AssistantPlan plan(AssistantToolCall call) {
        return new AssistantPlan(
                AssistantRetrievalType.CRM_SUMMARY,
                AssistantIntent.CRM_SUMMARY,
                "summary",
                "",
                "",
                "",
                20,
                List.of(call),
                "CRM summary"
        );
    }
}
