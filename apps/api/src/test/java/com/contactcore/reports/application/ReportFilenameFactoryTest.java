// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ReportFilenameFactoryTest {
    @Test
    void buildsSafeDatedXlsxFilename() {
        ReportFilenameFactory factory = new ReportFilenameFactory(Clock.fixed(Instant.parse("2026-06-26T10:15:00Z"), ZoneOffset.UTC));

        assertThat(factory.xlsx("ContactCore Customers / Export"))
                .isEqualTo("contactcore-customers-export-2026-06-26.xlsx");
    }
}
