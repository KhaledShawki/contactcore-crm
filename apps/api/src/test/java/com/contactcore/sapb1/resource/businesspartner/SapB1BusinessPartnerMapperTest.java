// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.resource.businesspartner;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.connector.businesspartner.model.ConnectorAddressType;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerType;
import com.contactcore.connector.domain.CrmConnectorInstance;
import com.contactcore.connector.model.CrmConnectorEnvironment;
import com.contactcore.connector.model.CrmConnectorType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class SapB1BusinessPartnerMapperTest {
    private final SapB1BusinessPartnerMapper mapper = new SapB1BusinessPartnerMapper();

    @Test
    void mapsSummaryWithoutLeakingSapDtoShape() {
        var card = mapper.toSummary(instance(), dto());

        assertThat(card.identity().externalId()).isEqualTo("C100");
        assertThat(card.identity().connectorType()).isEqualTo("SAP_B1");
        assertThat(card.type()).isEqualTo(ConnectorBusinessPartnerType.CUSTOMER);
        assertThat(card.primaryPhone()).isEqualTo("+49 123");
        assertThat(card.balance()).isEqualByComparingTo("123.45");
        assertThat(card.status().active()).isTrue();
    }

    @Test
    void mapsDetailWithAddressesContactsAndFinancialProfile() {
        var detail = mapper.toDetail(instance(), dto());

        assertThat(detail.financialProfile().taxId()).isEqualTo("DE123");
        assertThat(detail.addresses()).hasSize(1);
        assertThat(detail.addresses().getFirst().type()).isEqualTo(ConnectorAddressType.BILLING);
        assertThat(detail.addresses().getFirst().defaultBilling()).isTrue();
        assertThat(detail.contactPersons()).hasSize(1);
        assertThat(detail.contactPersons().getFirst().email()).isEqualTo("max@example.com");
        assertThat(detail.contactPoints()).extracting("value").contains("info@example.com", "+49 123", "https://example.com");
    }

    private CrmConnectorInstance instance() {
        return new CrmConnectorInstance(CrmConnectorType.SAP_B1, "SAP Demo", CrmConnectorEnvironment.TEST, "{}");
    }

    private SapB1BusinessPartnerDto dto() {
        return new SapB1BusinessPartnerDto(
                "C100",
                "Example GmbH",
                "C",
                "+49 123",
                null,
                null,
                "info@example.com",
                "https://example.com",
                "EUR",
                new BigDecimal("123.45"),
                new BigDecimal("1000"),
                null,
                "DE123",
                "Y",
                "14",
                "tYES",
                "tNO",
                "Main",
                null,
                "Max Mustermann",
                7,
                3,
                2,
                List.of(new SapB1BusinessPartnerAddressDto("Main", "bo_BillTo", "Street 1", null, "12345", "Berlin", null, null, "DE", "C100", 0)),
                List.of(new SapB1BusinessPartnerContactEmployeeDto(10, "Max Mustermann", "Max", null, "Mustermann", null, "Buyer", "max@example.com", "+49 999", null, "Y"))
        );
    }
}
