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

import com.contactcore.commercial.application.ItemQueryService;
import com.contactcore.commercial.domain.CommercialSourceSystem;
import com.contactcore.commercial.security.CommercialAuthorizationGuard;
import com.contactcore.commercial.security.ItemAuthorizationContext;
import com.contactcore.iam.application.IamAccessDeniedException;
import com.contactcore.shared.api.PageResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class ItemControllerAuthorizationTest {
    private final ItemQueryService service = mock(ItemQueryService.class);
    private final CommercialAuthorizationGuard authorization = mock(CommercialAuthorizationGuard.class);
    private final ItemController controller = new ItemController(service, authorization);

    @Test
    void authorizesBeforeSearchingItems() {
        when(service.search(any(), any(), any(), anyInt(), anyInt(), any()))
                .thenReturn(new PageResponse<>(List.of(), 0, 20, 0, 0));

        controller.search(CommercialSourceSystem.SAP_B1, true, "item", 0, 20, "code_asc");

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireListItems(any(ItemAuthorizationContext.class));
        inOrder.verify(service).search(eq(CommercialSourceSystem.SAP_B1), eq(true), eq("item"), eq(0), eq(20), eq("code_asc"));
    }

    @Test
    void skipsSearchWhenAuthorizationFails() {
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        org.mockito.Mockito.doThrow(denied).when(authorization).requireListItems(any(ItemAuthorizationContext.class));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> controller.search(null, null, "", 0, 20, "updated_desc"))
                .isSameAs(denied);

        verify(service, never()).search(any(), any(), any(), anyInt(), anyInt(), any());
    }

    @Test
    void authorizesBeforeReadingItemDetail() {
        controller.get(10L);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireReadItem(10L);
        inOrder.verify(service).get(10L);
    }
}
