// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.assistant.application.answer.DeterministicCrmAnswerGenerator;
import com.contactcore.assistant.application.answer.AssistantStaticResponseCatalog;
import com.contactcore.assistant.application.planning.AssistantIntent;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRecordReference;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import com.contactcore.assistant.retrieval.AssistantSearchResult;
import com.contactcore.assistant.tool.AssistantToolCall;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DeterministicCrmAnswerGeneratorTest {
    private final DeterministicCrmAnswerGenerator generator = new DeterministicCrmAnswerGenerator(new AssistantStaticResponseCatalog());


    @Test
    void answersGreetingWithoutInternalSchemaOrReferences() {
        AssistantPlan plan = noToolPlan(AssistantIntent.GREETING, "hi");
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(AssistantRetrievalType.ASSISTANT_HELP, List.of());

        String answer = generator.generate(plan, retrieval, "hi").answer();

        assertThat(answer).contains("Hi", "CRM records");
        assertThat(answer).doesNotContain("CRM_SUMMARY", "STATUS_BREAKDOWN", "CONTACT_COVERAGE");
    }

    @Test
    void answersUnclearRequestWithoutCrmEvidenceLabels() {
        AssistantPlan plan = noToolPlan(AssistantIntent.UNCLEAR_REQUEST, "tandom things");
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(AssistantRetrievalType.ASSISTANT_HELP, List.of());

        String answer = generator.generate(plan, retrieval, "tandom things").answer();

        assertThat(answer).contains("could not understand", "customers", "leads", "suppliers");
        assertThat(answer).doesNotContain("CRM_SUMMARY", "STATUS_BREAKDOWN", "CONTACT_COVERAGE");
    }

    @Test
    void answersIdentityQuestionWithoutInternalSchemaOrReferences() {
        AssistantPlan plan = noToolPlan(AssistantIntent.ASSISTANT_IDENTITY, "what are you?");
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(AssistantRetrievalType.ASSISTANT_HELP, List.of());

        String answer = generator.generate(plan, retrieval, "what are you?").answer();

        assertThat(answer).contains("ContactCore Assistant", "read-only CRM assistant");
        assertThat(answer).doesNotContain("CRM_RECORD", "CRM_SUMMARY", "STATUS_BREAKDOWN", "CONTACT_COVERAGE");
    }

    @Test
    void answersCapabilitiesQuestionWithoutInternalSchemaOrReferences() {
        AssistantPlan plan = noToolPlan(AssistantIntent.ASSISTANT_CAPABILITIES, "what can you help me with?");
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(AssistantRetrievalType.ASSISTANT_HELP, List.of());

        String answer = generator.generate(plan, retrieval, "what can you help me with?").answer();

        assertThat(answer).contains("Find customers", "List leads", "read-only CRM questions");
        assertThat(answer).contains("I cannot create, update, delete, archive, or send records.");
        assertThat(answer).doesNotContain("CRM_RECORD", "CRM_SUMMARY", "STATUS_BREAKDOWN", "CONTACT_COVERAGE");
    }

    @Test
    void answersUnsupportedWriteRequestAsDeterministicRefusal() {
        AssistantPlan plan = noToolPlan(AssistantIntent.UNSUPPORTED, "Create a new customer called Test GmbH.");
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(AssistantRetrievalType.ASSISTANT_HELP, List.of());

        String answer = generator.generate(plan, retrieval, "Create a new customer called Test GmbH.").answer();

        assertThat(answer).contains("read-only mode", "cannot create, update, delete, archive");
    }

    @Test
    void answersNoForMissingCustomerExistenceQuestion() {
        AssistantPlan plan = existencePlan("Customer ekdwkskpdm", "CUSTOMER");
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(AssistantRetrievalType.CRM_SEARCH, List.of());

        String answer = generator.generate(plan, retrieval, "Do we have customer with name Customer ekdwkskpdm?").answer();

        assertThat(answer).contains("No.", "active customer", "Customer ekdwkskpdm");
        assertThat(answer).doesNotContain("CRM SUMMARY", "STATUS BREAKDOWN", "CONTACT COVERAGE");
    }

    @Test
    void answersYesForFoundCustomerExistenceQuestion() {
        AssistantPlan plan = existencePlan("Customer 00100000", "CUSTOMER");
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(
                AssistantRetrievalType.CRM_SEARCH,
                List.of(AssistantSearchResult.of(
                        new AssistantRecordReference("BUSINESS_PARTNER", 10L, "CUS-10 - Customer 00100000", "/customers/10"),
                        fields("Kind", "CUSTOMER", "Status", "Active")
                ))
        );

        String answer = generator.generate(plan, retrieval, "Do we have customer with name Customer 00100000?").answer();

        assertThat(answer).contains("Yes.", "1 active customer", "Customer 00100000", "CUS-10");
    }


    private AssistantPlan noToolPlan(AssistantIntent intent, String query) {
        return new AssistantPlan(
                AssistantRetrievalType.ASSISTANT_HELP,
                intent,
                query,
                "",
                "",
                "",
                20,
                List.of(),
                intent.name()
        );
    }

    private AssistantPlan existencePlan(String query, String kindCode) {
        return new AssistantPlan(
                AssistantRetrievalType.CRM_SEARCH,
                AssistantIntent.BUSINESS_PARTNER_EXISTENCE,
                "Do we have customer with name " + query + "?",
                query,
                kindCode,
                "",
                20,
                List.of(AssistantToolCall.of("crm.searchRecords", Map.of("query", query, "kindCode", kindCode))),
                "BUSINESS_PARTNER_EXISTENCE for " + query + " [" + kindCode + "]"
        );
    }

    private LinkedHashMap<String, String> fields(String firstKey, String firstValue, String... rest) {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        values.put(firstKey, firstValue);
        for (int index = 0; index < rest.length; index += 2) {
            values.put(rest[index], rest[index + 1]);
        }
        return values;
    }
}
