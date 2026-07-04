// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.localization;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

@Service
public class LocalizedMessageService {
    private final MessageSource messages;

    public LocalizedMessageService(MessageSource messages) {
        this.messages = messages;
    }

    public String message(LocaleContext locale, String key, String fallback, Object... args) {
        Locale javaLocale = (locale == null ? SupportedLocale.DEFAULT : locale.locale()).javaLocale();
        try {
            return messages.getMessage(key, args, javaLocale);
        } catch (NoSuchMessageException ignored) {
            return fallback == null ? key : fallback;
        }
    }
}
