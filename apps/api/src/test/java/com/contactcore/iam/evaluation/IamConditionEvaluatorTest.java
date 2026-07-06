// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.iam.domain.IamConditionBlock;
import com.contactcore.iam.domain.IamConditionOperator;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class IamConditionEvaluatorTest {
    private final IamConditionEvaluator evaluator = new IamConditionEvaluator(new IamMatcher());

    @Test
    void stringNotEqualsFailsClosedWhenContextKeyIsMissing() {
        IamConditionBlock condition = new IamConditionBlock(Map.of(
                IamConditionOperator.STRING_NOT_EQUALS,
                Map.of("sourceSystem", List.of("SAP_B1"))
        ));

        assertThat(evaluator.matches(condition, IamRequestContext.empty())).isFalse();
    }

    @Test
    void invalidBooleanExpectedValuesDoNotMatchFalseAccidentally() {
        IamConditionBlock condition = new IamConditionBlock(Map.of(
                IamConditionOperator.BOOL,
                Map.of("trusted", List.of("not-a-boolean"))
        ));

        assertThat(evaluator.matches(condition, new IamRequestContext(Map.of("trusted", false)))).isFalse();
    }

    @Test
    void invalidDateExpectedValuesFailClosed() {
        IamConditionBlock condition = new IamConditionBlock(Map.of(
                IamConditionOperator.DATE_GREATER_THAN,
                Map.of("requestTime", List.of("not-a-date"))
        ));

        assertThat(evaluator.matches(condition, new IamRequestContext(Map.of("requestTime", "2026-07-05T12:00:00Z")))).isFalse();
    }

    @Test
    void invalidDateContextValuesFailClosed() {
        IamConditionBlock condition = new IamConditionBlock(Map.of(
                IamConditionOperator.DATE_LESS_THAN,
                Map.of("requestTime", List.of("2026-07-05T12:00:00Z"))
        ));

        assertThat(evaluator.matches(condition, new IamRequestContext(Map.of("requestTime", "not-a-date")))).isFalse();
    }
}
