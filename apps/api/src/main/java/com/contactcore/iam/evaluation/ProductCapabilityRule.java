// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamResource;
import java.util.Objects;

public record ProductCapabilityRule(IamAction action, IamResource resource) {
    public ProductCapabilityRule {
        action = Objects.requireNonNull(action, "action must not be null");
        resource = Objects.requireNonNull(resource, "resource must not be null");
    }

    boolean matches(IamAction requestedAction, IamResource requestedResource, IamMatcher matcher) {
        return matcher.matchesAction(action, requestedAction) && matcher.matchesResource(resource, requestedResource);
    }
}
