// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import com.contactcore.commercial.domain.CommercialDocumentStatus;
import com.contactcore.commercial.domain.CommercialDocumentType;
import com.contactcore.commercial.domain.CommercialSourceSystem;
import java.time.LocalDate;

public record CommercialDocumentSearchCriteria(
        Long businessPartnerId,
        CommercialDocumentType type,
        CommercialDocumentStatus status,
        CommercialSourceSystem sourceSystem,
        LocalDate fromDate,
        LocalDate toDate,
        String query
) {
    public boolean hasQuery() {
        return query != null && !query.isBlank();
    }
}
