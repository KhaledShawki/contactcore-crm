// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamResource;
import com.contactcore.iam.evaluation.IamDecision;
import com.contactcore.iam.evaluation.IamRequestContext;
import org.springframework.stereotype.Service;

@Service
public class CurrentIamAuthorizationService {
    private final CurrentIamSubjectResolver subjectResolver;
    private final IamAuthorizationService authorizationService;

    public CurrentIamAuthorizationService(CurrentIamSubjectResolver subjectResolver, IamAuthorizationService authorizationService) {
        this.subjectResolver = subjectResolver;
        this.authorizationService = authorizationService;
    }

    public CurrentIamSubject currentSubject() {
        return subjectResolver.currentSubject();
    }

    public IamDecision evaluate(IamAction action, IamResource resource) {
        return evaluate(action, resource, IamRequestContext.empty());
    }

    public IamDecision evaluate(IamAction action, IamResource resource, IamRequestContext context) {
        return evaluate(currentSubject(), action, resource, context);
    }

    public IamDecision evaluate(CurrentIamSubject subject, IamAction action, IamResource resource) {
        return evaluate(subject, action, resource, IamRequestContext.empty());
    }

    public IamDecision evaluate(CurrentIamSubject subject, IamAction action, IamResource resource, IamRequestContext context) {
        return authorizationService.evaluate(subject.principal(), subject.roleCodes(), action, resource, context);
    }

    public void requireAllowed(IamAction action, IamResource resource) {
        requireAllowed(action, resource, IamRequestContext.empty());
    }

    public void requireAllowed(IamAction action, IamResource resource, IamRequestContext context) {
        requireAllowed(currentSubject(), action, resource, context);
    }

    public void requireAllowed(CurrentIamSubject subject, IamAction action, IamResource resource) {
        requireAllowed(subject, action, resource, IamRequestContext.empty());
    }

    public void requireAllowed(CurrentIamSubject subject, IamAction action, IamResource resource, IamRequestContext context) {
        IamDecision decision = evaluate(subject, action, resource, context);
        if (!decision.allowed()) {
            throw new IamAccessDeniedException(subject.principal(), action, resource, decision);
        }
    }
}
