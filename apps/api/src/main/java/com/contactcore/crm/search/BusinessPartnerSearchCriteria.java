// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.search;

public record BusinessPartnerSearchCriteria(
        String kindCode,
        String query,
        String queryPattern,
        int page,
        int size,
        long offset,
        BusinessPartnerSearchSort sort
) {
    public boolean hasQuery() {
        return query != null && !query.isBlank();
    }
}
