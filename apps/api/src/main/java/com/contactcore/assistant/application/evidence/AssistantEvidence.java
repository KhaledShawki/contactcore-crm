// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.evidence;

import com.contactcore.assistant.retrieval.AssistantRetrievalResult;

public record AssistantEvidence(
        AssistantRetrievalResult retrieval,
        boolean aggregateEvidenceAllowed,
        String reason
) {
    public boolean valid() {
        return retrieval != null;
    }
}
