// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.answer;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.assistant.application.AssistantLocaleContext;
import com.contactcore.assistant.application.planning.AssistantIntent;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRecordReference;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import com.contactcore.assistant.retrieval.AssistantSearchResult;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.shared.localization.SupportedLocale;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DeterministicCrmAnswerLocaleTest {
    private final DeterministicCrmAnswerGenerator generator = new DeterministicCrmAnswerGenerator(new AssistantStaticResponseCatalog());

    @Test
    void composesGermanAnswerWithoutTranslatingSourceValues() {
        AssistantAnswerGenerationResult result = generator.generate(plan(), retrieval(), "Zeige mir Kunde Meyer", locale(SupportedLocale.DE));

        assertThat(result.answer()).contains("Ich habe");
        assertThat(result.answer()).contains("Meyer GmbH");
        assertThat(result.answer()).contains("C10001");
        assertThat(result.answer()).contains("Kunde");
    }

    @Test
    void composesArabicAnswerWithoutTranslatingSourceValues() {
        AssistantAnswerGenerationResult result = generator.generate(plan(), retrieval(), "اعرض العميل Meyer", locale(SupportedLocale.AR));

        assertThat(result.answer()).contains("وجدت");
        assertThat(result.answer()).contains("Meyer GmbH");
        assertThat(result.answer()).contains("C10001");
        assertThat(result.answer()).contains("عميل");
    }


    @Test
    void composesLocalizedClarificationForAmbiguousBusinessPartnerTypes() {
        AssistantPlan ambiguousPlan = new AssistantPlan(
                AssistantRetrievalType.ASSISTANT_HELP,
                AssistantIntent.UNCLEAR_REQUEST,
                "show customer supplier Meyer",
                "",
                "",
                "",
                20,
                List.of(),
                "UNCLEAR_REQUEST conflicting business partner types [CUSTOMER,SUPPLIER]"
        );

        AssistantAnswerGenerationResult german = generator.generate(ambiguousPlan, new AssistantRetrievalResult(AssistantRetrievalType.ASSISTANT_HELP, List.of()), "zeige Kunde Lieferant Meyer", locale(SupportedLocale.DE));
        AssistantAnswerGenerationResult arabic = generator.generate(ambiguousPlan, new AssistantRetrievalResult(AssistantRetrievalType.ASSISTANT_HELP, List.of()), "اعرض العميل المورد Meyer", locale(SupportedLocale.AR));

        assertThat(german.answer()).contains("Kunden", "Lieferanten", "Lead");
        assertThat(arabic.answer()).contains("عميلاً", "مورداً", "محتملاً");
    }

    private AssistantPlan plan() {
        return new AssistantPlan(
                AssistantRetrievalType.CRM_SEARCH,
                AssistantIntent.BUSINESS_PARTNER_SEARCH,
                "show me customer Meyer",
                "Meyer",
                "CUSTOMER",
                "",
                20,
                List.of(AssistantToolCall.of("crm.searchRecords", Map.of("query", "Meyer", "kindCode", "CUSTOMER"))),
                "BUSINESS_PARTNER_SEARCH for Meyer [CUSTOMER]"
        );
    }

    private AssistantRetrievalResult retrieval() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("Kind", "CUSTOMER");
        fields.put("Status", "ACTIVE");
        fields.put("External code", "C10001");
        AssistantRecordReference reference = new AssistantRecordReference("CONNECTOR_BUSINESS_PARTNER", null, "Meyer GmbH (C10001)", "/connectors/business-partners/C10001");
        return new AssistantRetrievalResult(AssistantRetrievalType.CRM_SEARCH, List.of(AssistantSearchResult.of(reference, fields)));
    }

    private AssistantLocaleContext locale(SupportedLocale locale) {
        return new AssistantLocaleContext(locale, locale.languageName(), locale.direction());
    }
}
