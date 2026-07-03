// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.odata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SapB1ODataEscaperTest {
    @Test
    void escapesSingleQuotesInsideStringLiterals() {
        assertThat(SapB1ODataEscaper.stringLiteral("C'100")).isEqualTo("C''100");
    }

    @Test
    void encodesODataQueryValuesWithReadableSpaces() {
        assertThat(SapB1ODataEscaper.encodedQueryValue("CardName eq 'Meyer & Sons'"))
                .isEqualTo("CardName%20eq%20%27Meyer%20%26%20Sons%27");
    }
}
