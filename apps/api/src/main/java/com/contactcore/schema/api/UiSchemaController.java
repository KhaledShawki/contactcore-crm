// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import com.contactcore.schema.application.UiSchemaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ui")
public class UiSchemaController {
    private final UiSchemaService service;

    public UiSchemaController(UiSchemaService service) {
        this.service = service;
    }

    @GetMapping("/manifest")
    public UiManifest manifest() {
        return service.manifest();
    }

    @GetMapping("/screens/{key}")
    public UiScreen screen(@PathVariable String key) {
        return service.screen(key);
    }
}
