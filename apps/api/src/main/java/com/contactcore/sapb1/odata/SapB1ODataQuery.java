// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.odata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SapB1ODataQuery {
    private final String resourcePath;
    private final List<String> select = new ArrayList<>();
    private SapB1ODataFilter filter;
    private String orderBy;
    private Integer skip;
    private Integer top;

    private SapB1ODataQuery(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public static SapB1ODataQuery resource(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new IllegalArgumentException("resourcePath must not be blank.");
        }
        return new SapB1ODataQuery(resourcePath.trim());
    }

    public SapB1ODataQuery select(String... fields) {
        Arrays.stream(fields)
                .filter(field -> field != null && !field.isBlank())
                .map(String::trim)
                .forEach(select::add);
        return this;
    }

    public SapB1ODataQuery filter(SapB1ODataFilter filter) {
        this.filter = filter;
        return this;
    }

    public SapB1ODataQuery orderBy(String orderBy) {
        this.orderBy = orderBy == null || orderBy.isBlank() ? null : orderBy.trim();
        return this;
    }

    public SapB1ODataQuery skip(int skip) {
        this.skip = Math.max(0, skip);
        return this;
    }

    public SapB1ODataQuery top(int top) {
        this.top = Math.max(1, top);
        return this;
    }

    public String build() {
        List<String> options = new ArrayList<>();
        if (!select.isEmpty()) {
            options.add("$select=" + String.join(",", select));
        }
        if (filter != null && !filter.isBlank()) {
            options.add("$filter=" + SapB1ODataEscaper.encodedQueryValue(filter.expression()));
        }
        if (orderBy != null) {
            options.add("$orderby=" + SapB1ODataEscaper.encodedQueryValue(orderBy));
        }
        if (skip != null) {
            options.add("$skip=" + skip);
        }
        if (top != null) {
            options.add("$top=" + top);
        }
        return options.isEmpty() ? resourcePath : resourcePath + "?" + String.join("&", options);
    }
}
