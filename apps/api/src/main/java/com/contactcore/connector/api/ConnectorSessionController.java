// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.api;

import com.contactcore.connector.application.ConnectorSessionService;
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

    public ConnectorSessionController(ConnectorSessionService sessions) {
        this.sessions = sessions;
    }

    @GetMapping
    public ConnectorSessionResponse status(@AuthenticationPrincipal UserPrincipal principal) {
        return sessions.status(principal.id());
    }

    @PostMapping
    public ConnectorSessionResponse login(@AuthenticationPrincipal UserPrincipal principal,
                                          @Valid @RequestBody ConnectorLoginRequest request) {
        return sessions.login(principal.id(), request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disconnect(@AuthenticationPrincipal UserPrincipal principal) {
        sessions.disconnect(principal.id());
    }
}
