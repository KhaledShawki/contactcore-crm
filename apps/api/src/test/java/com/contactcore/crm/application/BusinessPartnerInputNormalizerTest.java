// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.crm.api.BusinessPartnerWriteRequest;
import org.junit.jupiter.api.Test;

class BusinessPartnerInputNormalizerTest {
    @Test
    void normalizesCodesEmailAndBlankOptionalFieldsBeforePersistence() {
        BusinessPartnerWriteRequest request = new BusinessPartnerWriteRequest(
                " customer ",
                " active ",
                " c-100 ",
                "  Acme GmbH  ",
                " Sales@Acme.DE ",
                "  +49 123  ",
                "  ",
                " referral ",
                "  Main Street 1 ",
                " ",
                " Berlin ",
                " 10115 ",
                " de ",
                "  Important account  "
        );

        NormalizedBusinessPartnerInput normalized = BusinessPartnerInputNormalizer.normalize(request);

        assertThat(normalized.kind()).isEqualTo("CUSTOMER");
        assertThat(normalized.statusCode()).isEqualTo("ACTIVE");
        assertThat(normalized.code()).isEqualTo("C-100");
        assertThat(normalized.name()).isEqualTo("Acme GmbH");
        assertThat(normalized.primaryEmail()).isEqualTo("sales@acme.de");
        assertThat(normalized.website()).isNull();
        assertThat(normalized.sourceCode()).isEqualTo("REFERRAL");
        assertThat(normalized.addressLine2()).isNull();
        assertThat(normalized.countryCode()).isEqualTo("DE");
        assertThat(normalized.notes()).isEqualTo("Important account");
    }
}
