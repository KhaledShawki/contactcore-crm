// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.analytics.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AnalyticsMathTest {
    @Test
    void percentageReturnsZeroWhenTotalIsZero() {
        assertThat(AnalyticsMath.percentage(4, 0)).isZero();
    }

    @Test
    void percentageRoundsToOneDecimalPlace() {
        assertThat(AnalyticsMath.percentage(1, 3)).isEqualTo(33.3);
    }

    @Test
    void percentageDoesNotReturnNegativeRates() {
        assertThat(AnalyticsMath.percentage(-1, 10)).isZero();
    }
}
