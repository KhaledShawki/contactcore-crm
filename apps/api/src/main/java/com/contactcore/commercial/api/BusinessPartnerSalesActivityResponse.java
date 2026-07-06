// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.api;

import java.util.List;

public record BusinessPartnerSalesActivityResponse(
        Long businessPartnerId,
        String businessPartnerCode,
        String businessPartnerName,
        BusinessPartnerCommercialSummaryResponse summary,
        List<CommercialDocumentSummaryResponse> recentDocuments
) {}
