// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.answer;

import com.contactcore.assistant.application.AssistantLocaleContext;
import com.contactcore.shared.localization.LocaleConfiguration;
import com.contactcore.shared.localization.LocaleContext;
import com.contactcore.shared.localization.LocalizedMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssistantStaticResponseCatalog {
    private final LocalizedMessageService messages;

    public AssistantStaticResponseCatalog() {
        this(new LocalizedMessageService(new LocaleConfiguration().messageSource()));
    }

    @Autowired
    public AssistantStaticResponseCatalog(LocalizedMessageService messages) {
        this.messages = messages;
    }

    public String greetingAnswer(AssistantLocaleContext locale) {
        return message(locale, "assistant.static.greeting", "Hi. I can help with read-only CRM questions based on ContactCore CRM data.");
    }

    public String identityAnswer(AssistantLocaleContext locale) {
        return message(locale, "assistant.static.identity", "I am ContactCore Assistant, a read-only CRM assistant for ContactCore.");
    }

    public String capabilitiesAnswer(AssistantLocaleContext locale) {
        return message(locale, "assistant.static.capabilities", "I can help with read-only CRM questions.");
    }

    public String unsupportedAnswer(AssistantLocaleContext locale) {
        return message(locale, "assistant.static.unsupported", "I can only answer from authorized ContactCore CRM data in read-only mode.");
    }

    public String unclearRequestAnswer(AssistantLocaleContext locale) {
        return message(locale, "assistant.static.unclear", "I could not understand the request.");
    }

    public String ambiguousBusinessPartnerTypeAnswer(AssistantLocaleContext locale) {
        return message(locale, "assistant.static.ambiguousBusinessPartnerType", "Do you mean a customer, supplier, or lead?");
    }

    public String message(AssistantLocaleContext locale, String key, String fallback, Object... args) {
        return messages.message(new LocaleContext(locale.locale(), locale.languageName(), locale.direction()), key, fallback, args);
    }
}
