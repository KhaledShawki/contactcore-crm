// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.assistant.application.nlu.AssistantNluDecisionSource;
import com.contactcore.assistant.application.nlu.AssistantNluRequest;
import com.contactcore.assistant.application.nlu.AssistantNluRouter;
import com.contactcore.assistant.application.nlu.CompositeAssistantNluRouter;
import com.contactcore.assistant.application.nlu.AssistantMessageNormalizer;
import org.junit.jupiter.api.Test;

class AssistantNluRouterTest {
    private final AssistantNluRouter router = new CompositeAssistantNluRouter(new AssistantMessageNormalizer());

    @Test
    void routesKnownGermanAndArabicPhrasesThroughCatalog() {
        var german = router.route(new AssistantNluRequest("Zeige Kunde Meyer"));
        var arabic = router.route(new AssistantNluRequest("أي العملاء المحتملين لا يملكون جهات اتصال؟"));

        assertThat(german.source()).isEqualTo(AssistantNluDecisionSource.CATALOG);
        assertThat(german.normalizedMessage().canonicalText()).isEqualTo("show me customer Meyer");
        assertThat(arabic.source()).isEqualTo(AssistantNluDecisionSource.CATALOG);
        assertThat(arabic.normalizedMessage().canonicalText()).contains("lead", "without", "contact persons");
    }
}
