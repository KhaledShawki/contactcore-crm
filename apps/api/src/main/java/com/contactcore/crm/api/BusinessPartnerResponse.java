// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.api;

import java.time.Instant;
import java.util.List;

public record BusinessPartnerResponse(
        Long id,
        Long version,
        Instant createdAt,
        Instant updatedAt,
        String kind,
        String statusCode,
        String statusName,
        String sourceCode,
        String code,
        String name,
        String primaryEmail,
        String primaryPhone,
        String website,
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String countryCode,
        String notes,
        List<ContactMethodResponse> contactMethods,
        List<ContactPersonResponse> contactPersons
) {}
