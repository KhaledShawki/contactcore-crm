// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.localization;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SupportedLocaleTest {
    @Test
    void normalizesSupportedLanguageTags() {
        assertThat(SupportedLocale.normalizeOrDefault("de-DE")).isEqualTo(SupportedLocale.DE);
        assertThat(SupportedLocale.normalizeOrDefault("ar_EG")).isEqualTo(SupportedLocale.AR);
        assertThat(SupportedLocale.normalizeOrDefault("en-US")).isEqualTo(SupportedLocale.EN);
    }

    @Test
    void fallsBackToEnglishForUnsupportedLocales() {
        assertThat(SupportedLocale.normalizeOrDefault("fr-FR")).isEqualTo(SupportedLocale.EN);
    }

    @Test
    void marksArabicAsRtl() {
        assertThat(SupportedLocale.AR.direction()).isEqualTo(TextDirection.RTL);
        assertThat(SupportedLocale.DE.direction()).isEqualTo(TextDirection.LTR);
    }
}
