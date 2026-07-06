// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.contactcore.crm.domain.BusinessPartner;
import com.contactcore.shared.api.NotFoundException;
import com.contactcore.storage.domain.BusinessPartnerDocument;
import com.contactcore.storage.domain.BusinessPartnerDocumentRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class BusinessPartnerDocumentAccessServiceTest {
    private final BusinessPartnerDocumentRepository documents = mock(BusinessPartnerDocumentRepository.class);
    private final BusinessPartnerDocumentAccessService service = new BusinessPartnerDocumentAccessService(documents);

    @Test
    void returnsBusinessPartnerIdForActiveDocument() {
        BusinessPartnerDocument document = mock(BusinessPartnerDocument.class);
        BusinessPartner partner = mock(BusinessPartner.class);
        when(partner.getId()).thenReturn(42L);
        when(document.getBusinessPartner()).thenReturn(partner);
        when(documents.findActiveById(99L)).thenReturn(Optional.of(document));

        Long businessPartnerId = service.requireActiveBusinessPartnerId(99L);

        assertThat(businessPartnerId).isEqualTo(42L);
    }

    @Test
    void throwsNotFoundWhenDocumentDoesNotExist() {
        when(documents.findActiveById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.requireActiveBusinessPartnerId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Document not found: 99");
    }
}
