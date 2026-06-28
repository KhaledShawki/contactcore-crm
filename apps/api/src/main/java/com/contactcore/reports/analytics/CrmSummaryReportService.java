// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.analytics;

import com.contactcore.analytics.api.ChartPointResponse;
import com.contactcore.analytics.api.CrmReportResponse;
import com.contactcore.analytics.api.KpiResponse;
import com.contactcore.analytics.api.MarketingSourceReportRowResponse;
import com.contactcore.analytics.api.RecentBusinessPartnerResponse;
import com.contactcore.analytics.application.AnalyticsService;
import com.contactcore.reports.excel.ExcelColumn;
import com.contactcore.reports.excel.ExcelSheet;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CrmSummaryReportService {
    private final AnalyticsService analyticsService;

    public CrmSummaryReportService(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    public List<ExcelSheet<?>> buildSheets() {
        CrmReportResponse report = analyticsService.crmReport();
        return List.of(
                kpiSheet(report.kpis()),
                chartSheet("Records by Type", report.kindBreakdown()),
                chartSheet("Records by Status", report.statusBreakdown()),
                chartSheet("Records by Source", report.marketingSourceBreakdown()),
                chartSheet("Contact Roles", report.contactPersonsByRole()),
                chartSheet("Contact Coverage", report.contactCoverageByKind()),
                marketingPerformanceSheet(report.marketingSourcePerformance()),
                recentBusinessPartnersSheet(report.recentBusinessPartners())
        );
    }

    private ExcelSheet<KpiResponse> kpiSheet(List<KpiResponse> rows) {
        return new ExcelSheet<>("KPIs", List.of(
                ExcelColumn.text("Key", 18, KpiResponse::key),
                ExcelColumn.text("Label", 28, KpiResponse::label),
                ExcelColumn.decimal("Value", 14, KpiResponse::value),
                ExcelColumn.text("Unit", 12, KpiResponse::unit),
                ExcelColumn.text("Help text", 44, KpiResponse::helpText)
        ), rows);
    }

    private ExcelSheet<ChartPointResponse> chartSheet(String name, List<ChartPointResponse> rows) {
        return new ExcelSheet<>(name, List.of(
                ExcelColumn.text("Label", 32, ChartPointResponse::label),
                ExcelColumn.integer("Value", 14, ChartPointResponse::value)
        ), rows);
    }

    private ExcelSheet<MarketingSourceReportRowResponse> marketingPerformanceSheet(List<MarketingSourceReportRowResponse> rows) {
        return new ExcelSheet<>("Marketing Performance", List.of(
                ExcelColumn.text("Marketing source", 28, MarketingSourceReportRowResponse::marketingSource),
                ExcelColumn.integer("Leads", 12, MarketingSourceReportRowResponse::leads),
                ExcelColumn.integer("Qualified leads", 18, MarketingSourceReportRowResponse::qualifiedLeads),
                ExcelColumn.integer("Customers", 14, MarketingSourceReportRowResponse::customers),
                ExcelColumn.decimal("Qualification rate", 20, MarketingSourceReportRowResponse::leadQualificationRate)
        ), rows);
    }

    private ExcelSheet<RecentBusinessPartnerResponse> recentBusinessPartnersSheet(List<RecentBusinessPartnerResponse> rows) {
        return new ExcelSheet<>("Recent Records", List.of(
                ExcelColumn.text("Type", 12, RecentBusinessPartnerResponse::kind),
                ExcelColumn.text("Code", 16, RecentBusinessPartnerResponse::code),
                ExcelColumn.text("Name", 30, RecentBusinessPartnerResponse::name),
                ExcelColumn.text("Status", 18, RecentBusinessPartnerResponse::status),
                ExcelColumn.text("Marketing source", 24, RecentBusinessPartnerResponse::marketingSource),
                ExcelColumn.instant("Created at", 20, RecentBusinessPartnerResponse::createdAt)
        ), rows);
    }
}
