// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AssistantEntityExtractorTest {
    private final AssistantEntityExtractor extractor = new AssistantEntityExtractor();

    @Test
    void extractsNameFromWithNameLookup() {
        AssistantEntityExtractor.AssistantCriteria criteria = extractor.extract("Do we have customer with name Customer ekdwkskpdm?");

        assertThat(criteria.searchTerm()).isEqualTo("Customer ekdwkskpdm");
        assertThat(criteria.kindCode()).isEqualTo("CUSTOMER");
    }

    @Test
    void extractsNameFromExistenceLookupWithoutWithNameMarker() {
        AssistantEntityExtractor.AssistantCriteria criteria = extractor.extract("Is there a supplier called Meyer AG?");

        assertThat(criteria.searchTerm()).isEqualTo("Meyer AG");
        assertThat(criteria.kindCode()).isEqualTo("SUPPLIER");
    }

    @Test
    void preservesQuotedSearchTerms() {
        AssistantEntityExtractor.AssistantCriteria criteria = extractor.extract("Find records related to \"Meyer Digital GmbH\".");

        assertThat(criteria.searchTerm()).isEqualTo("Meyer Digital GmbH");
    }
}
