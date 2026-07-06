// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.api;

import java.time.LocalDate;
import java.util.List;

public record BusinessPartnerCommercialSummaryResponse(
        long documentCount,
        long openDocumentCount,
        long openQuotationCount,
        long openSalesOrderCount,
        long deliveryNoteCount,
        long customerInvoiceCount,
        LocalDate latestDocumentDate,
        List<CommercialAmountByCurrencyResponse> totalsByCurrency
) {}
