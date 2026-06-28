// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.assistant.application.planning.AssistantIntent;
import com.contactcore.assistant.application.planning.AssistantIntentClassifier;
import com.contactcore.assistant.application.planning.AssistantToolPlanValidator;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import org.junit.jupiter.api.Test;

class AssistantQueryPlannerTest {
    private final AssistantQueryPlanner planner = new AssistantQueryPlanner(
            properties(),
            new AssistantEntityExtractor(),
            new AssistantIntentClassifier(),
            new AssistantToolPlanValidator()
    );


    @Test
    void mapsGreetingToSafeNoToolPlan() {
        AssistantPlan plan = planner.plan("hi");

        assertThat(plan.intent()).isEqualTo(AssistantIntent.GREETING);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.ASSISTANT_HELP);
        assertThat(plan.toolCalls()).isEmpty();
    }

    @Test
    void mapsRandomTextToSafeNoToolUnclearPlan() {
        AssistantPlan plan = planner.plan("tandom things");

        assertThat(plan.intent()).isEqualTo(AssistantIntent.UNCLEAR_REQUEST);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.ASSISTANT_HELP);
        assertThat(plan.toolCalls()).isEmpty();
    }

    @Test
    void doesNotUseCrmSummaryAsUnknownFallback() {
        AssistantPlan plan = planner.plan("tell me about random things");

        assertThat(plan.intent()).isEqualTo(AssistantIntent.UNCLEAR_REQUEST);
        assertThat(plan.toolCalls()).isEmpty();
    }

    @Test
    void mapsIdentityQuestionToSafeNoToolPlan() {
        AssistantPlan plan = planner.plan("what are you?");

        assertThat(plan.intent()).isEqualTo(AssistantIntent.ASSISTANT_IDENTITY);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.ASSISTANT_HELP);
        assertThat(plan.toolCalls()).isEmpty();
    }

    @Test
    void mapsCapabilitiesQuestionToSafeNoToolPlan() {
        AssistantPlan plan = planner.plan("what can you help me with?");

        assertThat(plan.intent()).isEqualTo(AssistantIntent.ASSISTANT_CAPABILITIES);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.ASSISTANT_HELP);
        assertThat(plan.toolCalls()).isEmpty();
    }

    @Test
    void mapsFollowUpQuestionsToStaleLeads() {
        AssistantPlan plan = planner.plan("Which leads need follow-up?");

        assertThat(plan.intent()).isEqualTo(AssistantIntent.LEADS_NEED_FOLLOW_UP);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.STALE_LEADS);
        assertThat(plan.toolCalls()).singleElement().satisfies(call -> assertThat(call.toolName()).isEqualTo("crm.listStaleLeads"));
    }

    @Test
    void mapsContactCoverageQuestionsToLeadsWithoutContacts() {
        AssistantPlan plan = planner.plan("Show leads without contact persons.");

        assertThat(plan.intent()).isEqualTo(AssistantIntent.LEADS_MISSING_CONTACTS);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.LEADS_WITHOUT_CONTACTS);
    }

    @Test
    void mapsMarketingQuestionsToMarketingAnalytics() {
        assertThat(planner.plan("Show marketing source performance.").retrievalType())
                .isEqualTo(AssistantRetrievalType.MARKETING_SOURCE_ANALYTICS);
    }

    @Test
    void mapsSearchQuestionsToCrmSearchAndExtractsSearchTerm() {
        AssistantPlan plan = planner.plan("Find records related to Meyer.");

        assertThat(plan.intent()).isEqualTo(AssistantIntent.BUSINESS_PARTNER_SEARCH);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.CRM_SEARCH);
        assertThat(plan.toolCalls().getFirst().stringArgument("query")).isEqualTo("Meyer");
    }

    @Test
    void mapsCustomerExistenceQuestionOnlyToBusinessPartnerSearch() {
        AssistantPlan plan = planner.plan("Do we have customer with name Customer ekdwkskpdm?");

        assertThat(plan.intent()).isEqualTo(AssistantIntent.BUSINESS_PARTNER_EXISTENCE);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.CRM_SEARCH);
        assertThat(plan.searchTerm()).isEqualTo("Customer ekdwkskpdm");
        assertThat(plan.kindCode()).isEqualTo("CUSTOMER");
        assertThat(plan.toolCalls()).hasSize(1);
        assertThat(plan.toolCalls().getFirst().toolName()).isEqualTo("crm.searchRecords");
        assertThat(plan.toolCalls().getFirst().stringArgument("query")).isEqualTo("Customer ekdwkskpdm");
        assertThat(plan.toolCalls().getFirst().stringArgument("kindCode")).isEqualTo("CUSTOMER");
    }

    @Test
    void doesNotUseAggregateToolsForExistenceQuestions() {
        AssistantPlan plan = planner.plan("Is there a customer named Customer 00100000?");

        assertThat(plan.toolCalls()).extracting("toolName")
                .containsExactly("crm.searchRecords");
    }

    @Test
    void fallsBackToCrmSummaryForSummaryRequests() {
        AssistantPlan plan = planner.plan("Summarize the CRM.");

        assertThat(plan.intent()).isEqualTo(AssistantIntent.CRM_SUMMARY);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.MULTI_TOOL);
        assertThat(plan.toolCalls()).extracting("toolName")
                .containsExactly("crm.getCrmSummary", "crm.getStatusBreakdown", "crm.getContactCoverage");
    }


    @Test
    void routesCreatedRecentlyToRecentRecordsInsteadOfWriteGuard() {
        AssistantPlan plan = planner.plan("Which records were created recently?");

        assertThat(plan.intent()).isEqualTo(AssistantIntent.RECENT_RECORDS);
        assertThat(plan.toolCalls()).extracting("toolName").containsExactly("crm.getRecentRecords");
    }

    @Test
    void mapsWriteRequestsToSafeNoToolUnsupportedPlan() {
        AssistantPlan plan = planner.plan("Delete this lead");

        assertThat(plan.intent()).isEqualTo(AssistantIntent.UNSUPPORTED);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.ASSISTANT_HELP);
        assertThat(plan.toolCalls()).isEmpty();
    }

    private AssistantProperties properties() {
        return new AssistantProperties(true, "noop", "test", "", "", 5000, 12000, 20, 10);
    }
}
