// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.odata;

import org.springframework.stereotype.Component;

@Component
public final class SapB1ODataPathBuilder {
    public SapB1ODataQuery entitySet(String entitySet) {
        return SapB1ODataQuery.resource(entitySet);
    }

    public SapB1ODataQuery entity(String entitySet, String key) {
        return SapB1ODataQuery.resource(entitySet + "('" + SapB1ODataEscaper.stringLiteral(key) + "')");
    }
}
