// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.api;

import com.contactcore.crm.application.ContactPersonService;
import com.contactcore.crm.security.CrmAuthorizationGuard;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crm/business-partners/{businessPartnerId}/contact-persons")
public class ContactPersonController {
    private final ContactPersonService service;
    private final CrmAuthorizationGuard authorization;

    public ContactPersonController(ContactPersonService service, CrmAuthorizationGuard authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @GetMapping
    public List<ContactPersonResponse> list(@PathVariable Long businessPartnerId) {
        authorization.requireReadBusinessPartner(businessPartnerId);
        return service.list(businessPartnerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactPersonResponse create(@PathVariable Long businessPartnerId, @Valid @RequestBody ContactPersonWriteRequest request) {
        authorization.requireUpdateBusinessPartner(businessPartnerId);
        return service.create(businessPartnerId, request);
    }

    @PutMapping("/{id}")
    public ContactPersonResponse update(@PathVariable Long businessPartnerId, @PathVariable Long id,
                                        @Valid @RequestBody ContactPersonWriteRequest request) {
        authorization.requireUpdateBusinessPartner(businessPartnerId);
        return service.update(businessPartnerId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable Long businessPartnerId, @PathVariable Long id) {
        authorization.requireUpdateBusinessPartner(businessPartnerId);
        service.archive(businessPartnerId, id);
    }
}
