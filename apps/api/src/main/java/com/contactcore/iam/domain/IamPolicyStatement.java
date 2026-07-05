// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.List;
import java.util.Objects;

public record IamPolicyStatement(
        String sid,
        IamEffect effect,
        List<IamAction> actions,
        List<IamResource> resources,
        IamConditionBlock conditions
) {
    public IamPolicyStatement {
        sid = sid == null ? null : sid.trim();
        effect = Objects.requireNonNull(effect, "effect must not be null");
        actions = safeActions(actions);
        resources = safeResources(resources);
        conditions = conditions == null ? IamConditionBlock.empty() : conditions;
    }

    public static IamPolicyStatement allow(String sid, List<IamAction> actions, List<IamResource> resources) {
        return new IamPolicyStatement(sid, IamEffect.ALLOW, actions, resources, IamConditionBlock.empty());
    }

    public static IamPolicyStatement deny(String sid, List<IamAction> actions, List<IamResource> resources) {
        return new IamPolicyStatement(sid, IamEffect.DENY, actions, resources, IamConditionBlock.empty());
    }

    private static List<IamAction> safeActions(List<IamAction> actions) {
        Objects.requireNonNull(actions, "actions must not be null");
        if (actions.isEmpty()) {
            throw new IllegalArgumentException("actions must not be empty");
        }
        return actions.stream()
                .map(action -> Objects.requireNonNull(action, "action must not be null"))
                .toList();
    }

    private static List<IamResource> safeResources(List<IamResource> resources) {
        Objects.requireNonNull(resources, "resources must not be null");
        if (resources.isEmpty()) {
            throw new IllegalArgumentException("resources must not be empty");
        }
        return resources.stream()
                .map(resource -> Objects.requireNonNull(resource, "resource must not be null"))
                .toList();
    }
}
