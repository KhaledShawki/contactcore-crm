// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.api;

import com.contactcore.assistant.application.AssistantApplicationService;
import com.contactcore.security.application.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {
    private final AssistantApplicationService service;

    public AssistantController(AssistantApplicationService service) {
        this.service = service;
    }

    @PostMapping("/messages")
    public AssistantResponse sendMessage(@AuthenticationPrincipal UserPrincipal principal,
                                         @Valid @RequestBody AssistantRequest request) {
        return service.sendMessage(principal.id(), request);
    }

    @GetMapping("/conversations")
    public List<AssistantConversationResponse> conversations(@AuthenticationPrincipal UserPrincipal principal) {
        return service.conversations(principal.id());
    }

    @GetMapping("/conversations/{id}")
    public AssistantConversationDetailResponse conversation(@AuthenticationPrincipal UserPrincipal principal,
                                                            @PathVariable Long id) {
        return service.conversation(principal.id(), id);
    }

    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Void> archiveConversation(@AuthenticationPrincipal UserPrincipal principal,
                                                    @PathVariable Long id) {
        service.archiveConversation(principal.id(), id);
        return ResponseEntity.noContent().build();
    }
}
