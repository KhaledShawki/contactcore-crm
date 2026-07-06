// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.contactcore.commercial.domain.CommercialDocumentLineRepository;
import com.contactcore.commercial.domain.CommercialDocumentRepository;
import com.contactcore.shared.api.InvalidRequestException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class CommercialDocumentQueryServiceTest {
    private final CommercialDocumentRepository documents = mock(CommercialDocumentRepository.class);
    private final CommercialDocumentLineRepository lines = mock(CommercialDocumentLineRepository.class);
    private final CommercialDocumentQueryService service = new CommercialDocumentQueryService(documents, lines);

    @Test
    void rejectsInvalidDateRangeBeforeBuildingSpecification() {
        assertThatThrownBy(() -> service.search(
                null,
                null,
                null,
                null,
                LocalDate.parse("2026-07-05"),
                LocalDate.parse("2026-07-04"),
                null,
                0,
                20,
                null
        )).isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("fromDate");
    }
}
