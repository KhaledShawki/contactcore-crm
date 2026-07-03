// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.odata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SapB1ODataPathBuilderTest {
    private final SapB1ODataPathBuilder pathBuilder = new SapB1ODataPathBuilder();

    @Test
    void buildsEntityPathWithEscapedKey() {
        assertThat(pathBuilder.entity("BusinessPartners", "C'100")
                .select("CardCode", "CardName")
                .build())
                .isEqualTo("BusinessPartners('C''100')?$select=CardCode,CardName");
    }

    @Test
    void buildsEncodedSearchQuery() {
        String path = pathBuilder.entitySet("BusinessPartners")
                .select("CardCode", "CardName")
                .filter(SapB1ODataFilter.and(
                        SapB1ODataFilter.eq("CardType", "C"),
                        SapB1ODataFilter.or(
                                SapB1ODataFilter.contains("CardName", "Meyer & Sons"),
                                SapB1ODataFilter.contains("CardCode", "Meyer & Sons")
                        )))
                .orderBy("CardName asc")
                .skip(50)
                .top(25)
                .build();

        assertThat(path).startsWith("BusinessPartners?$select=CardCode,CardName");
        assertThat(path).contains("CardType%20eq%20%27C%27");
        assertThat(path).contains("contains%28CardName%2C%27Meyer%20%26%20Sons%27%29");
        assertThat(path).contains("&$skip=50");
        assertThat(path).contains("&$top=25");
        assertThat(path).contains("&$orderby=CardName%20asc");
    }
}
