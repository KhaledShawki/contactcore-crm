// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

import java.util.List;

public record AssistantNluAliasEntry(
        AssistantConcept concept,
        String canonicalText,
        List<String> phrases
) {
    public AssistantNluAliasEntry {
        canonicalText = canonicalText == null ? "" : canonicalText.trim();
        phrases = phrases == null ? List.of() : phrases.stream()
                .filter(phrase -> phrase != null && !phrase.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
    }

    public boolean conceptBacked() {
        return concept != null;
    }
}
