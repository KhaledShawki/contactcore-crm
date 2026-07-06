// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.application;

import com.contactcore.shared.api.InvalidRequestException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Locale;

public record CommercialDashboardQuery(
        CommercialDashboardDateRange dateRange,
        String currency,
        int limit,
        CommercialDashboardGroupBy groupBy
) {
    public static final int DEFAULT_LIMIT = 10;
    public static final int MAX_LIMIT = 50;

    public CommercialDashboardQuery {
        if (dateRange == null) {
            throw new InvalidRequestException("dateRange must not be null");
        }
        currency = normalizeCurrency(currency);
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new InvalidRequestException("limit must be between 1 and " + MAX_LIMIT);
        }
        groupBy = groupBy == null ? CommercialDashboardGroupBy.MONTH : groupBy;
    }

    public static CommercialDashboardQuery of(LocalDate from,
                                              LocalDate to,
                                              String currency,
                                              Integer limit,
                                              String groupBy,
                                              Clock clock) {
        CommercialDashboardDateRange range = from == null && to == null
                ? CommercialDashboardDateRange.currentYear(clock)
                : new CommercialDashboardDateRange(
                        from == null ? CommercialDashboardDateRange.currentYear(clock).from() : from,
                        to == null ? LocalDate.now(clock) : to
                );
        int safeLimit = limit == null ? DEFAULT_LIMIT : limit;
        return new CommercialDashboardQuery(range, currency, safeLimit, CommercialDashboardGroupBy.from(groupBy));
    }

    private static String normalizeCurrency(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("^[A-Z]{3}$")) {
            throw new InvalidRequestException("currency must be a three-letter ISO code.");
        }
        return normalized;
    }
}
