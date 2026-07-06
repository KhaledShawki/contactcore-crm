// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.crm.security.BusinessPartnerAuthorizationContext;
import com.contactcore.crm.security.CrmAuthorizationGuard;
import com.contactcore.iam.application.IamAccessDeniedException;
import com.contactcore.reports.application.ReportExportService;
import com.contactcore.reports.application.ReportFile;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class ReportControllerAuthorizationTest {
    private final ReportExportService exportService = mock(ReportExportService.class);
    private final CrmAuthorizationGuard authorization = mock(CrmAuthorizationGuard.class);
    private final ReportController controller = new ReportController(exportService, authorization);

    @Test
    void businessPartnerExportRequiresCrmExportPermission() {
        when(exportService.businessPartners("CUSTOMER", "Acme", "updated_desc", 5000))
                .thenReturn(new ReportFile("report.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[] {1, 2, 3}));

        var response = controller.businessPartners("CUSTOMER", "Acme", "updated_desc", 5000);

        InOrder inOrder = inOrder(authorization, exportService);
        inOrder.verify(authorization).requireExportBusinessPartners(any(BusinessPartnerAuthorizationContext.class));
        inOrder.verify(exportService).businessPartners("CUSTOMER", "Acme", "updated_desc", 5000);
        assertThat(response.getBody()).containsExactly(1, 2, 3);
    }

    @Test
    void crmSummaryExportRequiresCrmExportPermission() {
        when(exportService.crmSummary())
                .thenReturn(new ReportFile("summary.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[] {4}));

        controller.crmSummary();

        InOrder inOrder = inOrder(authorization, exportService);
        inOrder.verify(authorization).requireExportBusinessPartners(any(BusinessPartnerAuthorizationContext.class));
        inOrder.verify(exportService).crmSummary();
    }

    @Test
    void marketingExportIsNotCoveredByCrmAuthorization() {
        when(exportService.marketingSources("", 5000))
                .thenReturn(new ReportFile("marketing.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[] {5}));

        controller.marketingSources("", 5000);

        verify(authorization, never()).requireExportBusinessPartners(any());
        verify(exportService).marketingSources("", 5000);
    }

    @Test
    void skipsExportWhenAuthorizationFails() {
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        org.mockito.Mockito.doThrow(denied).when(authorization).requireExportBusinessPartners(any(BusinessPartnerAuthorizationContext.class));

        assertThatThrownBy(() -> controller.crmSummary()).isSameAs(denied);

        verify(exportService, never()).crmSummary();
    }
}
