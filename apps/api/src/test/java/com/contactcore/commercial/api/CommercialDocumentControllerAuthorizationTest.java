// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.commercial.application.CommercialDocumentQueryService;
import com.contactcore.commercial.domain.CommercialDocumentStatus;
import com.contactcore.commercial.domain.CommercialDocumentType;
import com.contactcore.commercial.domain.CommercialSourceSystem;
import com.contactcore.commercial.security.CommercialAuthorizationGuard;
import com.contactcore.commercial.security.CommercialDocumentAuthorizationContext;
import com.contactcore.iam.application.IamAccessDeniedException;
import com.contactcore.shared.api.PageResponse;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class CommercialDocumentControllerAuthorizationTest {
    private final CommercialDocumentQueryService service = mock(CommercialDocumentQueryService.class);
    private final CommercialAuthorizationGuard authorization = mock(CommercialAuthorizationGuard.class);
    private final CommercialDocumentController controller = new CommercialDocumentController(service, authorization);

    @Test
    void authorizesBeforeSearchingDocuments() {
        when(service.search(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any()))
                .thenReturn(new PageResponse<>(List.of(), 0, 20, 0, 0));

        controller.search(
                1L,
                CommercialDocumentType.SALES_ORDER,
                CommercialDocumentStatus.OPEN,
                CommercialSourceSystem.SAP_B1,
                LocalDate.parse("2026-07-01"),
                LocalDate.parse("2026-07-31"),
                "100",
                0,
                20,
                "updated_desc"
        );

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireListDocuments(any(CommercialDocumentAuthorizationContext.class));
        inOrder.verify(service).search(
                eq(1L),
                eq(CommercialDocumentType.SALES_ORDER),
                eq(CommercialDocumentStatus.OPEN),
                eq(CommercialSourceSystem.SAP_B1),
                eq(LocalDate.parse("2026-07-01")),
                eq(LocalDate.parse("2026-07-31")),
                eq("100"),
                eq(0),
                eq(20),
                eq("updated_desc")
        );
    }

    @Test
    void skipsSearchWhenAuthorizationFails() {
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        org.mockito.Mockito.doThrow(denied).when(authorization).requireListDocuments(any(CommercialDocumentAuthorizationContext.class));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> controller.search(
                null,
                null,
                null,
                null,
                null,
                null,
                "",
                0,
                20,
                "updated_desc"
        )).isSameAs(denied);

        verify(service, never()).search(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any());
    }

    @Test
    void documentLinesUseDocumentReadAuthorization() {
        when(service.lines(99L)).thenReturn(List.of());

        controller.lines(99L);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireReadDocument(99L);
        inOrder.verify(service).lines(99L);
    }
}
