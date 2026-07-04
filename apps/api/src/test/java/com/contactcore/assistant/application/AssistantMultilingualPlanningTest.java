// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.assistant.application.nlu.AssistantMessageNormalizer;
import com.contactcore.assistant.application.planning.AssistantIntent;
import com.contactcore.assistant.application.planning.AssistantIntentClassifier;
import com.contactcore.assistant.application.planning.AssistantToolPlanValidator;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import org.junit.jupiter.api.Test;

class AssistantMultilingualPlanningTest {
    private final AssistantQueryPlanner planner = new AssistantQueryPlanner(
            new AssistantProperties(true, "noop", "test", "", "", 5000, 12000, 20, 10),
            new AssistantEntityExtractor(),
            new AssistantIntentClassifier(),
            new AssistantToolPlanValidator(),
            new AssistantMessageNormalizer()
    );

    @Test
    void englishGermanAndArabicCustomerRequestsProduceSameCanonicalToolPlan() {
        AssistantPlan english = planner.plan("Show me customer Meyer");
        AssistantPlan german = planner.plan("Zeige mir den Kunden Meyer");
        AssistantPlan arabic = planner.plan("اعرض العميل Meyer");

        assertCanonicalBusinessPartnerSearch(english, "CUSTOMER", "Meyer");
        assertCanonicalBusinessPartnerSearch(german, "CUSTOMER", "Meyer");
        assertCanonicalBusinessPartnerSearch(arabic, "CUSTOMER", "Meyer");
    }

    @Test
    void germanSupplierRequestUsesCanonicalSupplierEnum() {
        AssistantPlan plan = planner.plan("Suche Lieferant Acme");

        assertCanonicalBusinessPartnerSearch(plan, "SUPPLIER", "Acme");
    }

    @Test
    void arabicLeadPhraseUsesSpecificLeadConceptBeforeGenericCustomerConcept() {
        AssistantPlan plan = planner.plan("اعرض العميل المحتمل Meyer");

        assertCanonicalBusinessPartnerSearch(plan, "LEAD", "Meyer");
    }


    @Test
    void arabicLeadsWithoutContactPersonsQuestionProducesCanonicalToolPlan() {
        AssistantPlan plan = planner.plan("أي العملاء المحتملين لا يملكون جهات اتصال؟");

        assertThat(plan.intent()).isEqualTo(AssistantIntent.LEADS_MISSING_CONTACTS);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.LEADS_WITHOUT_CONTACTS);
        assertThat(plan.kindCode()).isBlank();
        assertThat(plan.searchTerm()).isBlank();
        assertThat(plan.toolCalls()).singleElement().satisfies(call -> {
            assertThat(call.toolName()).isEqualTo("crm.listLeadsWithoutContactPersons");
            assertThat(call.arguments()).doesNotContainKey("kindCode");
        });
    }

    @Test
    void englishGermanAndArabicMissingContactQuestionsProduceSameIntent() {
        AssistantPlan english = planner.plan("Which leads do not have contact persons?");
        AssistantPlan german = planner.plan("Welche Leads haben keine Kontaktpersonen?");
        AssistantPlan arabic = planner.plan("أي العملاء المحتملين لا يملكون جهات اتصال؟");

        assertLeadsWithoutContactPersonsPlan(english);
        assertLeadsWithoutContactPersonsPlan(german);
        assertLeadsWithoutContactPersonsPlan(arabic);
    }

    @Test
    void conflictingBusinessPartnerTypesDoNotExecuteTools() {
        AssistantPlan english = planner.plan("show customer supplier Meyer");
        AssistantPlan german = planner.plan("zeige Kunde Lieferant Meyer");
        AssistantPlan arabic = planner.plan("اعرض العميل المورد Meyer");

        assertAmbiguousBusinessPartnerTypePlan(english);
        assertAmbiguousBusinessPartnerTypePlan(german);
        assertAmbiguousBusinessPartnerTypePlan(arabic);
    }

    @Test
    void mixedLanguageRequestsStillProduceCanonicalToolPlans() {
        AssistantPlan germanCommand = planner.plan("Zeige customer Meyer");
        AssistantPlan englishCommand = planner.plan("Show Kunde Meyer");

        assertCanonicalBusinessPartnerSearch(germanCommand, "CUSTOMER", "Meyer");
        assertCanonicalBusinessPartnerSearch(englishCommand, "CUSTOMER", "Meyer");
    }


    private void assertLeadsWithoutContactPersonsPlan(AssistantPlan plan) {
        assertThat(plan.intent()).isEqualTo(AssistantIntent.LEADS_MISSING_CONTACTS);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.LEADS_WITHOUT_CONTACTS);
        assertThat(plan.toolCalls()).singleElement().satisfies(call ->
                assertThat(call.toolName()).isEqualTo("crm.listLeadsWithoutContactPersons")
        );
    }

    private void assertCanonicalBusinessPartnerSearch(AssistantPlan plan, String kindCode, String query) {
        assertThat(plan.intent()).isEqualTo(AssistantIntent.BUSINESS_PARTNER_SEARCH);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.CRM_SEARCH);
        assertThat(plan.kindCode()).isEqualTo(kindCode);
        assertThat(plan.searchTerm()).isEqualTo(query);
        assertThat(plan.toolCalls()).singleElement().satisfies(call -> {
            assertThat(call.toolName()).isEqualTo("crm.searchRecords");
            assertThat(call.stringArgument("kindCode")).isEqualTo(kindCode);
            assertThat(call.stringArgument("query")).isEqualTo(query);
        });
    }

    private void assertAmbiguousBusinessPartnerTypePlan(AssistantPlan plan) {
        assertThat(plan.intent()).isEqualTo(AssistantIntent.UNCLEAR_REQUEST);
        assertThat(plan.retrievalType()).isEqualTo(AssistantRetrievalType.ASSISTANT_HELP);
        assertThat(plan.toolCalls()).isEmpty();
        assertThat(plan.userIntent()).startsWith("UNCLEAR_REQUEST conflicting business partner types");
    }
}
