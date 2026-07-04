// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

import java.util.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
public class NoOpStructuredAssistantNluRouter implements AssistantNluCandidateRouter {
    @Override
    public Optional<AssistantNluDecision> routeCandidate(AssistantNluRequest request) {
        return Optional.empty();
    }
}
