// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.model;

public record CrmBusinessPartnerSearchCriteria(
        String query,
        CrmBusinessPartnerType type,
        int page,
        int size,
        String sort
) {
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    public CrmBusinessPartnerSearchCriteria {
        query = query == null ? "" : query.trim();
        page = Math.max(0, page);
        size = Math.max(1, Math.min(size <= 0 ? DEFAULT_SIZE : size, MAX_SIZE));
        sort = sort == null || sort.isBlank() ? "code_asc" : sort.trim();
    }
}
