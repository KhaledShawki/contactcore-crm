// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

import java.util.List;
import java.util.Set;

public record AssistantNormalizedMessage(
        String originalText,
        String canonicalText,
        List<AssistantConceptAliasMatch> conceptMatches,
        Set<AssistantConcept> detectedConcepts,
        Set<AssistantConcept> detectedBusinessPartnerTypes,
        boolean hasConflictingBusinessPartnerTypes
) {
    public AssistantNormalizedMessage {
        originalText = originalText == null ? "" : originalText.trim();
        canonicalText = canonicalText == null ? "" : canonicalText.trim();
        conceptMatches = conceptMatches == null ? List.of() : List.copyOf(conceptMatches);
        detectedConcepts = detectedConcepts == null ? Set.of() : Set.copyOf(detectedConcepts);
        detectedBusinessPartnerTypes = detectedBusinessPartnerTypes == null ? Set.of() : Set.copyOf(detectedBusinessPartnerTypes);
    }

    public AssistantNormalizedMessage(String originalText, String canonicalText) {
        this(originalText, canonicalText, List.of(), Set.of(), Set.of(), false);
    }
}
