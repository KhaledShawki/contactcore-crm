// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.assistant.application.planning.AssistantIntent;
import com.contactcore.assistant.application.planning.AssistantIntentClassifier;
import org.junit.jupiter.api.Test;

class AssistantIntentClassifierTest {
    private final AssistantEntityExtractor extractor = new AssistantEntityExtractor();
    private final AssistantIntentClassifier classifier = new AssistantIntentClassifier();


    @Test
    void classifiesGreetingAsNoToolIntent() {
        String message = "hi";

        assertThat(classifier.classify(message, extractor.extract(message)))
                .isEqualTo(AssistantIntent.GREETING);
    }

    @Test
    void classifiesRandomTextAsUnclearInsteadOfCrmSummary() {
        String message = "tandom things";

        assertThat(classifier.classify(message, extractor.extract(message)))
                .isEqualTo(AssistantIntent.UNCLEAR_REQUEST);
    }

    @Test
    void doesNotTreatGenericAboutTextAsCrmSearch() {
        String message = "tell me about random things";

        assertThat(classifier.classify(message, extractor.extract(message)))
                .isEqualTo(AssistantIntent.UNCLEAR_REQUEST);
    }

    @Test
    void classifiesCustomerCountQuestionAsCrmSummary() {
        String message = "How many customers do we have?";

        assertThat(classifier.classify(message, extractor.extract(message)))
                .isEqualTo(AssistantIntent.CRM_SUMMARY);
    }


    @Test
    void classifiesIdentityQuestionBeforeCrmSummaryFallback() {
        String message = "what are you?";

        assertThat(classifier.classify(message, extractor.extract(message)))
                .isEqualTo(AssistantIntent.ASSISTANT_IDENTITY);
    }

    @Test
    void classifiesCapabilitiesQuestionBeforeCrmSummaryFallback() {
        String message = "what can you help me with?";

        assertThat(classifier.classify(message, extractor.extract(message)))
                .isEqualTo(AssistantIntent.ASSISTANT_CAPABILITIES);
    }

    @Test
    void classifiesCustomerExistenceLookupBeforeSummary() {
        String message = "Do we have customer with name Customer ekdwkskpdm?";

        assertThat(classifier.classify(message, extractor.extract(message)))
                .isEqualTo(AssistantIntent.BUSINESS_PARTNER_EXISTENCE);
    }

    @Test
    void classifiesMissingLeadContacts() {
        String message = "Which leads are missing contact persons?";

        assertThat(classifier.classify(message, extractor.extract(message)))
                .isEqualTo(AssistantIntent.LEADS_MISSING_CONTACTS);
    }

    @Test
    void classifiesWriteRequestsAsUnsupported() {
        String message = "Delete this customer";

        assertThat(classifier.classify(message, extractor.extract(message)))
                .isEqualTo(AssistantIntent.UNSUPPORTED);
    }
}
