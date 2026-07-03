// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.api;

import com.contactcore.connector.application.ConnectorBusinessPartnerQueryService;
import com.contactcore.connector.model.CrmBusinessPartnerSearchCriteria;
import com.contactcore.connector.model.CrmBusinessPartnerType;
import com.contactcore.connector.model.CrmBusinessPartnerView;
import com.contactcore.security.application.UserPrincipal;
import com.contactcore.shared.api.PageResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/connectors/business-partners")
public class ConnectorBusinessPartnerController {
    private final ConnectorBusinessPartnerQueryService service;

    public ConnectorBusinessPartnerController(ConnectorBusinessPartnerQueryService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<CrmBusinessPartnerView> search(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false, defaultValue = "") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code_asc") String sort
    ) {
        return service.search(principal.id(), new CrmBusinessPartnerSearchCriteria(q, CrmBusinessPartnerType.optional(type), page, size, sort));
    }

    @GetMapping("/{externalId}")
    public CrmBusinessPartnerView get(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String externalId) {
        return service.get(principal.id(), externalId);
    }
}
