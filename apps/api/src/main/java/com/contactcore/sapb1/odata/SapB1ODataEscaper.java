// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.odata;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class SapB1ODataEscaper {
    private SapB1ODataEscaper() {}

    public static String stringLiteral(String value) {
        return value == null ? "" : value.trim().replace("'", "''");
    }

    public static String encodedQueryValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
