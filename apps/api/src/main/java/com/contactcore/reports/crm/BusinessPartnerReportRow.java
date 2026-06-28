// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.crm;

import java.time.Instant;

public record BusinessPartnerReportRow(
        String type,
        String code,
        String name,
        String status,
        String marketingSource,
        String primaryEmail,
        String primaryPhone,
        String website,
        String primaryContactPerson,
        Integer contactPersonCount,
        Integer documentCount,
        String city,
        String countryCode,
        Instant createdAt,
        Instant updatedAt,
        String notes
) {}
