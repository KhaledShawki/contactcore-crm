// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

import java.util.List;
import java.util.Objects;

public record IamDecision(
        IamDecisionEffect effect,
        IamDecisionReason reason,
        List<String> matchedStatementIds,
        String message
) {
    public IamDecision {
        effect = Objects.requireNonNull(effect, "effect must not be null");
        reason = Objects.requireNonNull(reason, "reason must not be null");
        matchedStatementIds = matchedStatementIds == null ? List.of() : List.copyOf(matchedStatementIds);
        message = message == null ? reason.name() : message;
    }

    public static IamDecision allow(List<String> matchedStatementIds) {
        return new IamDecision(IamDecisionEffect.ALLOW, IamDecisionReason.ALLOW, matchedStatementIds, "Access allowed.");
    }

    public static IamDecision deny(IamDecisionReason reason, List<String> matchedStatementIds, String message) {
        return new IamDecision(IamDecisionEffect.DENY, reason, matchedStatementIds, message);
    }

    public boolean allowed() {
        return effect == IamDecisionEffect.ALLOW;
    }
}
