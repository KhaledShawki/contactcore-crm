// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.i18n;

import com.contactcore.shared.localization.SupportedLocale;

public record AssistantLanguageDetectionResult(
        SupportedLocale locale,
        double confidence,
        String reason
) {
    private static final double CONFIDENCE_THRESHOLD = 0.65d;

    public AssistantLanguageDetectionResult {
        confidence = Math.max(0.0d, Math.min(1.0d, confidence));
        reason = reason == null ? "" : reason.trim();
    }

    public static AssistantLanguageDetectionResult unknown(String reason) {
        return new AssistantLanguageDetectionResult(null, 0.0d, reason);
    }

    public boolean confident() {
        return locale != null && confidence >= CONFIDENCE_THRESHOLD;
    }
}
