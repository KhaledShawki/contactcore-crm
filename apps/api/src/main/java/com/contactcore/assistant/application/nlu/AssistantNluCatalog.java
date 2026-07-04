// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

import java.util.List;

public record AssistantNluCatalog(
        int version,
        List<String> ignoredTokens,
        List<AssistantNluAliasEntry> aliases
) {
    public AssistantNluCatalog {
        ignoredTokens = ignoredTokens == null ? List.of() : ignoredTokens.stream()
                .filter(token -> token != null && !token.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        aliases = aliases == null ? List.of() : List.copyOf(aliases);
    }
}
