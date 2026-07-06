// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.api;

import com.contactcore.commercial.application.ItemQueryService;
import com.contactcore.commercial.domain.CommercialSourceSystem;
import com.contactcore.commercial.security.CommercialAuthorizationGuard;
import com.contactcore.commercial.security.ItemAuthorizationContext;
import com.contactcore.shared.api.PageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemQueryService service;
    private final CommercialAuthorizationGuard authorization;

    public ItemController(ItemQueryService service, CommercialAuthorizationGuard authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @GetMapping
    public PageResponse<ItemSummaryResponse> search(
            @RequestParam(required = false) CommercialSourceSystem sourceSystem,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updated_desc") String sort
    ) {
        authorization.requireListItems(new ItemAuthorizationContext(sourceSystem, active));
        return service.search(sourceSystem, active, q, page, size, sort);
    }

    @GetMapping("/{id}")
    public ItemDetailResponse get(@PathVariable Long id) {
        authorization.requireReadItem(id);
        return service.get(id);
    }
}
