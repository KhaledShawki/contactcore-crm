// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

import java.util.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class CatalogAssistantNluRouter implements AssistantNluCandidateRouter {
    private final AssistantMessageNormalizer normalizer;

    public CatalogAssistantNluRouter(AssistantMessageNormalizer normalizer) {
        this.normalizer = normalizer;
    }

    @Override
    public Optional<AssistantNluDecision> routeCandidate(AssistantNluRequest request) {
        AssistantNormalizedMessage normalized = normalizer.normalize(request.message());
        return Optional.of(AssistantNluDecision.catalog(normalized));
    }
}
