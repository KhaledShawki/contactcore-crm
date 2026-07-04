// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

@Component
public class CompositeAssistantNluRouter implements AssistantNluRouter {
    private final List<AssistantNluCandidateRouter> candidateRouters;

    @Autowired
    public CompositeAssistantNluRouter(List<AssistantNluCandidateRouter> candidateRouters) {
        this.candidateRouters = candidateRouters == null ? List.of() : candidateRouters.stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .toList();
    }

    public CompositeAssistantNluRouter(AssistantMessageNormalizer normalizer) {
        this(List.of(new CatalogAssistantNluRouter(normalizer), new NoOpStructuredAssistantNluRouter()));
    }

    @Override
    public AssistantNluDecision route(AssistantNluRequest request) {
        AssistantNluRequest safeRequest = request == null ? new AssistantNluRequest("") : request;
        for (AssistantNluCandidateRouter candidateRouter : candidateRouters) {
            var decision = candidateRouter.routeCandidate(safeRequest);
            if (decision.isPresent()) {
                return decision.get();
            }
        }
        return new AssistantNluDecision(new AssistantNormalizedMessage(safeRequest.message(), safeRequest.message()), AssistantNluDecisionSource.FALLBACK, 0.0);
    }
}
