// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.assistant.application.nlu.AssistantConcept;
import com.contactcore.assistant.application.nlu.AssistantNluCatalogLoader;
import org.junit.jupiter.api.Test;

class AssistantNluCatalogLoaderTest {
    @Test
    void loadsVersionedCatalogFromResourceFiles() {
        var catalog = AssistantNluCatalogLoader.loadDefaultCatalog();

        assertThat(catalog.version()).isEqualTo(1);
        assertThat(catalog.ignoredTokens()).contains("der", "die", "ال");
        assertThat(catalog.aliases()).anySatisfy(entry -> {
            assertThat(entry.concept()).isEqualTo(AssistantConcept.LEAD);
            assertThat(entry.canonicalText()).isEqualTo("lead");
            assertThat(entry.phrases()).contains("العميل المحتمل", "lead");
        });
    }

    @Test
    void keepsUserLanguageMappingsOutOfTheRegistryImplementation() {
        var catalog = AssistantNluCatalogLoader.loadDefaultCatalog();

        assertThat(catalog.aliases())
                .flatExtracting(entry -> entry.phrases())
                .contains("kunde", "عميل", "لا يملكون", "contact persons");
    }
}
