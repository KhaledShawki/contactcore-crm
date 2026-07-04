// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import com.contactcore.assistant.application.i18n.AssistantLanguageDetectionResult;
import com.contactcore.assistant.application.i18n.AssistantLocaleDecisionSource;
import com.contactcore.shared.localization.LocaleContext;
import com.contactcore.shared.localization.SupportedLocale;
import com.contactcore.shared.localization.TextDirection;

public record AssistantLocaleContext(
        SupportedLocale selectedLocale,
        SupportedLocale detectedInputLocale,
        SupportedLocale responseLocale,
        TextDirection direction,
        AssistantLocaleDecisionSource decisionSource,
        double detectionConfidence
) {
    public AssistantLocaleContext {
        selectedLocale = selectedLocale == null ? SupportedLocale.DEFAULT : selectedLocale;
        responseLocale = responseLocale == null ? selectedLocale : responseLocale;
        direction = direction == null ? responseLocale.direction() : direction;
        decisionSource = decisionSource == null ? AssistantLocaleDecisionSource.DEFAULT_FALLBACK : decisionSource;
        detectionConfidence = Math.max(0.0d, Math.min(1.0d, detectionConfidence));
    }

    public AssistantLocaleContext(LocaleContext context) {
        this(context == null ? SupportedLocale.DEFAULT : context.locale(),
                null,
                context == null ? SupportedLocale.DEFAULT : context.locale(),
                context == null ? SupportedLocale.DEFAULT.direction() : context.direction(),
                context == null ? AssistantLocaleDecisionSource.DEFAULT_FALLBACK : AssistantLocaleDecisionSource.SELECTED_LOCALE_FALLBACK,
                0.0d);
    }

    public AssistantLocaleContext(SupportedLocale locale, String ignoredLanguageName, TextDirection direction) {
        this(locale,
                null,
                locale == null ? SupportedLocale.DEFAULT : locale,
                direction == null ? (locale == null ? SupportedLocale.DEFAULT : locale).direction() : direction,
                AssistantLocaleDecisionSource.SELECTED_LOCALE_FALLBACK,
                0.0d);
    }

    public static AssistantLocaleContext fromDetectedMessageLanguage(SupportedLocale selectedLocale,
                                                                      SupportedLocale detectedLocale,
                                                                      AssistantLanguageDetectionResult detection) {
        SupportedLocale responseLocale = detectedLocale == null ? SupportedLocale.DEFAULT : detectedLocale;
        return new AssistantLocaleContext(
                selectedLocale,
                detectedLocale,
                responseLocale,
                responseLocale.direction(),
                AssistantLocaleDecisionSource.USER_MESSAGE_LANGUAGE,
                detection == null ? 0.0d : detection.confidence()
        );
    }

    public static AssistantLocaleContext fromSelectedLocaleFallback(SupportedLocale selectedLocale,
                                                                    AssistantLanguageDetectionResult detection) {
        SupportedLocale fallback = selectedLocale == null ? SupportedLocale.DEFAULT : selectedLocale;
        return new AssistantLocaleContext(
                fallback,
                detection == null ? null : detection.locale(),
                fallback,
                fallback.direction(),
                AssistantLocaleDecisionSource.SELECTED_LOCALE_FALLBACK,
                detection == null ? 0.0d : detection.confidence()
        );
    }

    public static AssistantLocaleContext defaultFallback(AssistantLanguageDetectionResult detection) {
        return new AssistantLocaleContext(
                SupportedLocale.DEFAULT,
                detection == null ? null : detection.locale(),
                SupportedLocale.DEFAULT,
                SupportedLocale.DEFAULT.direction(),
                AssistantLocaleDecisionSource.DEFAULT_FALLBACK,
                detection == null ? 0.0d : detection.confidence()
        );
    }

    /**
     * Backwards-compatible accessor used by existing message-localization code.
     * The assistant always localizes answers with the resolved response locale.
     */
    public SupportedLocale locale() {
        return responseLocale;
    }

    public String languageName() {
        return responseLocale.languageName();
    }

    public String tag() {
        return responseLocale.tag();
    }

    public String htmlDirection() {
        return direction.htmlValue();
    }
}
