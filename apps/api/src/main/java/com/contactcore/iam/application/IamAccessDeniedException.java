// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamPrincipalRef;
import com.contactcore.iam.domain.IamResource;
import com.contactcore.iam.evaluation.IamDecision;
import com.contactcore.shared.api.ApiErrorCode;
import com.contactcore.shared.api.LocalizedApiException;
import java.util.Objects;

public class IamAccessDeniedException extends RuntimeException implements LocalizedApiException {
    private final IamPrincipalRef principal;
    private final IamAction action;
    private final IamResource resource;
    private final IamDecision decision;

    public IamAccessDeniedException(IamPrincipalRef principal, IamAction action, IamResource resource, IamDecision decision) {
        super(decision == null ? "Access denied." : decision.message());
        this.principal = Objects.requireNonNull(principal, "principal must not be null");
        this.action = Objects.requireNonNull(action, "action must not be null");
        this.resource = Objects.requireNonNull(resource, "resource must not be null");
        this.decision = Objects.requireNonNull(decision, "decision must not be null");
    }

    public IamPrincipalRef principal() {
        return principal;
    }

    public IamAction action() {
        return action;
    }

    public IamResource resource() {
        return resource;
    }

    public IamDecision decision() {
        return decision;
    }

    @Override
    public ApiErrorCode errorCode() {
        return ApiErrorCode.IAM_ACCESS_DENIED;
    }
}
