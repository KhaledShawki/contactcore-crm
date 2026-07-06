// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamConditionBlock;
import com.contactcore.iam.domain.IamConditionOperator;
import com.contactcore.iam.domain.IamEffect;
import com.contactcore.iam.domain.IamPolicyDocument;
import com.contactcore.iam.domain.IamPolicyStatement;
import com.contactcore.iam.domain.IamPrincipalRef;
import com.contactcore.iam.domain.IamResource;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DefaultAccessEvaluatorTest {
    private final IamMatcher matcher = new IamMatcher();
    private final AccessEvaluator evaluator = new DefaultAccessEvaluator(matcher, new IamConditionEvaluator(matcher));
    private final IamPrincipalRef principal = IamPrincipalRef.user(42L);

    @Test
    void deniesByDefaultWhenNoAllowStatementMatches() {
        IamDecision decision = evaluator.evaluate(request(List.of(IamPolicyDocument.empty())));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo(IamDecisionReason.MISSING_ALLOW);
    }

    @Test
    void allowsMatchingIdentityPolicy() {
        IamPolicyDocument policy = policy(IamPolicyStatement.allow(
                "AllowCommercialRead",
                List.of(IamAction.of("commercial:ReadDocument")),
                List.of(IamResource.of("contactcore:default:commercial:document/*"))
        ));

        IamDecision decision = evaluator.evaluate(request(List.of(policy)));

        assertThat(decision.allowed()).isTrue();
        assertThat(decision.matchedStatementIds()).containsExactly("AllowCommercialRead");
    }

    @Test
    void explicitDenyOverridesAllow() {
        IamPolicyDocument policy = policy(
                IamPolicyStatement.allow(
                        "AllowCommercialRead",
                        List.of(IamAction.of("commercial:ReadDocument")),
                        List.of(IamResource.of("contactcore:default:commercial:document/*"))
                ),
                IamPolicyStatement.deny(
                        "DenyCommercialRead",
                        List.of(IamAction.of("commercial:ReadDocument")),
                        List.of(IamResource.of("contactcore:default:commercial:document/123"))
                )
        );

        IamDecision decision = evaluator.evaluate(request(List.of(policy)));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo(IamDecisionReason.EXPLICIT_DENY);
        assertThat(decision.matchedStatementIds()).containsExactly("DenyCommercialRead");
    }

    @Test
    void permissionBoundaryCanOnlyReduceAccess() {
        IamPolicyDocument identityPolicy = policy(IamPolicyStatement.allow(
                "AllowAllCommercial",
                List.of(IamAction.of("commercial:*")),
                List.of(IamResource.of("contactcore:default:commercial:*"))
        ));
        IamPolicyDocument boundaryPolicy = policy(IamPolicyStatement.allow(
                "AllowOnlyItems",
                List.of(IamAction.of("commercial:ReadItem")),
                List.of(IamResource.of("contactcore:default:commercial:item/*"))
        ));

        IamDecision decision = evaluator.evaluate(IamEvaluationRequest.builder(
                        principal,
                        IamAction.of("commercial:ReadDocument"),
                        IamResource.of("contactcore:default:commercial:document/123")
                )
                .identityPolicies(List.of(identityPolicy))
                .permissionBoundaryPolicies(List.of(boundaryPolicy))
                .build());

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo(IamDecisionReason.PERMISSION_BOUNDARY_DENY);
    }

    @Test
    void productCapabilityBoundaryDeniesUnsupportedActionsEvenForAdminPolicies() {
        IamPolicyDocument adminPolicy = policy(IamPolicyStatement.allow(
                "AllowEverything",
                List.of(IamAction.of("*")),
                List.of(IamResource.of("contactcore:default:*"))
        ));
        ProductCapabilityBoundary boundary = new ProductCapabilityBoundary(List.of(
                new ProductCapabilityRule(
                        IamAction.of("commercial:ReadDocument"),
                        IamResource.of("contactcore:default:commercial:document/*")
                )
        ));

        IamDecision decision = evaluator.evaluate(IamEvaluationRequest.builder(
                        principal,
                        IamAction.of("commercial:PostDocument"),
                        IamResource.of("contactcore:default:commercial:document/123")
                )
                .identityPolicies(List.of(adminPolicy))
                .productCapabilityBoundary(boundary)
                .build());

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo(IamDecisionReason.UNSUPPORTED_ACTION);
    }

    @Test
    void conditionMustMatchRequestContext() {
        IamConditionBlock condition = new IamConditionBlock(Map.of(
                IamConditionOperator.STRING_EQUALS,
                Map.of("sourceSystem", List.of("SAP_B1"))
        ));
        IamPolicyDocument policy = policy(new IamPolicyStatement(
                "AllowSapB1Only",
                IamEffect.ALLOW,
                List.of(IamAction.of("commercial:ReadDocument")),
                List.of(IamResource.of("contactcore:default:commercial:document/*")),
                condition
        ));

        IamDecision denied = evaluator.evaluate(IamEvaluationRequest.builder(
                        principal,
                        IamAction.of("commercial:ReadDocument"),
                        IamResource.of("contactcore:default:commercial:document/123")
                )
                .identityPolicies(List.of(policy))
                .context(new IamRequestContext(Map.of("sourceSystem", "EXTERNAL")))
                .build());
        IamDecision allowed = evaluator.evaluate(IamEvaluationRequest.builder(
                        principal,
                        IamAction.of("commercial:ReadDocument"),
                        IamResource.of("contactcore:default:commercial:document/123")
                )
                .identityPolicies(List.of(policy))
                .context(new IamRequestContext(Map.of("sourceSystem", "SAP_B1")))
                .build());

        assertThat(denied.allowed()).isFalse();
        assertThat(denied.reason()).isEqualTo(IamDecisionReason.MISSING_ALLOW);
        assertThat(allowed.allowed()).isTrue();
    }

    private IamEvaluationRequest request(List<IamPolicyDocument> policies) {
        return IamEvaluationRequest.builder(
                        principal,
                        IamAction.of("commercial:ReadDocument"),
                        IamResource.of("contactcore:default:commercial:document/123")
                )
                .identityPolicies(policies)
                .build();
    }

    private IamPolicyDocument policy(IamPolicyStatement... statements) {
        return new IamPolicyDocument(IamPolicyDocument.CURRENT_VERSION, List.of(statements));
    }
}
