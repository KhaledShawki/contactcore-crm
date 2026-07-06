// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.api;

import com.contactcore.dashboard.application.CommercialDashboardQuery;
import com.contactcore.dashboard.application.CommercialDashboardService;
import com.contactcore.dashboard.security.DashboardAuthorizationGuard;
import com.contactcore.security.application.UserPrincipal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard/commercial")
public class CommercialDashboardController {
    private final CommercialDashboardService service;
    private final DashboardAuthorizationGuard authorization;

    public CommercialDashboardController(CommercialDashboardService service, DashboardAuthorizationGuard authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @GetMapping("/summary")
    public CommercialDashboardSummaryResponse summary(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String currency
    ) {
        CommercialDashboardQuery query = query(from, to, currency, null, null);
        authorization.requireCommercialFinancials("summary", query);
        return service.summary(principal.id(), query);
    }

    @GetMapping("/top-selling-items")
    public List<TopSellingItemResponse> topSellingItems(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) Integer limit
    ) {
        CommercialDashboardQuery query = query(from, to, currency, limit, null);
        authorization.requireCommercialDashboard("topSellingItems", query);
        return service.topSellingItems(principal.id(), query);
    }

    @GetMapping("/top-customers")
    public List<TopCustomerResponse> topCustomers(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) Integer limit
    ) {
        CommercialDashboardQuery query = query(from, to, currency, limit, null);
        authorization.requireCommercialDashboard("topCustomers", query);
        return service.topCustomers(principal.id(), query);
    }

    @GetMapping("/unpaid-invoices")
    public List<UnpaidInvoiceCustomerResponse> unpaidInvoices(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) Integer limit
    ) {
        CommercialDashboardQuery query = query(from, to, currency, limit, null);
        authorization.requireCommercialFinancials("unpaidInvoices", query);
        return service.unpaidInvoices(principal.id(), query);
    }

    @GetMapping("/invoice-aging")
    public List<InvoiceAgingBucketResponse> invoiceAging(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String currency
    ) {
        CommercialDashboardQuery query = query(from, to, currency, null, null);
        authorization.requireCommercialFinancials("invoiceAging", query);
        return service.invoiceAging(principal.id(), query);
    }

    @GetMapping("/sales-trend")
    public List<SalesTrendPointResponse> salesTrend(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false, defaultValue = "MONTH") String groupBy
    ) {
        CommercialDashboardQuery query = query(from, to, currency, null, groupBy);
        authorization.requireCommercialDashboard("salesTrend", query);
        return service.salesTrend(principal.id(), query);
    }

    @GetMapping("/sales-by-document-type")
    public List<SalesByDocumentTypeResponse> salesByDocumentType(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String currency
    ) {
        CommercialDashboardQuery query = query(from, to, currency, null, null);
        authorization.requireCommercialDashboard("salesByDocumentType", query);
        return service.salesByDocumentType(principal.id(), query);
    }

    private CommercialDashboardQuery query(LocalDate from, LocalDate to, String currency, Integer limit, String groupBy) {
        return service.query(from, to, currency, limit, groupBy);
    }
}
