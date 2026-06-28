// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.api;

import com.contactcore.crm.application.ContactPersonService;
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

    public ContactPersonController(ContactPersonService service) {
        this.service = service;
    }

    @GetMapping
    public List<ContactPersonResponse> list(@PathVariable Long businessPartnerId) {
        return service.list(businessPartnerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactPersonResponse create(@PathVariable Long businessPartnerId, @Valid @RequestBody ContactPersonWriteRequest request) {
        return service.create(businessPartnerId, request);
    }

    @PutMapping("/{id}")
    public ContactPersonResponse update(@PathVariable Long businessPartnerId, @PathVariable Long id,
                                        @Valid @RequestBody ContactPersonWriteRequest request) {
        return service.update(businessPartnerId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable Long businessPartnerId, @PathVariable Long id) {
        service.archive(businessPartnerId, id);
    }
}
