// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.search;

import java.util.Locale;
import java.util.Map;

public enum BusinessPartnerSearchSort {
    UPDATED_DESC("partner.updated_at desc, partner.id desc"),
    CREATED_DESC("partner.created_at desc, partner.id desc"),
    NAME_ASC("lower(partner.name) asc, partner.id asc"),
    CODE_ASC("lower(partner.code) asc, partner.id asc"),
    STATUS_ASC("lower(status.name) asc, lower(partner.name) asc, partner.id asc");

    private static final Map<String, BusinessPartnerSearchSort> SORTS = Map.of(
            "updated_desc", UPDATED_DESC,
            "created_desc", CREATED_DESC,
            "name_asc", NAME_ASC,
            "code_asc", CODE_ASC,
            "status_asc", STATUS_ASC
    );

    private final String orderBy;

    BusinessPartnerSearchSort(String orderBy) {
        this.orderBy = orderBy;
    }

    public String orderBy() {
        return orderBy;
    }

    public static BusinessPartnerSearchSort from(String sort) {
        if (sort == null || sort.isBlank()) {
            return UPDATED_DESC;
        }
        return SORTS.getOrDefault(sort.trim().toLowerCase(Locale.ROOT), UPDATED_DESC);
    }
}
