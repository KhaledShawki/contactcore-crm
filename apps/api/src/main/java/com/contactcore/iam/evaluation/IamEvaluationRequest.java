// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamPolicyDocument;
import com.contactcore.iam.domain.IamPrincipalRef;
import com.contactcore.iam.domain.IamResource;
import java.util.List;
import java.util.Objects;

public record IamEvaluationRequest(
        IamPrincipalRef principal,
        IamAction action,
        IamResource resource,
        IamRequestContext context,
        List<IamPolicyDocument> identityPolicies,
        List<IamPolicyDocument> permissionBoundaryPolicies,
        List<IamPolicyDocument> sessionPolicies,
        ProductCapabilityBoundary productCapabilityBoundary
) {
    public IamEvaluationRequest {
        principal = Objects.requireNonNull(principal, "principal must not be null");
        action = Objects.requireNonNull(action, "action must not be null");
        resource = Objects.requireNonNull(resource, "resource must not be null");
        context = context == null ? IamRequestContext.empty() : context;
        identityPolicies = identityPolicies == null ? List.of() : List.copyOf(identityPolicies);
        permissionBoundaryPolicies = permissionBoundaryPolicies == null ? List.of() : List.copyOf(permissionBoundaryPolicies);
        sessionPolicies = sessionPolicies == null ? List.of() : List.copyOf(sessionPolicies);
        productCapabilityBoundary = productCapabilityBoundary == null ? ProductCapabilityBoundary.allowAll() : productCapabilityBoundary;
    }

    public static Builder builder(IamPrincipalRef principal, IamAction action, IamResource resource) {
        return new Builder(principal, action, resource);
    }

    public static final class Builder {
        private final IamPrincipalRef principal;
        private final IamAction action;
        private final IamResource resource;
        private IamRequestContext context = IamRequestContext.empty();
        private List<IamPolicyDocument> identityPolicies = List.of();
        private List<IamPolicyDocument> permissionBoundaryPolicies = List.of();
        private List<IamPolicyDocument> sessionPolicies = List.of();
        private ProductCapabilityBoundary productCapabilityBoundary = ProductCapabilityBoundary.allowAll();

        private Builder(IamPrincipalRef principal, IamAction action, IamResource resource) {
            this.principal = principal;
            this.action = action;
            this.resource = resource;
        }

        public Builder context(IamRequestContext context) {
            this.context = context;
            return this;
        }

        public Builder identityPolicies(List<IamPolicyDocument> identityPolicies) {
            this.identityPolicies = identityPolicies;
            return this;
        }

        public Builder permissionBoundaryPolicies(List<IamPolicyDocument> permissionBoundaryPolicies) {
            this.permissionBoundaryPolicies = permissionBoundaryPolicies;
            return this;
        }

        public Builder sessionPolicies(List<IamPolicyDocument> sessionPolicies) {
            this.sessionPolicies = sessionPolicies;
            return this;
        }

        public Builder productCapabilityBoundary(ProductCapabilityBoundary productCapabilityBoundary) {
            this.productCapabilityBoundary = productCapabilityBoundary;
            return this;
        }

        public IamEvaluationRequest build() {
            return new IamEvaluationRequest(
                    principal,
                    action,
                    resource,
                    context,
                    identityPolicies,
                    permissionBoundaryPolicies,
                    sessionPolicies,
                    productCapabilityBoundary
            );
        }
    }
}
