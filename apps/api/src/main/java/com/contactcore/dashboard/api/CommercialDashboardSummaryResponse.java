// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.api;

import java.math.BigDecimal;

public record CommercialDashboardSummaryResponse(
        BigDecimal totalSales,
        BigDecimal openInvoiceAmount,
        BigDecimal overdueAmount,
        int unpaidInvoiceCount,
        int activeCustomerCount,
        int soldItemCount,
        String currency
) {}
