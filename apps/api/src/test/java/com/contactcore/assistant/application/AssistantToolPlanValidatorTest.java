// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.contactcore.assistant.application.planning.AssistantIntent;
import com.contactcore.assistant.application.planning.AssistantPlanValidationException;
import com.contactcore.assistant.application.planning.AssistantToolPlanValidator;
import com.contactcore.assistant.tool.AssistantToolCall;
import java.util.List;
import org.junit.jupiter.api.Test;

class AssistantToolPlanValidatorTest {
    private final AssistantToolPlanValidator validator = new AssistantToolPlanValidator();


    @Test
    void allowsEmptyPlanForGreetingIntent() {
        assertThatCode(() -> validator.validate(AssistantIntent.GREETING, List.of()))
                .doesNotThrowAnyException();
    }

    @Test
    void allowsEmptyPlanForUnclearIntent() {
        assertThatCode(() -> validator.validate(AssistantIntent.UNCLEAR_REQUEST, List.of()))
                .doesNotThrowAnyException();
    }

    @Test
    void allowsEmptyPlanForIdentityIntent() {
        assertThatCode(() -> validator.validate(AssistantIntent.ASSISTANT_IDENTITY, List.of()))
                .doesNotThrowAnyException();
    }

    @Test
    void allowsEmptyPlanForCapabilitiesIntent() {
        assertThatCode(() -> validator.validate(AssistantIntent.ASSISTANT_CAPABILITIES, List.of()))
                .doesNotThrowAnyException();
    }

    @Test
    void allowsEmptyPlanForUnsupportedIntent() {
        assertThatCode(() -> validator.validate(AssistantIntent.UNSUPPORTED, List.of()))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsCrmToolsForIdentityIntent() {
        assertThatThrownBy(() -> validator.validate(
                AssistantIntent.ASSISTANT_IDENTITY,
                List.of(AssistantToolCall.of("crm.getCrmSummary"))
        )).isInstanceOf(AssistantPlanValidationException.class)
                .hasMessageContaining("must not execute CRM tools");
    }

    @Test
    void rejectsCrmToolsForUnclearIntent() {
        assertThatThrownBy(() -> validator.validate(
                AssistantIntent.UNCLEAR_REQUEST,
                List.of(AssistantToolCall.of("crm.getCrmSummary"))
        )).isInstanceOf(AssistantPlanValidationException.class)
                .hasMessageContaining("must not execute CRM tools");
    }

    @Test
    void allowsSearchToolForExistenceIntent() {
        assertThatCode(() -> validator.validate(
                AssistantIntent.BUSINESS_PARTNER_EXISTENCE,
                List.of(AssistantToolCall.of("crm.searchRecords"))
        )).doesNotThrowAnyException();
    }

    @Test
    void rejectsAggregateToolForExistenceIntent() {
        assertThatThrownBy(() -> validator.validate(
                AssistantIntent.BUSINESS_PARTNER_EXISTENCE,
                List.of(AssistantToolCall.of("crm.getCrmSummary"))
        )).isInstanceOf(AssistantPlanValidationException.class)
                .hasMessageContaining("not valid");
    }
}
