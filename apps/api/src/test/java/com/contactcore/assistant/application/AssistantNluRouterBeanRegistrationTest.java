// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.assistant.application.nlu.AssistantConceptAliasRegistry;
import com.contactcore.assistant.application.nlu.AssistantMessageNormalizer;
import com.contactcore.assistant.application.nlu.AssistantNluCatalogLoader;
import com.contactcore.assistant.application.nlu.AssistantNluCatalogValidator;
import com.contactcore.assistant.application.nlu.AssistantNluRouter;
import com.contactcore.assistant.application.nlu.CatalogAssistantNluRouter;
import com.contactcore.assistant.application.nlu.CompositeAssistantNluRouter;
import com.contactcore.assistant.application.nlu.NoOpStructuredAssistantNluRouter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class AssistantNluRouterBeanRegistrationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(AssistantNluCatalogValidator.class)
            .withBean(AssistantNluCatalogLoader.class)
            .withBean(AssistantConceptAliasRegistry.class)
            .withBean(AssistantMessageNormalizer.class)
            .withBean(CatalogAssistantNluRouter.class)
            .withBean(NoOpStructuredAssistantNluRouter.class)
            .withBean(CompositeAssistantNluRouter.class);

    @Test
    void registersCompositeNluRouterWithCandidateRouters() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(AssistantNluRouter.class);
            assertThat(context).hasSingleBean(CompositeAssistantNluRouter.class);
            assertThat(context.getBean(AssistantNluRouter.class))
                    .isInstanceOf(CompositeAssistantNluRouter.class);
        });
    }
}
