// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class SapB1ServiceLayerClientBeanRegistrationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(ObjectMapper.class, ObjectMapper::new)
            .withBean(DefaultSapB1ServiceLayerClient.class);

    @Test
    void registersExactlyOneConcreteServiceLayerClientBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SapB1ServiceLayerClient.class);
            assertThat(context).hasSingleBean(DefaultSapB1ServiceLayerClient.class);
            assertThat(context.getBean(SapB1ServiceLayerClient.class))
                    .isInstanceOf(DefaultSapB1ServiceLayerClient.class);
        });
    }
}
