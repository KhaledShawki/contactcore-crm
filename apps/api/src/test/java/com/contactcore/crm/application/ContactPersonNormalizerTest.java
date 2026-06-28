// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.contactcore.crm.api.ContactPersonWriteRequest;
import com.contactcore.shared.api.InvalidRequestException;
import org.junit.jupiter.api.Test;

class ContactPersonNormalizerTest {
    @Test
    void normalizesContactPersonInput() {
        var input = ContactPersonNormalizer.normalize(new ContactPersonWriteRequest(
                "  Sara ", " Meyer ", " CTO ", "SARA@EXAMPLE.COM ", " +49 123 ", null,
                " Engineering ", true, " Important contact "
        ));

        assertThat(input.firstName()).isEqualTo("Sara");
        assertThat(input.lastName()).isEqualTo("Meyer");
        assertThat(input.email()).isEqualTo("sara@example.com");
        assertThat(input.primaryContact()).isTrue();
    }

    @Test
    void rejectsContactWithoutCommunicationChannel() {
        assertThatThrownBy(() -> ContactPersonNormalizer.normalize(new ContactPersonWriteRequest(
                "Sara", "Meyer", null, null, " ", null, null, false, null
        ))).isInstanceOf(InvalidRequestException.class).hasMessageContaining("contact person needs at least one");
    }
}
