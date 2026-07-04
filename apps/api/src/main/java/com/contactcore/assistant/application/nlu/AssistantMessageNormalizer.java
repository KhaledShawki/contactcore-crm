// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

import org.springframework.stereotype.Component;

@Component
public class AssistantMessageNormalizer {
    private final AssistantConceptAliasRegistry aliases;

    public AssistantMessageNormalizer() {
        this(new AssistantConceptAliasRegistry());
    }

    public AssistantMessageNormalizer(AssistantConceptAliasRegistry aliases) {
        this.aliases = aliases;
    }

    public AssistantNormalizedMessage normalize(String message) {
        String original = message == null ? "" : message.trim();
        AssistantAliasNormalizationResult result = aliases.normalize(original);
        String canonical = result.canonicalText().isBlank() ? original : result.canonicalText();
        return new AssistantNormalizedMessage(
                result.originalText(),
                canonical,
                result.conceptMatches(),
                result.detectedConcepts(),
                result.detectedBusinessPartnerTypes(),
                result.hasConflictingBusinessPartnerTypes()
        );
    }
}
