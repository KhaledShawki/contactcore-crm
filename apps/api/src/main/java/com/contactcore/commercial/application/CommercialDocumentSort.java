// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import java.util.Locale;
import org.springframework.data.domain.Sort;

public enum CommercialDocumentSort {
    UPDATED_DESC(Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id"))),
    DOCUMENT_DATE_DESC(Sort.by(Sort.Order.desc("documentDate"), Sort.Order.desc("id"))),
    DOCUMENT_DATE_ASC(Sort.by(Sort.Order.asc("documentDate"), Sort.Order.asc("id"))),
    DUE_DATE_ASC(Sort.by(Sort.Order.asc("dueDate").nullsLast(), Sort.Order.desc("id"))),
    TOTAL_DESC(Sort.by(Sort.Order.desc("totalAmount"), Sort.Order.desc("id"))),
    NUMBER_ASC(Sort.by(Sort.Order.asc("externalNumber"), Sort.Order.asc("id")));

    private final Sort sort;

    CommercialDocumentSort(Sort sort) {
        this.sort = sort;
    }

    public Sort sort() {
        return sort;
    }

    public static CommercialDocumentSort from(String value) {
        if (value == null || value.isBlank()) {
            return UPDATED_DESC;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        for (CommercialDocumentSort sort : values()) {
            if (sort.name().equals(normalized)) {
                return sort;
            }
        }
        return UPDATED_DESC;
    }
}
