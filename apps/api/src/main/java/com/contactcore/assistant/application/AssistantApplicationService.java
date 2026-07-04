// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import com.contactcore.assistant.api.AssistantConversationDetailResponse;
import com.contactcore.assistant.api.AssistantConversationResponse;
import com.contactcore.assistant.api.AssistantMessageResponse;
import com.contactcore.assistant.api.AssistantReferenceResponse;
import com.contactcore.assistant.api.AssistantRequest;
import com.contactcore.assistant.api.AssistantResponse;
import com.contactcore.assistant.domain.AssistantConversation;
import com.contactcore.assistant.domain.AssistantConversationRepository;
import com.contactcore.assistant.domain.AssistantMessage;
import com.contactcore.assistant.domain.AssistantMessageRepository;
import com.contactcore.assistant.application.answer.AssistantAnswerGenerationResult;
import com.contactcore.assistant.application.i18n.AssistantLocaleContextResolver;
import com.contactcore.assistant.application.evidence.AssistantEvidence;
import com.contactcore.assistant.application.evidence.AssistantEvidenceGate;
import com.contactcore.assistant.application.answer.AssistantAnswerGenerationService;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRecordReference;
import com.contactcore.assistant.security.AssistantInputGuard;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import com.contactcore.assistant.tool.AssistantToolOrchestrator;
import com.contactcore.shared.api.InvalidRequestException;
import com.contactcore.shared.localization.LocaleContextResolver;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssistantApplicationService {
    private static final int TITLE_LIMIT = 80;

    private final AssistantProperties properties;
    private final AssistantConversationRepository conversations;
    private final AssistantMessageRepository messages;
    private final AssistantInputGuard inputGuard;
    private final AssistantRateLimitService rateLimitService;
    private final AssistantQueryPlanner queryPlanner;
    private final AssistantToolOrchestrator toolOrchestrator;
    private final AssistantEvidenceGate evidenceGate;
    private final AssistantAnswerGenerationService answerGenerationService;
    private final AssistantAuditService auditService;
    private final AssistantPersistenceService persistenceService;
    private final LocaleContextResolver localeContextResolver;
    private final AssistantLocaleContextResolver assistantLocaleContextResolver;

    public AssistantApplicationService(AssistantProperties properties,
                                       AssistantConversationRepository conversations,
                                       AssistantMessageRepository messages,
                                       AssistantInputGuard inputGuard,
                                       AssistantRateLimitService rateLimitService,
                                       AssistantQueryPlanner queryPlanner,
                                       AssistantToolOrchestrator toolOrchestrator,
                                       AssistantEvidenceGate evidenceGate,
                                       AssistantAnswerGenerationService answerGenerationService,
                                       AssistantAuditService auditService,
                                       AssistantPersistenceService persistenceService,
                                       LocaleContextResolver localeContextResolver,
                                       AssistantLocaleContextResolver assistantLocaleContextResolver) {
        this.properties = properties;
        this.conversations = conversations;
        this.messages = messages;
        this.inputGuard = inputGuard;
        this.rateLimitService = rateLimitService;
        this.queryPlanner = queryPlanner;
        this.toolOrchestrator = toolOrchestrator;
        this.evidenceGate = evidenceGate;
        this.answerGenerationService = answerGenerationService;
        this.auditService = auditService;
        this.persistenceService = persistenceService;
        this.localeContextResolver = localeContextResolver;
        this.assistantLocaleContextResolver = assistantLocaleContextResolver;
    }

    public AssistantResponse sendMessage(Long userId, AssistantRequest request) {
        ensureEnabled();
        rateLimitService.check(userId);
        String userMessage = inputGuard.normalizeAndValidate(request.message());
        AssistantPersistenceService.AssistantUserMessagePersistResult persistedUserMessage = persistenceService.saveUserMessage(
                userId,
                request.conversationId(),
                userMessage,
                titleFrom(userMessage)
        );
        AssistantConversation conversation = persistedUserMessage.conversation();
        AssistantLocaleContext locale = assistantLocaleContextResolver.resolve(userMessage, localeContextResolver.resolveForUser(userId));
        AssistantPlan plan = queryPlanner.plan(userMessage);

        try {
            AssistantRetrievalResult rawRetrieval = retrieve(userId, plan);
            AssistantEvidence evidence = evidenceGate.assess(plan, rawRetrieval);
            AssistantRetrievalResult retrieval = evidence.retrieval();
            AssistantAnswerGenerationResult answer = answerGenerationService.generate(plan, retrieval, userMessage, locale);
            List<AssistantRecordReference> persistedReferences = retrieval.references().stream()
                    .filter(reference -> reference.entityId() != null && reference.entityId() > 0)
                    .toList();
            AssistantMessage assistantMessage = persistenceService.saveAssistantMessage(userId, conversation.getId(), answer.answer(), persistedReferences);
            auditService.recordAnswer(userId, conversation.getId(), plan.retrievalType(), persistedReferences.size(), answer);

            return new AssistantResponse(
                    conversation.getId(),
                    answer.answer(),
                    plan.retrievalType().name(),
                    answer.modelName(),
                    answer.status().name(),
                    answer.source().name(),
                    answer.warning(),
                    persistedReferences.stream().map(this::toReferenceResponse).toList(),
                    assistantMessage.getCreatedAt() == null ? Instant.now() : assistantMessage.getCreatedAt()
            );
        } catch (RuntimeException exception) {
            auditService.recordFailure(userId, conversation.getId(), plan.retrievalType(), properties.model(), exception);
            throw exception;
        }
    }

    private AssistantRetrievalResult retrieve(Long userId, AssistantPlan plan) {
        if (plan.toolCalls().isEmpty()) {
            return new AssistantRetrievalResult(plan.retrievalType(), plan.userIntent(), List.of());
        }
        return toolOrchestrator.execute(userId, plan);
    }

    @Transactional(readOnly = true)
    public List<AssistantConversationResponse> conversations(Long userId) {
        return conversations.findActiveByUserId(userId).stream()
                .map(conversation -> new AssistantConversationResponse(
                        conversation.getId(),
                        conversation.getTitle(),
                        conversation.getCreatedAt(),
                        conversation.getUpdatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public AssistantConversationDetailResponse conversation(Long userId, Long id) {
        AssistantConversation conversation = persistenceService.findConversation(userId, id);
        List<AssistantMessageResponse> messageResponses = messages.findConversationMessages(id).stream()
                .map(this::toMessageResponse)
                .toList();
        return new AssistantConversationDetailResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt(),
                messageResponses
        );
    }

    @Transactional
    public void archiveConversation(Long userId, Long id) {
        AssistantConversation conversation = persistenceService.findConversation(userId, id);
        conversation.archive();
    }

    private void ensureEnabled() {
        if (!properties.enabled()) {
            throw new InvalidRequestException("Assistant is disabled.");
        }
    }

    private String titleFrom(String message) {
        return message.length() <= TITLE_LIMIT ? message : message.substring(0, TITLE_LIMIT - 1) + "…";
    }

    private AssistantMessageResponse toMessageResponse(AssistantMessage message) {
        return new AssistantMessageResponse(
                message.getId(),
                message.getRole().name(),
                message.getContent(),
                message.getReferences().stream()
                        .map(reference -> new AssistantReferenceResponse(
                                reference.getEntityType(),
                                reference.getEntityId(),
                                reference.getLabel(),
                                reference.getRoute()
                        ))
                        .toList(),
                message.getCreatedAt()
        );
    }

    private AssistantReferenceResponse toReferenceResponse(AssistantRecordReference reference) {
        return new AssistantReferenceResponse(reference.entityType(), reference.entityId(), reference.label(), reference.route());
    }
}
