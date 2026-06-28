// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.crm;

import com.contactcore.reports.excel.ExcelColumn;
import com.contactcore.reports.excel.ExcelSheet;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BusinessPartnerReportService {
    private final BusinessPartnerReportQueryService queryService;

    public BusinessPartnerReportService(BusinessPartnerReportQueryService queryService) {
        this.queryService = queryService;
    }

    public ExcelSheet<BusinessPartnerReportRow> buildSheet(String kind, String query, String sort, int maxRows) {
        List<BusinessPartnerReportRow> rows = queryService.search(kind, query, sort, maxRows);
        return new ExcelSheet<>(sheetName(kind), columns(), rows);
    }

    private String sheetName(String kind) {
        return switch (kind == null ? "" : kind.trim().toUpperCase()) {
            case "CUSTOMER" -> "Customers";
            case "LEAD" -> "Leads";
            case "SUPPLIER" -> "Suppliers";
            default -> "Business Partners";
        };
    }

    private List<ExcelColumn<BusinessPartnerReportRow>> columns() {
        return List.of(
                ExcelColumn.text("Type", 12, BusinessPartnerReportRow::type),
                ExcelColumn.text("Code", 16, BusinessPartnerReportRow::code),
                ExcelColumn.text("Name", 30, BusinessPartnerReportRow::name),
                ExcelColumn.text("Status", 18, BusinessPartnerReportRow::status),
                ExcelColumn.text("Marketing source", 22, BusinessPartnerReportRow::marketingSource),
                ExcelColumn.text("Primary email", 30, BusinessPartnerReportRow::primaryEmail),
                ExcelColumn.text("Primary phone", 18, BusinessPartnerReportRow::primaryPhone),
                ExcelColumn.text("Website", 28, BusinessPartnerReportRow::website),
                ExcelColumn.text("Primary contact person", 28, BusinessPartnerReportRow::primaryContactPerson),
                ExcelColumn.integer("Contact persons", 16, BusinessPartnerReportRow::contactPersonCount),
                ExcelColumn.integer("Documents", 12, BusinessPartnerReportRow::documentCount),
                ExcelColumn.text("City", 18, BusinessPartnerReportRow::city),
                ExcelColumn.text("Country", 12, BusinessPartnerReportRow::countryCode),
                ExcelColumn.instant("Created at", 20, BusinessPartnerReportRow::createdAt),
                ExcelColumn.instant("Updated at", 20, BusinessPartnerReportRow::updatedAt),
                ExcelColumn.text("Notes", 36, BusinessPartnerReportRow::notes)
        );
    }
}
