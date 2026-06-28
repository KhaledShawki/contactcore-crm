// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.application;

import com.contactcore.reports.analytics.CrmSummaryReportService;
import com.contactcore.reports.crm.BusinessPartnerReportService;
import com.contactcore.reports.excel.ExcelWorkbookWriter;
import com.contactcore.reports.marketing.MarketingSourceReportService;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportExportService {
    private final BusinessPartnerReportService businessPartnerReports;
    private final CrmSummaryReportService crmSummaryReports;
    private final MarketingSourceReportService marketingSourceReports;
    private final ExcelWorkbookWriter workbookWriter;
    private final ReportFilenameFactory filenameFactory;

    public ReportExportService(
            BusinessPartnerReportService businessPartnerReports,
            CrmSummaryReportService crmSummaryReports,
            MarketingSourceReportService marketingSourceReports,
            ExcelWorkbookWriter workbookWriter,
            ReportFilenameFactory filenameFactory
    ) {
        this.businessPartnerReports = businessPartnerReports;
        this.crmSummaryReports = crmSummaryReports;
        this.marketingSourceReports = marketingSourceReports;
        this.workbookWriter = workbookWriter;
        this.filenameFactory = filenameFactory;
    }

    @Transactional(readOnly = true)
    public ReportFile businessPartners(String kind, String query, String sort, int maxRows) {
        var sheet = businessPartnerReports.buildSheet(kind, query, sort, maxRows);
        String reportKind = kind == null || kind.isBlank() ? "business-partners" : kind.trim().toLowerCase(Locale.ROOT) + "s";
        String filename = filenameFactory.xlsx("contactcore-" + reportKind);
        return new ReportFile(filename, ExcelWorkbookWriter.XLSX_CONTENT_TYPE, workbookWriter.write(sheet));
    }

    @Transactional(readOnly = true)
    public ReportFile crmSummary() {
        String filename = filenameFactory.xlsx("contactcore-crm-report");
        return new ReportFile(filename, ExcelWorkbookWriter.XLSX_CONTENT_TYPE, workbookWriter.write(crmSummaryReports.buildSheets()));
    }

    @Transactional(readOnly = true)
    public ReportFile marketingSources(String query, int maxRows) {
        var sheet = marketingSourceReports.buildSheet(query, maxRows);
        String filename = filenameFactory.xlsx("contactcore-marketing-sources");
        return new ReportFile(filename, ExcelWorkbookWriter.XLSX_CONTENT_TYPE, workbookWriter.write(sheet));
    }
}
