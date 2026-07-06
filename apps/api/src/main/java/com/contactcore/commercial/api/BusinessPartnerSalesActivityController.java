// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.api;

import com.contactcore.commercial.application.BusinessPartnerSalesActivityService;
import com.contactcore.commercial.security.CommercialAuthorizationGuard;
import com.contactcore.commercial.security.CommercialDocumentAuthorizationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crm/business-partners/{businessPartnerId}/sales-activity")
public class BusinessPartnerSalesActivityController {
    private final BusinessPartnerSalesActivityService service;
    private final CommercialAuthorizationGuard authorization;

    public BusinessPartnerSalesActivityController(BusinessPartnerSalesActivityService service, CommercialAuthorizationGuard authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @GetMapping
    public BusinessPartnerSalesActivityResponse get(
            @PathVariable Long businessPartnerId,
            @RequestParam(defaultValue = "10") int size
    ) {
        authorization.requireListDocuments(new CommercialDocumentAuthorizationContext(
                businessPartnerId,
                null,
                null,
                null,
                null,
                null
        ));
        return service.get(businessPartnerId, size);
    }
}
