// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

public interface AccessEvaluator {
    IamDecision evaluate(IamEvaluationRequest request);
}
