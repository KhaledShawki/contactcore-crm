// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import java.util.Locale;
import org.springframework.data.domain.Sort;

public enum ItemSort {
    UPDATED_DESC(Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id"))),
    CODE_ASC(Sort.by(Sort.Order.asc("itemCode"), Sort.Order.asc("id"))),
    NAME_ASC(Sort.by(Sort.Order.asc("name"), Sort.Order.asc("id")));

    private final Sort sort;

    ItemSort(Sort sort) {
        this.sort = sort;
    }

    public Sort sort() {
        return sort;
    }

    public static ItemSort from(String value) {
        if (value == null || value.isBlank()) {
            return UPDATED_DESC;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        for (ItemSort sort : values()) {
            if (sort.name().equals(normalized)) {
                return sort;
            }
        }
        return UPDATED_DESC;
    }
}
