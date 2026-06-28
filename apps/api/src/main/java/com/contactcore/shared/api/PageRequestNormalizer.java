// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.api;

public final class PageRequestNormalizer {
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    private PageRequestNormalizer() {}

    public static int page(int page) {
        return Math.max(page, 0);
    }

    public static int size(int size) {
        return Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    }

    public static String query(String query) {
        return query == null || query.isBlank() ? "" : query.trim();
    }
}
