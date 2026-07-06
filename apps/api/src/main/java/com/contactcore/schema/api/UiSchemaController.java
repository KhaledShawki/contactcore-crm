// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import com.contactcore.schema.application.SchemaAuthorizationGuard;
import com.contactcore.schema.application.UiSchemaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ui")
public class UiSchemaController {
    private final UiSchemaService service;
    private final SchemaAuthorizationGuard authorization;

    public UiSchemaController(UiSchemaService service, SchemaAuthorizationGuard authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @GetMapping("/manifest")
    public UiManifest manifest() {
        authorization.requireReadManifest();
        return service.manifest();
    }

    @GetMapping("/screens/{key}")
    public UiScreen screen(@PathVariable String key) {
        authorization.requireReadManifest();
        return service.screen(key);
    }
}
