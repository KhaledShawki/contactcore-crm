// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.localization;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

class LocalizedMessageServiceTest {
    private final MessageSource messageSource = new LocaleConfiguration().messageSource();
    private final LocalizedMessageService messages = new LocalizedMessageService(messageSource);

    @Test
    void resolvesGermanMessages() {
        String message = messages.message(new LocaleContext(SupportedLocale.DE), "api.error.CONNECTOR_SESSION_REQUIRED", "fallback");
        assertThat(message).contains("CRM-Connector");
    }

    @Test
    void resolvesArabicMessages() {
        String message = messages.message(new LocaleContext(SupportedLocale.AR), "api.error.CONNECTOR_SESSION_REQUIRED", "fallback");
        assertThat(message).contains("CRM");
        assertThat(message).contains("أولاً");
    }

    @Test
    void usesFallbackForMissingKeys() {
        assertThat(messages.message(new LocaleContext(SupportedLocale.DE), "missing.key", "fallback")).isEqualTo("fallback");
    }
}
