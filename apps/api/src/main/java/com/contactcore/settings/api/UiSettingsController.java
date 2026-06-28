// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.settings.api;

import com.contactcore.security.application.UserPrincipal;
import com.contactcore.settings.application.UiSettingsService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings/ui")
public class UiSettingsController {
    private final UiSettingsService service;

    public UiSettingsController(UiSettingsService service) {
        this.service = service;
    }

    @GetMapping
    public UiSettingsResponse get(@AuthenticationPrincipal UserPrincipal principal) {
        return service.get(principal.id());
    }

    @PutMapping
    public UiSettingsResponse update(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody UiSettingsWriteRequest request) {
        return service.update(principal.id(), request);
    }
}
