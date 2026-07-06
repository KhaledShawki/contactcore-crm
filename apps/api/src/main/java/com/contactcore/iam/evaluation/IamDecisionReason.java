// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

public enum IamDecisionReason {
    ALLOW,
    EXPLICIT_DENY,
    IMPLICIT_DENY,
    MISSING_ALLOW,
    UNSUPPORTED_ACTION,
    PERMISSION_BOUNDARY_DENY,
    SESSION_POLICY_DENY,
    CONDITION_MISMATCH
}
