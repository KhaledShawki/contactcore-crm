// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamPrincipalRef;
import com.contactcore.iam.domain.IamResource;
import com.contactcore.iam.evaluation.IamDecision;
import com.contactcore.iam.evaluation.IamDecisionReason;
import java.util.List;
import org.junit.jupiter.api.Test;

class CurrentIamAuthorizationServiceTest {
    private final CurrentIamSubject subject = CurrentIamSubject.of(IamPrincipalRef.user(7L), List.of("SALES_USER"));
    private final CurrentIamSubjectResolver subjectResolver = () -> subject;
    private final IamAuthorizationService authorizationService = mock(IamAuthorizationService.class);
    private final CurrentIamAuthorizationService service = new CurrentIamAuthorizationService(subjectResolver, authorizationService);
    private final IamAction action = IamAction.of("commercial:ReadDocument");
    private final IamResource resource = IamResource.of("contactcore:default:commercial:document/123");

    @Test
    void evaluatesCurrentSubjectAgainstIamAuthorizationService() {
        IamDecision allowed = IamDecision.allow(List.of("AllowCommercialRead"));
        when(authorizationService.evaluate(subject.principal(), subject.roleCodes(), action, resource, com.contactcore.iam.evaluation.IamRequestContext.empty()))
                .thenReturn(allowed);

        IamDecision decision = service.evaluate(action, resource);

        assertThat(decision).isSameAs(allowed);
        verify(authorizationService).evaluate(subject.principal(), subject.roleCodes(), action, resource, com.contactcore.iam.evaluation.IamRequestContext.empty());
    }

    @Test
    void requireAllowedThrowsAccessDeniedWithDecisionContext() {
        IamDecision denied = IamDecision.deny(IamDecisionReason.MISSING_ALLOW, List.of(), "No matching allow statement.");
        when(authorizationService.evaluate(subject.principal(), subject.roleCodes(), action, resource, com.contactcore.iam.evaluation.IamRequestContext.empty()))
                .thenReturn(denied);

        assertThatThrownBy(() -> service.requireAllowed(action, resource))
                .isInstanceOfSatisfying(IamAccessDeniedException.class, exception -> {
                    assertThat(exception.principal()).isEqualTo(subject.principal());
                    assertThat(exception.action()).isEqualTo(action);
                    assertThat(exception.resource()).isEqualTo(resource);
                    assertThat(exception.decision()).isSameAs(denied);
                });
    }
}
