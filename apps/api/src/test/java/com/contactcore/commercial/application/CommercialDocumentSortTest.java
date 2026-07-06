// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CommercialDocumentSortTest {
    @Test
    void defaultsUnknownSortsToUpdatedDesc() {
        assertThat(CommercialDocumentSort.from(null)).isEqualTo(CommercialDocumentSort.UPDATED_DESC);
        assertThat(CommercialDocumentSort.from("unknown")).isEqualTo(CommercialDocumentSort.UPDATED_DESC);
    }

    @Test
    void acceptsSnakeCaseAndDashCaseSorts() {
        assertThat(CommercialDocumentSort.from("document_date_desc")).isEqualTo(CommercialDocumentSort.DOCUMENT_DATE_DESC);
        assertThat(CommercialDocumentSort.from("document-date-asc")).isEqualTo(CommercialDocumentSort.DOCUMENT_DATE_ASC);
    }
}
