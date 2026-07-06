// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamPolicyDocument;
import java.util.List;

public record IamResolvedPolicies(
        List<IamPolicyDocument> identityPolicies,
        List<IamPolicyDocument> permissionBoundaryPolicies,
        List<IamPolicyDocument> sessionPolicies
) {
    public IamResolvedPolicies {
        identityPolicies = identityPolicies == null ? List.of() : List.copyOf(identityPolicies);
        permissionBoundaryPolicies = permissionBoundaryPolicies == null ? List.of() : List.copyOf(permissionBoundaryPolicies);
        sessionPolicies = sessionPolicies == null ? List.of() : List.copyOf(sessionPolicies);
    }

    public static IamResolvedPolicies empty() {
        return new IamResolvedPolicies(List.of(), List.of(), List.of());
    }
}
