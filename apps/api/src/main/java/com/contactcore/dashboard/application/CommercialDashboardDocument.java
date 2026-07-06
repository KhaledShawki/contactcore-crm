// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CommercialDashboardDocument(
        CommercialDashboardDocumentType type,
        String externalId,
        String businessPartnerCode,
        String businessPartnerName,
        LocalDate documentDate,
        LocalDate dueDate,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        String currency,
        boolean open,
        List<CommercialDashboardLine> lines
) {
    public CommercialDashboardDocument {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        externalId = normalize(externalId);
        businessPartnerCode = normalize(businessPartnerCode);
        businessPartnerName = normalize(businessPartnerName);
        totalAmount = totalAmount == null ? BigDecimal.ZERO : totalAmount;
        paidAmount = paidAmount == null ? BigDecimal.ZERO : paidAmount;
        currency = normalize(currency);
        lines = lines == null ? List.of() : List.copyOf(lines);
    }

    public BigDecimal openAmount() {
        BigDecimal result = totalAmount.subtract(paidAmount);
        return result.signum() < 0 ? BigDecimal.ZERO : result;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
