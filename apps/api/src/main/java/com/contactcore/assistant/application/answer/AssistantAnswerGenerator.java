// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.answer;

import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;

public interface AssistantAnswerGenerator {
    boolean supports(AssistantPlan plan, AssistantRetrievalResult retrieval);

    AssistantAnswerGenerationResult generate(AssistantPlan plan, AssistantRetrievalResult retrieval, String userMessage);
}
