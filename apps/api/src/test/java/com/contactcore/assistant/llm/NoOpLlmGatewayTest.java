// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.llm;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import org.junit.jupiter.api.Test;

class NoOpLlmGatewayTest {
    @Test
    void returnsDeterministicAnswerForTestsAndLocalDevelopment() {
        NoOpLlmGateway gateway = new NoOpLlmGateway();

        LlmResponse response = gateway.complete(new LlmRequest(
                "test",
                AssistantRetrievalType.LEADS_WITHOUT_CONTACTS,
                "system",
                "context",
                "question"
        ));

        assertThat(response.modelName()).isEqualTo("noop-assistant");
        assertThat(response.content()).contains("no active contact person");
    }
}
