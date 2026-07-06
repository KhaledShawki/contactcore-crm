// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class CommercialDomainValidationTest {
    @Test
    void documentNormalizesCurrencyAndRejectsInvalidCurrency() {
        CommercialDocument document = new CommercialDocument(
                CommercialSourceSystem.SAP_B1,
                "default",
                "123",
                "100001",
                CommercialDocumentType.CUSTOMER_INVOICE,
                LocalDate.parse("2026-07-04"),
                "chf"
        );

        assertThat(document.getCurrency()).isEqualTo("CHF");
        assertThatThrownBy(() -> new CommercialDocument(
                CommercialSourceSystem.SAP_B1,
                "default",
                "124",
                "100002",
                CommercialDocumentType.CUSTOMER_INVOICE,
                LocalDate.parse("2026-07-04"),
                "EURO"
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("currency");
    }

    @Test
    void documentRejectsNegativeAmounts() {
        CommercialDocument document = new CommercialDocument(
                CommercialSourceSystem.SAP_B1,
                "default",
                "123",
                "100001",
                CommercialDocumentType.SALES_ORDER,
                LocalDate.parse("2026-07-04"),
                "EUR"
        );

        assertThatThrownBy(() -> document.refreshHeader(
                CommercialDocumentStatus.OPEN,
                "Open",
                null,
                "C0001",
                "C0001",
                "Acme AG",
                null,
                null,
                BigDecimal.valueOf(-1),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("subtotalAmount");
    }

    @Test
    void itemRequiresStableSourceIdentity() {
        assertThatThrownBy(() -> new Item(CommercialSourceSystem.SAP_B1, "default", " ", "A1000", "Item"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("externalId");
    }

    @Test
    void lineRejectsInvalidIdentity() {
        assertThatThrownBy(() -> new CommercialDocumentLine("line-1", -1, "CHF"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("lineNumber");
    }
}
