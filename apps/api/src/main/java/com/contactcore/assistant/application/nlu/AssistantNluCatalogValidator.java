// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AssistantNluCatalogValidator {
    public AssistantNluCatalog validate(AssistantNluCatalog catalog) {
        if (catalog == null) {
            throw new AssistantNluCatalogException("Assistant NLU catalog is missing.");
        }
        if (catalog.version() < 1) {
            throw new AssistantNluCatalogException("Assistant NLU catalog version must be greater than zero.");
        }
        if (catalog.aliases().isEmpty()) {
            throw new AssistantNluCatalogException("Assistant NLU catalog must define at least one alias entry.");
        }

        Map<String, AssistantNluAliasEntry> aliasesByPhrase = new HashMap<>();
        for (AssistantNluAliasEntry entry : catalog.aliases()) {
            validateEntry(entry);
            for (String phrase : entry.phrases()) {
                String normalizedPhrase = normalizeKey(phrase);
                AssistantNluAliasEntry previous = aliasesByPhrase.putIfAbsent(normalizedPhrase, entry);
                if (previous != null && !sameMeaning(previous, entry)) {
                    throw new AssistantNluCatalogException(
                            "Assistant NLU alias '" + phrase + "' is mapped to multiple canonical meanings."
                    );
                }
            }
        }
        return catalog;
    }

    private void validateEntry(AssistantNluAliasEntry entry) {
        if (entry == null) {
            throw new AssistantNluCatalogException("Assistant NLU catalog contains an empty alias entry.");
        }
        if (entry.canonicalText().isBlank()) {
            throw new AssistantNluCatalogException("Assistant NLU alias entry is missing canonical text.");
        }
        if (entry.phrases().isEmpty()) {
            throw new AssistantNluCatalogException("Assistant NLU alias entry '" + entry.canonicalText() + "' has no phrases.");
        }
    }

    private boolean sameMeaning(AssistantNluAliasEntry left, AssistantNluAliasEntry right) {
        return left.concept() == right.concept() && left.canonicalText().equals(right.canonicalText());
    }

    private String normalizeKey(String value) {
        return value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
