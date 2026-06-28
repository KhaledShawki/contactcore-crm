// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.analytics.api;

import com.contactcore.analytics.application.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalyticsController {
    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return service.dashboard();
    }

    @GetMapping("/reports/crm")
    public CrmReportResponse crmReport() {
        return service.crmReport();
    }
}
