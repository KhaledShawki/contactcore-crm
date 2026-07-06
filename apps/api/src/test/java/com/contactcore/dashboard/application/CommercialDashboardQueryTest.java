// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.contactcore.shared.api.InvalidRequestException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class CommercialDashboardQueryTest {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-06T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void defaultsToCurrentYearAndDefaultLimit() {
        CommercialDashboardQuery query = CommercialDashboardQuery.of(null, null, null, null, null, CLOCK);

        assertThat(query.dateRange().from()).isEqualTo(LocalDate.parse("2026-01-01"));
        assertThat(query.dateRange().to()).isEqualTo(LocalDate.parse("2026-07-06"));
        assertThat(query.limit()).isEqualTo(10);
        assertThat(query.groupBy()).isEqualTo(CommercialDashboardGroupBy.MONTH);
    }

    @Test
    void normalizesCurrency() {
        CommercialDashboardQuery query = CommercialDashboardQuery.of(
                LocalDate.parse("2026-01-01"),
                LocalDate.parse("2026-01-31"),
                " chf ",
                25,
                "month",
                CLOCK
        );

        assertThat(query.currency()).isEqualTo("CHF");
        assertThat(query.limit()).isEqualTo(25);
    }

    @Test
    void rejectsInvalidRangeLimitAndCurrency() {
        assertThatThrownBy(() -> CommercialDashboardQuery.of(LocalDate.parse("2026-02-01"), LocalDate.parse("2026-01-01"), null, null, null, CLOCK))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("from");
        assertThatThrownBy(() -> CommercialDashboardQuery.of(LocalDate.parse("2026-01-01"), LocalDate.parse("2026-01-31"), "Swiss Franc", null, null, CLOCK))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("currency");
        assertThatThrownBy(() -> CommercialDashboardQuery.of(LocalDate.parse("2026-01-01"), LocalDate.parse("2026-01-31"), null, 99, null, CLOCK))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("limit");
    }
}
