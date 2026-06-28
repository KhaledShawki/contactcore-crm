// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.marketing;

import com.contactcore.reports.excel.ExcelColumn;
import com.contactcore.reports.excel.ExcelSheet;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MarketingSourceReportService {
    private final MarketingSourceReportQueryService queryService;

    public MarketingSourceReportService(MarketingSourceReportQueryService queryService) {
        this.queryService = queryService;
    }

    public ExcelSheet<MarketingSourceReportRow> buildSheet(String query, int maxRows) {
        return new ExcelSheet<>("Marketing Sources", columns(), queryService.search(query, maxRows));
    }

    private List<ExcelColumn<MarketingSourceReportRow>> columns() {
        return List.of(
                ExcelColumn.text("Code", 16, MarketingSourceReportRow::code),
                ExcelColumn.text("Name", 28, MarketingSourceReportRow::name),
                ExcelColumn.integer("Sort order", 14, MarketingSourceReportRow::sortOrder),
                ExcelColumn.integer("Business partners", 18, MarketingSourceReportRow::businessPartners),
                ExcelColumn.integer("Leads", 12, MarketingSourceReportRow::leads),
                ExcelColumn.integer("Customers", 12, MarketingSourceReportRow::customers),
                ExcelColumn.integer("Suppliers", 12, MarketingSourceReportRow::suppliers),
                ExcelColumn.instant("Created at", 20, MarketingSourceReportRow::createdAt),
                ExcelColumn.instant("Updated at", 20, MarketingSourceReportRow::updatedAt)
        );
    }
}
