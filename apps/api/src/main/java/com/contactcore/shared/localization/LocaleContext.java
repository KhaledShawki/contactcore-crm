// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.localization;

public record LocaleContext(
        SupportedLocale locale,
        String languageName,
        TextDirection direction
) {
    public LocaleContext(SupportedLocale locale) {
        this(locale == null ? SupportedLocale.DEFAULT : locale,
                (locale == null ? SupportedLocale.DEFAULT : locale).languageName(),
                (locale == null ? SupportedLocale.DEFAULT : locale).direction());
    }

    public String tag() {
        return locale.tag();
    }

    public String htmlDirection() {
        return direction.htmlValue();
    }
}
