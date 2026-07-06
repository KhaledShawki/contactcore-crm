// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamResource;
import java.util.List;
import java.util.Objects;

public record ProductCapabilityBoundary(List<ProductCapabilityRule> rules) {
    public static final ProductCapabilityBoundary ALLOW_ALL = new ProductCapabilityBoundary(List.of(
            new ProductCapabilityRule(IamAction.of("*"), IamResource.ALL)
    ));

    public ProductCapabilityBoundary {
        rules = rules == null ? List.of() : rules.stream()
                .map(rule -> Objects.requireNonNull(rule, "rule must not be null"))
                .toList();
    }

    public static ProductCapabilityBoundary allowAll() {
        return ALLOW_ALL;
    }

    public boolean supports(IamAction action, IamResource resource, IamMatcher matcher) {
        return rules.stream().anyMatch(rule -> rule.matches(action, resource, matcher));
    }
}
