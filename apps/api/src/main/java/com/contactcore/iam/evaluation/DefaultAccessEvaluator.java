// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

import com.contactcore.iam.domain.IamEffect;
import com.contactcore.iam.domain.IamPolicyDocument;
import com.contactcore.iam.domain.IamPolicyStatement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DefaultAccessEvaluator implements AccessEvaluator {
    private final IamMatcher matcher;
    private final IamConditionEvaluator conditionEvaluator;

    public DefaultAccessEvaluator(IamMatcher matcher, IamConditionEvaluator conditionEvaluator) {
        this.matcher = matcher;
        this.conditionEvaluator = conditionEvaluator;
    }

    @Override
    public IamDecision evaluate(IamEvaluationRequest request) {
        if (!request.productCapabilityBoundary().supports(request.action(), request.resource(), matcher)) {
            return IamDecision.deny(
                    IamDecisionReason.UNSUPPORTED_ACTION,
                    List.of(),
                    "The requested action is not supported by the ContactCore product capability boundary."
            );
        }

        StatementMatch identityMatch = evaluatePolicies(request.identityPolicies(), request);
        if (!identityMatch.explicitDenies().isEmpty()) {
            return IamDecision.deny(IamDecisionReason.EXPLICIT_DENY, identityMatch.explicitDenies(), "Access denied by an explicit deny statement.");
        }
        if (identityMatch.allows().isEmpty()) {
            return IamDecision.deny(IamDecisionReason.MISSING_ALLOW, List.of(), "No identity policy allows the requested action.");
        }

        if (!request.permissionBoundaryPolicies().isEmpty()) {
            StatementMatch boundaryMatch = evaluatePolicies(request.permissionBoundaryPolicies(), request);
            if (!boundaryMatch.explicitDenies().isEmpty()) {
                return IamDecision.deny(IamDecisionReason.PERMISSION_BOUNDARY_DENY, boundaryMatch.explicitDenies(), "Access denied by permission boundary.");
            }
            if (boundaryMatch.allows().isEmpty()) {
                return IamDecision.deny(IamDecisionReason.PERMISSION_BOUNDARY_DENY, List.of(), "Permission boundary does not allow the requested action.");
            }
        }

        if (!request.sessionPolicies().isEmpty()) {
            StatementMatch sessionMatch = evaluatePolicies(request.sessionPolicies(), request);
            if (!sessionMatch.explicitDenies().isEmpty()) {
                return IamDecision.deny(IamDecisionReason.SESSION_POLICY_DENY, sessionMatch.explicitDenies(), "Access denied by session policy.");
            }
            if (sessionMatch.allows().isEmpty()) {
                return IamDecision.deny(IamDecisionReason.SESSION_POLICY_DENY, List.of(), "Session policy does not allow the requested action.");
            }
        }

        return IamDecision.allow(identityMatch.allows());
    }

    private StatementMatch evaluatePolicies(List<IamPolicyDocument> policies, IamEvaluationRequest request) {
        List<String> allows = new ArrayList<>();
        List<String> explicitDenies = new ArrayList<>();
        for (IamPolicyDocument policy : policies) {
            for (IamPolicyStatement statement : policy.statements()) {
                if (matches(statement, request)) {
                    String sid = statement.sid() == null || statement.sid().isBlank() ? "<unnamed>" : statement.sid();
                    if (statement.effect() == IamEffect.DENY) {
                        explicitDenies.add(sid);
                    } else {
                        allows.add(sid);
                    }
                }
            }
        }
        return new StatementMatch(List.copyOf(allows), List.copyOf(explicitDenies));
    }

    private boolean matches(IamPolicyStatement statement, IamEvaluationRequest request) {
        boolean actionMatches = statement.actions().stream().anyMatch(action -> matcher.matchesAction(action, request.action()));
        if (!actionMatches) {
            return false;
        }
        boolean resourceMatches = statement.resources().stream().anyMatch(resource -> matcher.matchesResource(resource, request.resource()));
        return resourceMatches && conditionEvaluator.matches(statement.conditions(), request.context());
    }

    private record StatementMatch(List<String> allows, List<String> explicitDenies) {}
}
