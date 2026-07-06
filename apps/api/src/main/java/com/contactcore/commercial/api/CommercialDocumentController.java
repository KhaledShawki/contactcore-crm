// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.api;

import com.contactcore.commercial.application.CommercialDocumentQueryService;
import com.contactcore.commercial.domain.CommercialDocumentStatus;
import com.contactcore.commercial.domain.CommercialDocumentType;
import com.contactcore.commercial.domain.CommercialSourceSystem;
import com.contactcore.commercial.security.CommercialAuthorizationGuard;
import com.contactcore.commercial.security.CommercialDocumentAuthorizationContext;
import com.contactcore.shared.api.PageResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/commercial-documents")
public class CommercialDocumentController {
    private final CommercialDocumentQueryService service;
    private final CommercialAuthorizationGuard authorization;

    public CommercialDocumentController(CommercialDocumentQueryService service, CommercialAuthorizationGuard authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @GetMapping
    public PageResponse<CommercialDocumentSummaryResponse> search(
            @RequestParam(required = false) Long businessPartnerId,
            @RequestParam(required = false) CommercialDocumentType type,
            @RequestParam(required = false) CommercialDocumentStatus status,
            @RequestParam(required = false) CommercialSourceSystem sourceSystem,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updated_desc") String sort
    ) {
        authorization.requireListDocuments(new CommercialDocumentAuthorizationContext(
                businessPartnerId,
                type,
                status,
                sourceSystem,
                fromDate,
                toDate
        ));
        return service.search(businessPartnerId, type, status, sourceSystem, fromDate, toDate, q, page, size, sort);
    }

    @GetMapping("/{id}")
    public CommercialDocumentDetailResponse get(@PathVariable Long id) {
        authorization.requireReadDocument(id);
        return service.get(id);
    }

    @GetMapping("/{id}/lines")
    public List<CommercialDocumentLineResponse> lines(@PathVariable Long id) {
        authorization.requireReadDocument(id);
        return service.lines(id);
    }
}
