// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.assistant.application.AssistantLocaleContext;
import com.contactcore.shared.localization.LocaleContext;
import com.contactcore.shared.localization.SupportedLocale;
import com.contactcore.shared.localization.TextDirection;
import org.junit.jupiter.api.Test;

class AssistantLocaleContextResolverTest {
    private final AssistantLocaleContextResolver resolver = new AssistantLocaleContextResolver(new RuleBasedAssistantLanguageDetector());

    @Test
    void userMessageLanguageWinsOverSelectedLocaleWhenDetectedConfidently() {
        AssistantLocaleContext context = resolver.resolve("Show me customer Meyer", new LocaleContext(SupportedLocale.DE));

        assertThat(context.selectedLocale()).isEqualTo(SupportedLocale.DE);
        assertThat(context.detectedInputLocale()).isEqualTo(SupportedLocale.EN);
        assertThat(context.responseLocale()).isEqualTo(SupportedLocale.EN);
        assertThat(context.direction()).isEqualTo(TextDirection.LTR);
        assertThat(context.decisionSource()).isEqualTo(AssistantLocaleDecisionSource.USER_MESSAGE_LANGUAGE);
    }

    @Test
    void germanMessageWinsOverEnglishSelectedLocale() {
        AssistantLocaleContext context = resolver.resolve("Zeige mir den Kunden Meyer", new LocaleContext(SupportedLocale.EN));

        assertThat(context.detectedInputLocale()).isEqualTo(SupportedLocale.DE);
        assertThat(context.responseLocale()).isEqualTo(SupportedLocale.DE);
        assertThat(context.decisionSource()).isEqualTo(AssistantLocaleDecisionSource.USER_MESSAGE_LANGUAGE);
    }

    @Test
    void arabicMessageUsesRtlResponseDirection() {
        AssistantLocaleContext context = resolver.resolve("اعرض العميل Meyer", new LocaleContext(SupportedLocale.EN));

        assertThat(context.detectedInputLocale()).isEqualTo(SupportedLocale.AR);
        assertThat(context.responseLocale()).isEqualTo(SupportedLocale.AR);
        assertThat(context.direction()).isEqualTo(TextDirection.RTL);
        assertThat(context.htmlDirection()).isEqualTo("rtl");
    }

    @Test
    void unclearMessageFallsBackToSelectedLocale() {
        AssistantLocaleContext context = resolver.resolve("Meyer C10001 SAP", new LocaleContext(SupportedLocale.AR));

        assertThat(context.detectedInputLocale()).isNull();
        assertThat(context.responseLocale()).isEqualTo(SupportedLocale.AR);
        assertThat(context.decisionSource()).isEqualTo(AssistantLocaleDecisionSource.SELECTED_LOCALE_FALLBACK);
    }
}
