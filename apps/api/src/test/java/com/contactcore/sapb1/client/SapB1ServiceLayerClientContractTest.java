// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

class SapB1ServiceLayerClientContractTest {
    @Test
    void serviceLayerClientIsPureTransportPortAndNotSpringBean() {
        assertThat(SapB1ServiceLayerClient.class.isInterface()).isTrue();
        assertThat(SapB1ServiceLayerClient.class.isAnnotationPresent(Component.class)).isFalse();
        assertThat(SapB1ServiceLayerClient.class.isAnnotationPresent(Service.class)).isFalse();
    }

    @Test
    void defaultImplementationIsSpringManagedAdapterForPort() {
        assertThat(SapB1ServiceLayerClient.class).isAssignableFrom(DefaultSapB1ServiceLayerClient.class);
        assertThat(DefaultSapB1ServiceLayerClient.class.isAnnotationPresent(Component.class)).isTrue();
        assertThat(Modifier.isAbstract(DefaultSapB1ServiceLayerClient.class.getModifiers())).isFalse();
    }

    @Test
    void transportPortRemainsResourceNeutral() {
        assertThat(resourceSpecificMethodNames(SapB1ServiceLayerClient.class)).isEmpty();
    }

    @Test
    void defaultImplementationRemainsResourceNeutral() {
        assertThat(resourceSpecificMethodNames(DefaultSapB1ServiceLayerClient.class)).isEmpty();
    }

    private static java.util.List<String> resourceSpecificMethodNames(Class<?> type) {
        return Arrays.stream(type.getDeclaredMethods())
                .map(Method::getName)
                .filter(name -> {
                    String normalized = name.toLowerCase();
                    return normalized.contains("businesspartner")
                            || normalized.contains("cardcode")
                            || normalized.contains("invoice")
                            || normalized.contains("salesorder")
                            || normalized.contains("purchaseorder")
                            || normalized.contains("item");
                })
                .toList();
    }
}
