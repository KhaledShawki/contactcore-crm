// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

public record AssistantNluDecision(
        AssistantNormalizedMessage normalizedMessage,
        AssistantNluDecisionSource source,
        double confidence
) {
    public AssistantNluDecision {
        if (normalizedMessage == null) {
            normalizedMessage = new AssistantNormalizedMessage("", "");
        }
        source = source == null ? AssistantNluDecisionSource.FALLBACK : source;
        confidence = Math.max(0.0, Math.min(1.0, confidence));
    }

    public static AssistantNluDecision catalog(AssistantNormalizedMessage normalizedMessage) {
        double confidence = normalizedMessage.conceptMatches().isEmpty() ? 0.50 : 0.90;
        return new AssistantNluDecision(normalizedMessage, AssistantNluDecisionSource.CATALOG, confidence);
    }
}
