// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.security;

import com.contactcore.commercial.domain.CommercialDocumentStatus;
import com.contactcore.commercial.domain.CommercialDocumentType;
import com.contactcore.commercial.domain.CommercialSourceSystem;
import com.contactcore.iam.evaluation.IamRequestContext;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public record CommercialDocumentAuthorizationContext(
        Long businessPartnerId,
        CommercialDocumentType type,
        CommercialDocumentStatus status,
        CommercialSourceSystem sourceSystem,
        LocalDate fromDate,
        LocalDate toDate
) {
    public IamRequestContext toIamContext() {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "businessPartnerId", businessPartnerId);
        put(values, "documentType", type == null ? null : type.name());
        put(values, "documentStatus", status == null ? null : status.name());
        put(values, "sourceSystem", sourceSystem == null ? null : sourceSystem.name());
        put(values, "fromDate", fromDate == null ? null : fromDate.toString());
        put(values, "toDate", toDate == null ? null : toDate.toString());
        return new IamRequestContext(values);
    }

    private static void put(Map<String, Object> values, String key, Object value) {
        if (value != null) {
            values.put(key, value);
        }
    }
}
