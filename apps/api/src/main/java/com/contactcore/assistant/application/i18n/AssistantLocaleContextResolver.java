// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.i18n;

import com.contactcore.assistant.application.AssistantLocaleContext;
import com.contactcore.shared.localization.LocaleContext;
import com.contactcore.shared.localization.SupportedLocale;
import org.springframework.stereotype.Service;

@Service
public class AssistantLocaleContextResolver {
    private final AssistantLanguageDetector languageDetector;

    public AssistantLocaleContextResolver(AssistantLanguageDetector languageDetector) {
        this.languageDetector = languageDetector;
    }

    public AssistantLocaleContext resolve(String userMessage, LocaleContext selectedContext) {
        SupportedLocale selectedLocale = selectedContext == null ? SupportedLocale.DEFAULT : selectedContext.locale();
        AssistantLanguageDetectionResult detection = languageDetector.detect(userMessage);

        if (detection.confident()) {
            return AssistantLocaleContext.fromDetectedMessageLanguage(selectedLocale, detection.locale(), detection);
        }
        if (selectedLocale != null) {
            return AssistantLocaleContext.fromSelectedLocaleFallback(selectedLocale, detection);
        }
        return AssistantLocaleContext.defaultFallback(detection);
    }
}
