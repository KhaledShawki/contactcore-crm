// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import com.contactcore.commercial.domain.CommercialDocumentStatus;
import com.contactcore.commercial.domain.CommercialDocumentType;
import com.contactcore.commercial.domain.CommercialSourceSystem;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CommercialDocumentProjectionCommand(
        CommercialSourceSystem sourceSystem,
        String sourceTenantId,
        String externalId,
        String externalNumber,
        CommercialDocumentType type,
        CommercialDocumentStatus status,
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
