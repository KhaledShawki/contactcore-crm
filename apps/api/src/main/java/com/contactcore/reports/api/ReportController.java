// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.api;

import com.contactcore.reports.application.ReportExportService;
import com.contactcore.reports.application.ReportFile;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportExportService exportService;

    public ReportController(ReportExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/business-partners.xlsx")
    public ResponseEntity<byte[]> businessPartners(
            @RequestParam String kind,
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "updated_desc") String sort,
            @RequestParam(defaultValue = "5000") int maxRows
    ) {
        return download(exportService.businessPartners(kind, q, sort, maxRows));
    }

    @GetMapping("/crm-summary.xlsx")
    public ResponseEntity<byte[]> crmSummary() {
        return download(exportService.crmSummary());
    }

    @GetMapping("/marketing-sources.xlsx")
    public ResponseEntity<byte[]> marketingSources(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "5000") int maxRows
    ) {
        return download(exportService.marketingSources(q, maxRows));
    }

    private ResponseEntity<byte[]> download(ReportFile file) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, file.contentType())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(file.filename()).build().toString())
                .cacheControl(CacheControl.noStore())
                .body(file.content());
    }
}
