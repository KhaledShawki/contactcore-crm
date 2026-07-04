// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public record AssistantAliasNormalizationResult(
        String originalText,
        String canonicalText,
        List<AssistantConceptAliasMatch> conceptMatches
) {
    private static final Set<AssistantConcept> BUSINESS_PARTNER_TYPES = Set.of(
            AssistantConcept.CUSTOMER,
            AssistantConcept.SUPPLIER,
            AssistantConcept.LEAD
    );

    public AssistantAliasNormalizationResult {
        originalText = originalText == null ? "" : originalText.trim();
        canonicalText = canonicalText == null ? "" : canonicalText.trim();
        conceptMatches = conceptMatches == null ? List.of() : List.copyOf(conceptMatches);
    }

    public Set<AssistantConcept> detectedConcepts() {
        return conceptMatches.stream()
                .map(AssistantConceptAliasMatch::concept)
                .collect(java.util.stream.Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(AssistantConcept::name))));
    }

    public Set<AssistantConcept> detectedBusinessPartnerTypes() {
        return detectedConcepts().stream()
                .filter(BUSINESS_PARTNER_TYPES::contains)
                .collect(java.util.stream.Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(AssistantConcept::name))));
    }

    public boolean hasConflictingBusinessPartnerTypes() {
        return detectedBusinessPartnerTypes().size() > 1;
    }
}
