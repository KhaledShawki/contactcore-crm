// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.localization;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum SupportedLocale {
    EN("en", "English", TextDirection.LTR),
    DE("de", "German", TextDirection.LTR),
    AR("ar", "Arabic", TextDirection.RTL);

    public static final SupportedLocale DEFAULT = EN;

    private final String tag;
    private final String languageName;
    private final TextDirection direction;

    SupportedLocale(String tag, String languageName, TextDirection direction) {
        this.tag = tag;
        this.languageName = languageName;
        this.direction = direction;
    }

    public String tag() {
        return tag;
    }

    public String languageName() {
        return languageName;
    }

    public TextDirection direction() {
        return direction;
    }

    public Locale javaLocale() {
        return Locale.forLanguageTag(tag);
    }

    public static SupportedLocale normalizeOrDefault(String value) {
        return find(value).orElse(DEFAULT);
    }

    public static Optional<SupportedLocale> find(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        String language = Locale.forLanguageTag(value.trim().replace('_', '-')).getLanguage();
        if (language == null || language.isBlank()) {
            language = value.trim().toLowerCase(Locale.ROOT);
        }
        String normalized = language;
        return Arrays.stream(values())
                .filter(locale -> locale.tag.equalsIgnoreCase(normalized))
                .findFirst();
    }
}
