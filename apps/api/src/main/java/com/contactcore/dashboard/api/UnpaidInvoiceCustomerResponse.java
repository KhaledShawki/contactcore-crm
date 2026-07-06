// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.api;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UnpaidInvoiceCustomerResponse(
        String businessPartnerCode,
        String businessPartnerName,
        BigDecimal openAmount,
        String currency,
        int invoiceCount,
        LocalDate oldestDueDate,
        long maxOverdueDays
) {}
