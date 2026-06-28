// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.marketing.api;

import com.contactcore.marketing.application.MarketingSourceService;
import com.contactcore.shared.api.PageResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marketing/sources")
public class MarketingSourceController {
    private final MarketingSourceService service;

    public MarketingSourceController(MarketingSourceService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<MarketingSourceResponse> search(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return service.search(q, page, size);
    }

    @GetMapping("/options")
    public List<MarketingSourceResponse> options() {
        return service.listActive();
    }

    @GetMapping("/{id}")
    public MarketingSourceResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarketingSourceResponse create(@Valid @RequestBody MarketingSourceWriteRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public MarketingSourceResponse update(@PathVariable Long id, @Valid @RequestBody MarketingSourceWriteRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable Long id) {
        service.archive(id);
    }
}
