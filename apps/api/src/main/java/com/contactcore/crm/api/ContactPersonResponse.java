// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.api;

import java.time.Instant;

public record ContactPersonResponse(
        Long id,
        Long businessPartnerId,
        Long version,
        Instant createdAt,
        Instant updatedAt,
        String firstName,
        String lastName,
        String displayName,
        String roleTitle,
        String email,
        String phone,
        String mobile,
        String department,
        boolean primaryContact,
        String notes
) {}
