// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.sap;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.dashboard.application.CommercialDashboardDocumentType;
import com.contactcore.sapb1.resource.dashboard.SapB1DashboardDocumentDto;
import com.contactcore.sapb1.resource.dashboard.SapB1DashboardDocumentLineDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class SapDashboardMapperTest {
    private final SapDashboardMapper mapper = new SapDashboardMapper();

    @Test
    void mapsSapInvoiceToDashboardDocument() {
        SapB1DashboardDocumentDto dto = new SapB1DashboardDocumentDto(
                12,
                9001,
                "C1000",
                "Acme AG",
                LocalDate.parse("2026-02-01"),
                LocalDate.parse("2026-03-01"),
                new BigDecimal("1200"),
                new BigDecimal("200"),
                "CHF",
                "bost_Open",
                "tNO",
                List.of(new SapB1DashboardDocumentLineDto("I100", "Notebook", new BigDecimal("2"), new BigDecimal("1000")))
        );

        var document = mapper.toDocument(CommercialDashboardDocumentType.INVOICE, dto);

        assertThat(document.externalId()).isEqualTo("12");
        assertThat(document.businessPartnerCode()).isEqualTo("C1000");
        assertThat(document.open()).isTrue();
        assertThat(document.openAmount()).isEqualByComparingTo("1000");
        assertThat(document.lines()).hasSize(1);
        assertThat(document.lines().getFirst().itemCode()).isEqualTo("I100");
    }
}
