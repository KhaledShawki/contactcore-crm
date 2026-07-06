// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.api;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.dashboard.application.CommercialDashboardDateRange;
import com.contactcore.dashboard.application.CommercialDashboardGroupBy;
import com.contactcore.dashboard.application.CommercialDashboardQuery;
import com.contactcore.dashboard.application.CommercialDashboardService;
import com.contactcore.dashboard.security.DashboardAuthorizationGuard;
import com.contactcore.iam.application.IamAccessDeniedException;
import com.contactcore.security.application.UserPrincipal;
import com.contactcore.security.domain.AppUser;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

class CommercialDashboardControllerAuthorizationTest {
    private final CommercialDashboardService service = mock(CommercialDashboardService.class);
    private final DashboardAuthorizationGuard authorization = mock(DashboardAuthorizationGuard.class);
    private final CommercialDashboardController controller = new CommercialDashboardController(service, authorization);
    private final CommercialDashboardQuery query = new CommercialDashboardQuery(
            new CommercialDashboardDateRange(LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31")),
            "CHF",
            10,
            CommercialDashboardGroupBy.MONTH
    );

    @Test
    void summaryRequiresFinancialPermissionBeforeServiceCall() {
        UserPrincipal principal = principal();
        when(service.query(null, null, "CHF", null, null)).thenReturn(query);

        controller.summary(principal, null, null, "CHF");

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(service).query(null, null, "CHF", null, null);
        inOrder.verify(authorization).requireCommercialFinancials("summary", query);
        inOrder.verify(service).summary(42L, query);
    }

    @Test
    void topSellingItemsRequiresDashboardPermissionBeforeServiceCall() {
        UserPrincipal principal = principal();
        when(service.query(null, null, "CHF", 5, null)).thenReturn(query);
        when(service.topSellingItems(42L, query)).thenReturn(List.of());

        controller.topSellingItems(principal, null, null, "CHF", 5);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(service).query(null, null, "CHF", 5, null);
        inOrder.verify(authorization).requireCommercialDashboard("topSellingItems", query);
        inOrder.verify(service).topSellingItems(42L, query);
    }

    @Test
    void skipsServiceWhenAuthorizationFails() {
        UserPrincipal principal = principal();
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        when(service.query(null, null, "CHF", null, null)).thenReturn(query);
        org.mockito.Mockito.doThrow(denied).when(authorization).requireCommercialFinancials("summary", query);

        assertThatThrownBy(() -> controller.summary(principal, null, null, "CHF"))
                .isSameAs(denied);

        verify(service, never()).summary(anyLong(), any(CommercialDashboardQuery.class));
    }

    private static UserPrincipal principal() {
        AppUser user = new AppUser("khaled", "khaled@example.com", "hash");
        ReflectionTestUtils.setField(user, "id", 42L);
        return UserPrincipal.from(user);
    }
}
