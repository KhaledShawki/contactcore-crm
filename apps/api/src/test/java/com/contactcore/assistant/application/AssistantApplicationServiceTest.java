// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.contactcore.assistant.api.AssistantRequest;
import com.contactcore.assistant.api.AssistantResponse;
import com.contactcore.assistant.domain.AssistantConversation;
import com.contactcore.assistant.domain.AssistantConversationRepository;
import com.contactcore.assistant.domain.AssistantMessage;
import com.contactcore.assistant.domain.AssistantMessageRepository;
import com.contactcore.assistant.domain.AssistantMessageRole;
import com.contactcore.assistant.application.answer.AssistantAnswerGenerationResult;
import com.contactcore.assistant.application.i18n.AssistantLocaleContextResolver;
import com.contactcore.assistant.application.answer.AssistantAnswerGenerationService;
import com.contactcore.assistant.application.answer.AssistantAnswerSource;
import com.contactcore.assistant.application.evidence.AssistantEvidence;
import com.contactcore.assistant.application.evidence.AssistantEvidenceGate;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRecordReference;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import com.contactcore.assistant.retrieval.AssistantSearchResult;
import com.contactcore.assistant.security.AssistantInputGuard;
import com.contactcore.assistant.tool.AssistantToolOrchestrator;
import com.contactcore.shared.api.InvalidRequestException;
import com.contactcore.shared.localization.LocaleContext;
import com.contactcore.shared.localization.LocaleContextResolver;
import com.contactcore.shared.localization.SupportedLocale;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssistantApplicationServiceTest {
    @Mock
    private AssistantConversationRepository conversations;
    @Mock
    private AssistantMessageRepository messages;
    @Mock
    private AssistantInputGuard inputGuard;
    @Mock
    private AssistantRateLimitService rateLimitService;
    @Mock
    private AssistantQueryPlanner queryPlanner;
    @Mock
    private AssistantToolOrchestrator toolOrchestrator;
    @Mock
    private AssistantEvidenceGate evidenceGate;
    @Mock
    private AssistantAnswerGenerationService answerGenerationService;
    @Mock
    private AssistantAuditService auditService;
    @Mock
    private AssistantPersistenceService persistenceService;
    @Mock
    private LocaleContextResolver localeContextResolver;
    @Mock
    private AssistantLocaleContextResolver assistantLocaleContextResolver;

    @Test
    void sendsMessageThroughToolsContextAndLlm() {
        AssistantProperties properties = new AssistantProperties(true, "noop", "test-model", "", "", 5000, 12000, 20, 10);
        AssistantApplicationService service = service(properties);
        AssistantConversation conversation = new AssistantConversation(1L, "Which leads need follow-up?");
        setId(conversation, 10L);
        AssistantMessage userMessage = new AssistantMessage(conversation, AssistantMessageRole.USER, "Which leads need follow-up?");
        AssistantPlan plan = new AssistantPlan(AssistantRetrievalType.STALE_LEADS, "Which leads need follow-up?", 20);
        AssistantRecordReference reference = new AssistantRecordReference("BUSINESS_PARTNER", 7L, "LED-001 - Meyer", "/leads/7");
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(
                AssistantRetrievalType.STALE_LEADS,
                List.of(AssistantSearchResult.of(reference, new LinkedHashMap<>()))
        );

        when(inputGuard.normalizeAndValidate("Which leads need follow-up?")).thenReturn("Which leads need follow-up?");
        when(persistenceService.saveUserMessage(1L, null, "Which leads need follow-up?", "Which leads need follow-up?"))
                .thenReturn(new AssistantPersistenceService.AssistantUserMessagePersistResult(conversation, userMessage));
        when(queryPlanner.plan("Which leads need follow-up?")).thenReturn(plan);
        when(toolOrchestrator.execute(1L, plan)).thenReturn(retrieval);
        when(evidenceGate.assess(plan, retrieval)).thenReturn(new AssistantEvidence(retrieval, false, "test"));
        when(answerGenerationService.generate(org.mockito.Mockito.eq(plan), org.mockito.Mockito.eq(retrieval), org.mockito.Mockito.eq("Which leads need follow-up?"), any(AssistantLocaleContext.class)))
                .thenReturn(AssistantAnswerGenerationResult.success(AssistantAnswerSource.LLM, "Answer", "test-model"));
        AssistantMessage assistantMessage = new AssistantMessage(conversation, AssistantMessageRole.ASSISTANT, "Answer");
        when(persistenceService.saveAssistantMessage(1L, 10L, "Answer", List.of(reference))).thenReturn(assistantMessage);

        AssistantResponse response = service.sendMessage(1L, new AssistantRequest(null, "Which leads need follow-up?"));

        assertThat(response.conversationId()).isEqualTo(10L);
        assertThat(response.answer()).isEqualTo("Answer");
        assertThat(response.references()).hasSize(1);
        assertThat(response.answerStatus()).isEqualTo("SUCCESS");
        assertThat(response.answerSource()).isEqualTo("LLM");
        verify(rateLimitService).check(1L);
        verify(persistenceService).saveAssistantMessage(1L, 10L, "Answer", List.of(reference));
        verify(auditService).recordAnswer(org.mockito.Mockito.eq(1L), org.mockito.Mockito.eq(10L), org.mockito.Mockito.eq(AssistantRetrievalType.STALE_LEADS), org.mockito.Mockito.eq(1), any(AssistantAnswerGenerationResult.class));
    }

    @Test
    void answersGreetingWithoutExecutingCrmToolsOrPersistingReferences() {
        AssistantProperties properties = new AssistantProperties(true, "noop", "test-model", "", "", 5000, 12000, 20, 10);
        AssistantApplicationService service = service(properties);
        AssistantConversation conversation = new AssistantConversation(1L, "hi");
        setId(conversation, 12L);
        AssistantMessage userMessage = new AssistantMessage(conversation, AssistantMessageRole.USER, "hi");
        AssistantPlan plan = new AssistantPlan(
                AssistantRetrievalType.ASSISTANT_HELP,
                com.contactcore.assistant.application.planning.AssistantIntent.GREETING,
                "hi",
                "",
                "",
                "",
                20,
                List.of(),
                "GREETING"
        );
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(AssistantRetrievalType.ASSISTANT_HELP, "GREETING", List.of());

        when(inputGuard.normalizeAndValidate("hi")).thenReturn("hi");
        when(persistenceService.saveUserMessage(1L, null, "hi", "hi"))
                .thenReturn(new AssistantPersistenceService.AssistantUserMessagePersistResult(conversation, userMessage));
        when(queryPlanner.plan("hi")).thenReturn(plan);
        when(evidenceGate.assess(plan, retrieval)).thenReturn(new AssistantEvidence(retrieval, true, "no evidence required"));
        when(answerGenerationService.generate(org.mockito.Mockito.eq(plan), org.mockito.Mockito.eq(retrieval), org.mockito.Mockito.eq("hi"), any(AssistantLocaleContext.class)))
                .thenReturn(AssistantAnswerGenerationResult.success(AssistantAnswerSource.DETERMINISTIC, "Hi. I can help with CRM questions.", "contactcore-deterministic"));
        AssistantMessage assistantMessage = new AssistantMessage(conversation, AssistantMessageRole.ASSISTANT, "Hi. I can help with CRM questions.");
        when(persistenceService.saveAssistantMessage(1L, 12L, "Hi. I can help with CRM questions.", List.of())).thenReturn(assistantMessage);

        AssistantResponse response = service.sendMessage(1L, new AssistantRequest(null, "hi"));

        assertThat(response.answer()).contains("Hi");
        assertThat(response.references()).isEmpty();
        verifyNoInteractions(toolOrchestrator);
        verify(persistenceService).saveAssistantMessage(1L, 12L, "Hi. I can help with CRM questions.", List.of());
    }

    @Test
    void rejectsWhenAssistantIsDisabled() {
        AssistantApplicationService service = service(new AssistantProperties(false, "noop", "test", "", "", 5000, 12000, 20, 10));

        assertThatThrownBy(() -> service.sendMessage(1L, new AssistantRequest(null, "Summary")))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    void persistsAssistantReferencesOnResponseMessage() {
        AssistantProperties properties = new AssistantProperties(true, "noop", "test-model", "", "", 5000, 12000, 20, 10);
        AssistantApplicationService service = service(properties);
        AssistantConversation conversation = new AssistantConversation(1L, "Find Meyer");
        setId(conversation, 11L);
        AssistantMessage userMessage = new AssistantMessage(conversation, AssistantMessageRole.USER, "Find Meyer");
        AssistantPlan plan = new AssistantPlan(AssistantRetrievalType.CRM_SEARCH, "Find Meyer", 20);
        AssistantRecordReference reference = new AssistantRecordReference("BUSINESS_PARTNER", 9L, "LED-009 - Meyer", "/leads/9");
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(
                AssistantRetrievalType.CRM_SEARCH,
                List.of(AssistantSearchResult.of(reference, new LinkedHashMap<>()))
        );

        when(inputGuard.normalizeAndValidate("Find Meyer")).thenReturn("Find Meyer");
        when(persistenceService.saveUserMessage(1L, null, "Find Meyer", "Find Meyer"))
                .thenReturn(new AssistantPersistenceService.AssistantUserMessagePersistResult(conversation, userMessage));
        when(queryPlanner.plan("Find Meyer")).thenReturn(plan);
        when(toolOrchestrator.execute(1L, plan)).thenReturn(retrieval);
        when(evidenceGate.assess(plan, retrieval)).thenReturn(new AssistantEvidence(retrieval, false, "test"));
        when(answerGenerationService.generate(org.mockito.Mockito.eq(plan), org.mockito.Mockito.eq(retrieval), org.mockito.Mockito.eq("Find Meyer"), any(AssistantLocaleContext.class)))
                .thenReturn(AssistantAnswerGenerationResult.success(AssistantAnswerSource.LLM, "Answer", "test-model"));
        AssistantMessage assistantMessage = new AssistantMessage(conversation, AssistantMessageRole.ASSISTANT, "Answer");
        when(persistenceService.saveAssistantMessage(org.mockito.Mockito.eq(1L), org.mockito.Mockito.eq(11L), org.mockito.Mockito.eq("Answer"), any())).thenReturn(assistantMessage);
        ArgumentCaptor<List<AssistantRecordReference>> referencesCaptor = ArgumentCaptor.forClass(List.class);

        service.sendMessage(1L, new AssistantRequest(null, "Find Meyer"));

        verify(persistenceService).saveAssistantMessage(org.mockito.Mockito.eq(1L), org.mockito.Mockito.eq(11L), org.mockito.Mockito.eq("Answer"), referencesCaptor.capture());
        assertThat(referencesCaptor.getValue()).hasSize(1);
        assertThat(referencesCaptor.getValue().getFirst().route()).isEqualTo("/leads/9");
    }

    private AssistantApplicationService service(AssistantProperties properties) {
        LocaleContext selectedLocale = new LocaleContext(SupportedLocale.EN);
        AssistantLocaleContext assistantLocale = new AssistantLocaleContext(selectedLocale);
        org.mockito.Mockito.lenient().when(localeContextResolver.resolveForUser(1L)).thenReturn(selectedLocale);
        org.mockito.Mockito.lenient().when(assistantLocaleContextResolver.resolve(org.mockito.Mockito.anyString(), org.mockito.Mockito.any(LocaleContext.class))).thenReturn(assistantLocale);
        return new AssistantApplicationService(
                properties,
                conversations,
                messages,
                inputGuard,
                rateLimitService,
                queryPlanner,
                toolOrchestrator,
                evidenceGate,
                answerGenerationService,
                auditService,
                persistenceService,
                localeContextResolver,
                assistantLocaleContextResolver
        );
    }

    private void setId(AssistantConversation conversation, Long id) {
        try {
            java.lang.reflect.Field idField = com.contactcore.shared.domain.BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(conversation, id);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
