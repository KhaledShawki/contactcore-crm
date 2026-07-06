// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CommercialDocumentSummaryResponse(
        Long id,
        Long version,
        Instant createdAt,
        Instant updatedAt,
        String sourceSystem,
        String sourceTenantId,
        String externalId,
        String externalNumber,
        String type,
        String status,
        String sourceStatus,
        Long businessPartnerId,
        String businessPartnerExternalId,
        String businessPartnerCodeSnapshot,
        String businessPartnerNameSnapshot,
        LocalDate documentDate,
        LocalDate dueDate,
        LocalDate deliveryDate,
        String currency,
        BigDecimal subtotalAmount,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        BigDecimal openAmount,
        Instant lastSyncedAt
) {}
