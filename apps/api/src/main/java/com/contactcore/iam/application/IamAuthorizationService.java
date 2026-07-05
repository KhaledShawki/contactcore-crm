// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamPrincipalRef;
import com.contactcore.iam.domain.IamResource;
import com.contactcore.iam.evaluation.AccessEvaluator;
import com.contactcore.iam.evaluation.IamDecision;
import com.contactcore.iam.evaluation.IamEvaluationRequest;
import com.contactcore.iam.evaluation.IamRequestContext;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class IamAuthorizationService {
    private final AccessEvaluator evaluator;
    private final IamPolicyResolver policyResolver;
    private final ContactCoreProductCapabilityCatalog capabilityCatalog;
    private final ContactCoreTenantContext tenantContext;

    public IamAuthorizationService(AccessEvaluator evaluator, IamPolicyResolver policyResolver,
                                   ContactCoreProductCapabilityCatalog capabilityCatalog,
                                   ContactCoreTenantContext tenantContext) {
        this.evaluator = evaluator;
        this.policyResolver = policyResolver;
        this.capabilityCatalog = capabilityCatalog;
        this.tenantContext = tenantContext;
    }

    public IamDecision evaluate(IamPrincipalRef principal, Collection<String> roleCodes, IamAction action, IamResource resource) {
        return evaluate(principal, roleCodes, action, resource, IamRequestContext.empty());
    }

    public IamDecision evaluate(IamPrincipalRef principal, Collection<String> roleCodes, IamAction action,
                                IamResource resource, IamRequestContext context) {
        String tenantId = tenantContext.currentTenantId();
        IamResolvedPolicies resolvedPolicies = policyResolver.resolve(principal, roleCodes, tenantId);
        return evaluator.evaluate(IamEvaluationRequest.builder(principal, action, resource)
                .context(context)
                .identityPolicies(resolvedPolicies.identityPolicies())
                .permissionBoundaryPolicies(resolvedPolicies.permissionBoundaryPolicies())
                .sessionPolicies(resolvedPolicies.sessionPolicies())
                .productCapabilityBoundary(capabilityCatalog.boundaryForTenant(tenantId))
                .build());
    }
}
