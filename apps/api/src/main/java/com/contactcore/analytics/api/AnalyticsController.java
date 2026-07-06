// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.analytics.api;

import com.contactcore.analytics.application.AnalyticsService;
import com.contactcore.crm.security.BusinessPartnerAuthorizationContext;
import com.contactcore.crm.security.CrmAuthorizationGuard;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalyticsController {
    private final AnalyticsService service;
    private final CrmAuthorizationGuard authorization;

    public AnalyticsController(AnalyticsService service, CrmAuthorizationGuard authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        authorization.requireListBusinessPartners(BusinessPartnerAuthorizationContext.forOperation("dashboard"));
        return service.dashboard();
    }

    @GetMapping("/reports/crm")
    public CrmReportResponse crmReport() {
        authorization.requireListBusinessPartners(BusinessPartnerAuthorizationContext.forOperation("crmReport"));
        return service.crmReport();
    }
}
