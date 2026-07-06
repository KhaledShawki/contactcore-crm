// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import com.contactcore.commercial.domain.CommercialSourceSystem;
import java.time.Instant;

public record CommercialItemProjectionCommand(
        CommercialSourceSystem sourceSystem,
        String sourceTenantId,
        String externalId,
        String itemCode,
        String name,
        String description,
        String itemGroup,
        String unitOfMeasure,
        boolean active,
        Instant lastSyncedAt
) {}
