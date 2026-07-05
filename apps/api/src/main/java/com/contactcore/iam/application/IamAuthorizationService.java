// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamPolicyDocument;
import com.contactcore.iam.domain.IamPrincipalRef;
import com.contactcore.iam.domain.IamResource;
import com.contactcore.iam.evaluation.AccessEvaluator;
import com.contactcore.iam.evaluation.IamDecision;
import com.contactcore.iam.evaluation.IamEvaluationRequest;
import com.contactcore.iam.evaluation.IamRequestContext;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class IamAuthorizationService {
    private final AccessEvaluator evaluator;
    private final SecurityRolePolicyResolver rolePolicyResolver;
    private final ContactCoreProductCapabilityCatalog capabilityCatalog;
    private final ContactCoreTenantContext tenantContext;

    public IamAuthorizationService(AccessEvaluator evaluator, SecurityRolePolicyResolver rolePolicyResolver,
                                   ContactCoreProductCapabilityCatalog capabilityCatalog,
                                   ContactCoreTenantContext tenantContext) {
        this.evaluator = evaluator;
        this.rolePolicyResolver = rolePolicyResolver;
        this.capabilityCatalog = capabilityCatalog;
        this.tenantContext = tenantContext;
    }

    public IamDecision evaluate(IamPrincipalRef principal, Collection<String> roleCodes, IamAction action, IamResource resource) {
        return evaluate(principal, roleCodes, action, resource, IamRequestContext.empty());
    }

    public IamDecision evaluate(IamPrincipalRef principal, Collection<String> roleCodes, IamAction action,
                                IamResource resource, IamRequestContext context) {
        String tenantId = tenantContext.currentTenantId();
        List<IamPolicyDocument> policies = rolePolicyResolver.resolve(roleCodes, tenantId);
        return evaluator.evaluate(IamEvaluationRequest.builder(principal, action, resource)
                .context(context)
                .identityPolicies(policies)
                .productCapabilityBoundary(capabilityCatalog.boundaryForTenant(tenantId))
                .build());
    }
}
