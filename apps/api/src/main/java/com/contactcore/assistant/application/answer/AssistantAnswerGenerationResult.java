// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.answer;

public record AssistantAnswerGenerationResult(
        AssistantAnswerStatus status,
        AssistantAnswerSource source,
        String answer,
        String modelName,
        String warning,
        String failureReason
) {
    public AssistantAnswerGenerationResult {
        if (status == null) {
            throw new IllegalArgumentException("Answer status is required.");
        }
        if (source == null) {
            throw new IllegalArgumentException("Answer source is required.");
        }
        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("Answer content is required.");
        }
        modelName = modelName == null || modelName.isBlank() ? "unknown" : modelName.trim();
        warning = warning == null || warning.isBlank() ? null : warning.trim();
        failureReason = failureReason == null || failureReason.isBlank() ? null : failureReason.trim();
    }

    public static AssistantAnswerGenerationResult success(AssistantAnswerSource source, String answer, String modelName) {
        return new AssistantAnswerGenerationResult(AssistantAnswerStatus.SUCCESS, source, answer, modelName, null, null);
    }

    public static AssistantAnswerGenerationResult degraded(String answer, String modelName, String warning, String failureReason) {
        return new AssistantAnswerGenerationResult(AssistantAnswerStatus.DEGRADED, AssistantAnswerSource.FALLBACK, answer, modelName, warning, failureReason);
    }
}
