// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.commercial.domain.CommercialDocumentRepository;
import com.contactcore.commercial.domain.CommercialDocumentStatus;
import com.contactcore.commercial.domain.CommercialDocumentType;
import com.contactcore.crm.domain.BusinessPartner;
import com.contactcore.crm.domain.BusinessPartnerRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;

class BusinessPartnerSalesActivityServiceTest {
    private final BusinessPartnerRepository businessPartners = mock(BusinessPartnerRepository.class);
    private final CommercialDocumentRepository documents = mock(CommercialDocumentRepository.class);
    private final BusinessPartnerSalesActivityService service = new BusinessPartnerSalesActivityService(businessPartners, documents);

    @Test
    void countsOnlyClearlyOpenStatusesInSalesActivitySummary() {
        when(businessPartners.findActiveById(1L)).thenReturn(Optional.of(new BusinessPartner(null, null, "C1000", "Acme AG")));
        when(documents.findRecentActiveByBusinessPartnerId(eq(1L), any(Pageable.class))).thenReturn(List.of());
        when(documents.totalAmountsByCurrency(1L)).thenReturn(List.of());
        when(documents.latestDocumentDate(1L)).thenReturn(Optional.empty());

        service.get(1L, 10);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<CommercialDocumentStatus>> statuses = ArgumentCaptor.forClass(Collection.class);
        verify(documents).countByBusinessPartner_IdAndArchivedAtIsNullAndStatusIn(eq(1L), statuses.capture());
        assertThat(statuses.getValue())
                .containsExactly(CommercialDocumentStatus.OPEN, CommercialDocumentStatus.PARTIALLY_FULFILLED)
                .doesNotContain(CommercialDocumentStatus.UNKNOWN);

        verify(documents).countByBusinessPartner_IdAndArchivedAtIsNullAndTypeAndStatusIn(
                eq(1L),
                eq(CommercialDocumentType.SALES_QUOTATION),
                eq(List.of(CommercialDocumentStatus.OPEN, CommercialDocumentStatus.PARTIALLY_FULFILLED))
        );
        verify(documents).countByBusinessPartner_IdAndArchivedAtIsNullAndTypeAndStatusIn(
                eq(1L),
                eq(CommercialDocumentType.SALES_ORDER),
                eq(List.of(CommercialDocumentStatus.OPEN, CommercialDocumentStatus.PARTIALLY_FULFILLED))
        );
    }
}
