// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.Arrays;

public enum IamConditionOperator {
    STRING_EQUALS("StringEquals"),
    STRING_NOT_EQUALS("StringNotEquals"),
    STRING_LIKE("StringLike"),
    BOOL("Bool"),
    DATE_GREATER_THAN("DateGreaterThan"),
    DATE_LESS_THAN("DateLessThan"),
    FOR_ANY_VALUE_STRING_EQUALS("ForAnyValue:StringEquals"),
    FOR_ALL_VALUES_STRING_EQUALS("ForAllValues:StringEquals");

    private final String policyName;

    IamConditionOperator(String policyName) {
        this.policyName = policyName;
    }

    public String policyName() {
        return policyName;
    }

    public static IamConditionOperator fromPolicyName(String value) {
        return Arrays.stream(values())
                .filter(operator -> operator.policyName.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported IAM condition operator: " + value));
    }
}
