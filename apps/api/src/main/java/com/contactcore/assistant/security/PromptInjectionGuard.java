// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class PromptInjectionGuard {
    private static final List<String> BLOCKED_PHRASES = List.of(
            "ignore previous instructions",
            "ignore all previous instructions",
            "show system prompt",
            "print system prompt",
            "reveal system prompt",
            "developer message",
            "bypass safety",
            "disregard the above"
    );

    public boolean suspicious(String text) {
        String normalized = text == null ? "" : text.toLowerCase(Locale.ROOT);
        return BLOCKED_PHRASES.stream().anyMatch(normalized::contains);
    }
}
