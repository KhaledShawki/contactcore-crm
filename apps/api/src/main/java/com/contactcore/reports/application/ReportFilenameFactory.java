// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.application;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class ReportFilenameFactory {
    private final Clock clock;

    public ReportFilenameFactory() {
        this(Clock.systemUTC());
    }

    ReportFilenameFactory(Clock clock) {
        this.clock = clock;
    }

    public String xlsx(String baseName) {
        String safeBaseName = sanitizeBaseName(baseName);
        LocalDate today = LocalDate.now(clock.withZone(ZoneOffset.UTC));
        return "%s-%s.xlsx".formatted(safeBaseName, today);
    }

    private String sanitizeBaseName(String value) {
        String normalized = value == null ? "contactcore-report" : value.trim().toLowerCase(Locale.ROOT);
        normalized = normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+|-+$)", "");
        return normalized.isBlank() ? "contactcore-report" : normalized;
    }
}
