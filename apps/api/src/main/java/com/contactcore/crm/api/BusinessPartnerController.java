// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.api;

import com.contactcore.crm.application.BusinessPartnerService;
import com.contactcore.crm.security.BusinessPartnerAuthorizationContext;
import com.contactcore.crm.security.CrmAuthorizationGuard;
import com.contactcore.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crm/business-partners")
public class BusinessPartnerController {
    private final BusinessPartnerService service;
    private final CrmAuthorizationGuard authorization;

    public BusinessPartnerController(BusinessPartnerService service, CrmAuthorizationGuard authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @GetMapping
    public PageResponse<BusinessPartnerResponse> search(
            @RequestParam String kind,
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updated_desc") String sort
    ) {
        authorization.requireListBusinessPartners(BusinessPartnerAuthorizationContext.forSearch(kind, q));
        return service.search(kind, q, page, size, sort);
    }

    @GetMapping("/{id}")
    public BusinessPartnerResponse get(@PathVariable Long id) {
        authorization.requireReadBusinessPartner(id);
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessPartnerResponse create(@Valid @RequestBody BusinessPartnerWriteRequest request) {
        authorization.requireCreateBusinessPartner(BusinessPartnerAuthorizationContext.forWrite(request, "create"));
        return service.create(request);
    }

    @PutMapping("/{id}")
    public BusinessPartnerResponse update(@PathVariable Long id, @Valid @RequestBody BusinessPartnerWriteRequest request) {
        authorization.requireUpdateBusinessPartner(id, BusinessPartnerAuthorizationContext.forWrite(request, "update"));
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable Long id) {
        authorization.requireDeleteBusinessPartner(id);
        service.archive(id);
    }
}
