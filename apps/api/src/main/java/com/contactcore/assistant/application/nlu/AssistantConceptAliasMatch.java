// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

public record AssistantConceptAliasMatch(
        AssistantConcept concept,
        String alias,
        String canonicalValue,
        int start,
        int end
) {
    public AssistantConceptAliasMatch {
        if (concept == null) {
            throw new IllegalArgumentException("concept must not be null.");
        }
        alias = alias == null ? "" : alias.trim();
        canonicalValue = canonicalValue == null ? "" : canonicalValue.trim();
        if (start < 0 || end < start) {
            throw new IllegalArgumentException("Invalid alias match range.");
        }
    }

    boolean overlaps(AssistantConceptAliasMatch other) {
        return other != null && start < other.end && other.start < end;
    }
}
