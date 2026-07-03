// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ConnectorBusinessPartnerTransactionSummary(
        long salesOrderCount,
        long purchaseOrderCount,
        long invoiceCount,
        long openInvoiceCount,
        BigDecimal totalSalesAmount,
        BigDecimal totalPurchaseAmount,
        BigDecimal totalOpenAmount,
        LocalDate firstTransactionDate,
        LocalDate lastTransactionDate
) {
    public static ConnectorBusinessPartnerTransactionSummary empty() {
        return new ConnectorBusinessPartnerTransactionSummary(0, 0, 0, 0, null, null, null, null, null);
    }
}
