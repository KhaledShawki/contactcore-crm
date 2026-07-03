// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.odata;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class SapB1ODataFilter {
    private final String expression;

    private SapB1ODataFilter(String expression) {
        this.expression = expression;
    }

    public static SapB1ODataFilter raw(String expression) {
        return new SapB1ODataFilter(expression);
    }

    public static SapB1ODataFilter eq(String field, String value) {
        return new SapB1ODataFilter(field + " eq '" + SapB1ODataEscaper.stringLiteral(value) + "'");
    }

    public static SapB1ODataFilter contains(String field, String value) {
        return new SapB1ODataFilter("contains(" + field + ",'" + SapB1ODataEscaper.stringLiteral(value) + "')");
    }

    public static SapB1ODataFilter and(SapB1ODataFilter... filters) {
        return join(" and ", filters);
    }

    public static SapB1ODataFilter or(SapB1ODataFilter... filters) {
        String joined = Arrays.stream(filters)
                .filter(filter -> filter != null && !filter.expression.isBlank())
                .map(SapB1ODataFilter::expression)
                .collect(Collectors.joining(" or "));
        return new SapB1ODataFilter(joined.isBlank() ? "" : "(" + joined + ")");
    }

    public static SapB1ODataFilter all(List<SapB1ODataFilter> filters) {
        return join(" and ", filters.toArray(SapB1ODataFilter[]::new));
    }

    public String expression() {
        return expression;
    }

    public boolean isBlank() {
        return expression == null || expression.isBlank();
    }

    private static SapB1ODataFilter join(String separator, SapB1ODataFilter... filters) {
        String joined = Arrays.stream(filters)
                .filter(filter -> filter != null && !filter.expression.isBlank())
                .map(SapB1ODataFilter::expression)
                .collect(Collectors.joining(separator));
        return new SapB1ODataFilter(joined);
    }
}
