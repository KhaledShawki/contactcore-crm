// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

import java.util.Optional;

public interface AssistantNluCandidateRouter {
    Optional<AssistantNluDecision> routeCandidate(AssistantNluRequest request);
}
