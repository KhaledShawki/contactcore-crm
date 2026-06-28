// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.api;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class ContactPersonWriteRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsValidContactPersonRequest() {
        var request = new ContactPersonWriteRequest(
                "Sara", "Fischer", "Procurement Contact", "sara@example.test", "+49 7623 200050",
                "+49 171 3000050", "Purchasing", true, "Primary contact."
        );

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsMissingNamesAndMalformedCommunicationValues() {
        var request = new ContactPersonWriteRequest(
                " ", "", "Procurement Contact", "invalid-email", "call me", "mobile", null, false, null
        );

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("firstName", "lastName", "email", "phone", "mobile");
    }

    @Test
    void allowsBlankOptionalPhoneFieldsBeforeServiceNormalization() {
        var request = new ContactPersonWriteRequest(
                "Sara", "Fischer", null, "sara@example.test", "", "", null, false, null
        );

        assertThat(validator.validate(request)).isEmpty();
    }
}
