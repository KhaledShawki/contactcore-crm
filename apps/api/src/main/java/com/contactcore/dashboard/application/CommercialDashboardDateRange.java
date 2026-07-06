// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.application;

import com.contactcore.shared.api.InvalidRequestException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record CommercialDashboardDateRange(
        LocalDate from,
        LocalDate to
) {
    private static final long MAX_RANGE_DAYS = 731;

    public CommercialDashboardDateRange {
        if (from == null) {
            throw new InvalidRequestException("from must not be null");
        }
        if (to == null) {
            throw new InvalidRequestException("to must not be null");
        }
        if (from.isAfter(to)) {
            throw new InvalidRequestException("from must not be after to");
        }
        if (ChronoUnit.DAYS.between(from, to) > MAX_RANGE_DAYS) {
            throw new InvalidRequestException("Dashboard date range must not exceed 24 months.");
        }
    }

    public static CommercialDashboardDateRange currentYear(Clock clock) {
        LocalDate today = LocalDate.now(clock);
        return new CommercialDashboardDateRange(LocalDate.of(today.getYear(), 1, 1), today);
    }
}
