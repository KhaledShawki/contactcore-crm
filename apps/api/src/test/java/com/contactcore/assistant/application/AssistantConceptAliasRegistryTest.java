// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.assistant.application.nlu.AssistantConcept;
import com.contactcore.assistant.application.nlu.AssistantConceptAliasRegistry;
import org.junit.jupiter.api.Test;

class AssistantConceptAliasRegistryTest {
    private final AssistantConceptAliasRegistry registry = new AssistantConceptAliasRegistry();

    @Test
    void usesLongestSpecificAliasBeforeShorterOverlappingArabicAlias() {
        var result = registry.normalize("اعرض العميل المحتمل Meyer");

        assertThat(result.canonicalText()).isEqualTo("show me lead Meyer");
        assertThat(result.detectedBusinessPartnerTypes()).containsExactly(AssistantConcept.LEAD);
        assertThat(result.hasConflictingBusinessPartnerTypes()).isFalse();
    }

    @Test
    void reportsConflictingBusinessPartnerTypeConcepts() {
        var result = registry.normalize("show customer supplier Meyer");

        assertThat(result.detectedBusinessPartnerTypes())
                .containsExactlyInAnyOrder(AssistantConcept.CUSTOMER, AssistantConcept.SUPPLIER);
        assertThat(result.hasConflictingBusinessPartnerTypes()).isTrue();
    }

    @Test
    void doesNotReportConflictForOverlappingSpecificAndGenericArabicLeadAlias() {
        var result = registry.normalize("العميل المحتمل Meyer");

        assertThat(result.detectedBusinessPartnerTypes()).containsExactly(AssistantConcept.LEAD);
        assertThat(result.hasConflictingBusinessPartnerTypes()).isFalse();
    }
}
