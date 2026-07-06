// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.api;

import com.contactcore.connector.application.ConnectorSessionService;
import com.contactcore.connector.security.ConnectorAuthorizationGuard;
import com.contactcore.security.application.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/connectors/session")
public class ConnectorSessionController {
    private final ConnectorSessionService sessions;
    private final ConnectorAuthorizationGuard authorization;

    public ConnectorSessionController(ConnectorSessionService sessions, ConnectorAuthorizationGuard authorization) {
        this.sessions = sessions;
        this.authorization = authorization;
    }

    @GetMapping
    public ConnectorSessionResponse status(@AuthenticationPrincipal UserPrincipal principal) {
        authorization.requireReadSession();
        return sessions.status(principal.id());
    }

    @PostMapping
    public ConnectorSessionResponse login(@AuthenticationPrincipal UserPrincipal principal,
                                          @Valid @RequestBody ConnectorLoginRequest request) {
        authorization.requireConnectSession(request.connectorInstanceId());
        return sessions.login(principal.id(), request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disconnect(@AuthenticationPrincipal UserPrincipal principal) {
        authorization.requireDisconnectSession();
        sessions.disconnect(principal.id());
    }
}
