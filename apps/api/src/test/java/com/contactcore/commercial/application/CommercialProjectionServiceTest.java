// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.contactcore.commercial.domain.CommercialDocument;
import com.contactcore.commercial.domain.CommercialDocumentRepository;
import com.contactcore.commercial.domain.CommercialDocumentStatus;
import com.contactcore.commercial.domain.CommercialDocumentType;
import com.contactcore.commercial.domain.CommercialSourceSystem;
import com.contactcore.commercial.domain.ItemRepository;
import com.contactcore.crm.domain.BusinessPartnerRepository;
import com.contactcore.shared.api.InvalidRequestException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CommercialProjectionServiceTest {
    private final ItemRepository items = mock(ItemRepository.class);
    private final CommercialDocumentRepository documents = mock(CommercialDocumentRepository.class);
    private final BusinessPartnerRepository businessPartners = mock(BusinessPartnerRepository.class);
    private final CommercialProjectionService service = new CommercialProjectionService(items, documents, businessPartners);

    @Test
    void rejectsDuplicateSourceLineIdsBeforePersistingDocument() {
        CommercialDocumentProjectionCommand document = documentCommand();
        CommercialDocumentLineProjectionCommand line = line("1", 0);

        assertThatThrownBy(() -> service.projectDocument(document, List.of(line, line("1", 1))))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Duplicate sourceLineId");
    }

    @Test
    void rejectsDuplicateLineNumbersBeforePersistingDocument() {
        CommercialDocumentProjectionCommand document = documentCommand();

        assertThatThrownBy(() -> service.projectDocument(document, List.of(line("1", 0), line("2", 0))))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Duplicate lineNumber");
    }

    @Test
    void rejectsNegativeLineNumbersBeforePersistingDocument() {
        CommercialDocumentProjectionCommand document = documentCommand();

        assertThatThrownBy(() -> service.projectDocument(document, List.of(line("1", -1))))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("lineNumber");
    }

    @Test
    void createsNewDocumentWhenSourceIdentityDoesNotExist() {
        when(documents.findActiveBySourceIdentity(CommercialSourceSystem.SAP_B1, "default", "doc-1"))
                .thenReturn(Optional.empty());
        when(documents.save(any(CommercialDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.projectDocument(documentCommand(), List.of(line("1", 0)));
    }

    private CommercialDocumentProjectionCommand documentCommand() {
        return new CommercialDocumentProjectionCommand(
                CommercialSourceSystem.SAP_B1,
                "default",
                "doc-1",
                "100001",
                CommercialDocumentType.SALES_ORDER,
                CommercialDocumentStatus.OPEN,
                "Open",
                null,
                "C1000",
                "C1000",
                "Acme AG",
                LocalDate.parse("2026-07-04"),
                null,
                null,
                "CHF",
                BigDecimal.TEN,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.TEN,
                BigDecimal.TEN,
                null
        );
    }

    private CommercialDocumentLineProjectionCommand line(String sourceLineId, Integer lineNumber) {
        return new CommercialDocumentLineProjectionCommand(
                sourceLineId,
                lineNumber,
                null,
                "item-1",
                "A1000",
                "Item",
                "Item",
                BigDecimal.ONE,
                BigDecimal.ONE,
                "EA",
                BigDecimal.TEN,
                BigDecimal.ZERO,
                null,
                BigDecimal.TEN,
                "CHF",
                null
        );
    }
}
