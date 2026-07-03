// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.api;

import com.contactcore.connector.application.ConnectorInstanceService;
import com.contactcore.security.application.UserPrincipal;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/connectors")
public class ConnectorController {
    private final ConnectorInstanceService instances;

    public ConnectorController(ConnectorInstanceService instances) {
        this.instances = instances;
    }

    @GetMapping("/instances")
    public List<ConnectorInstanceResponse> instances(@AuthenticationPrincipal UserPrincipal principal) {
        return instances.availableInstances(principal.id());
    }
}
