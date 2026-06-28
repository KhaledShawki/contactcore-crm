// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import com.contactcore.assistant.application.answer.AssistantAnswerGenerationResult;
import com.contactcore.assistant.application.answer.AssistantAnswerSource;
import com.contactcore.assistant.application.answer.AssistantAnswerStatus;
import com.contactcore.assistant.domain.AssistantAuditEvent;
import com.contactcore.assistant.domain.AssistantAuditEventRepository;
import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssistantAuditService {
    private final AssistantAuditEventRepository repository;

    public AssistantAuditService(AssistantAuditEventRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordAnswer(Long userId, Long conversationId, AssistantRetrievalType type, int retrievalCount,
                             AssistantAnswerGenerationResult answer) {
        boolean success = answer.status() != AssistantAnswerStatus.FAILED;
        repository.save(new AssistantAuditEvent(
                userId,
                conversationId,
                type.name(),
                retrievalCount,
                answer.modelName(),
                success,
                sanitize(answer.failureReason()),
                answer.status().name(),
                answer.source().name()
        ));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordSuccess(Long userId, Long conversationId, AssistantRetrievalType type, int retrievalCount, String modelName) {
        repository.save(new AssistantAuditEvent(
                userId,
                conversationId,
                type.name(),
                retrievalCount,
                modelName,
                true,
                null,
                AssistantAnswerStatus.SUCCESS.name(),
                AssistantAnswerSource.LLM.name()
        ));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(Long userId, Long conversationId, AssistantRetrievalType type, String modelName, RuntimeException exception) {
        repository.save(new AssistantAuditEvent(
                userId,
                conversationId,
                type.name(),
                0,
                modelName,
                false,
                sanitize(exception.getMessage()),
                AssistantAnswerStatus.FAILED.name(),
                null
        ));
    }

    private String sanitize(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }
        return message.length() <= 500 ? message : message.substring(0, 500);
    }
}
