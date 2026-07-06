// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.contactcore.crm.domain.LeadSource;
import com.contactcore.crm.domain.LeadSourceRepository;
import com.contactcore.schema.api.UiResourceCapabilities;
import com.contactcore.shared.api.NotFoundException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UiSchemaServiceTest {
    private final LeadSourceRepository leadSources = mock(LeadSourceRepository.class);
    private final UiCapabilityResolver capabilityResolver = mock(UiCapabilityResolver.class);
    private final UiSchemaService service = new UiSchemaService(leadSources, capabilityResolver);

    @BeforeEach
    void setUpCapabilities() {
        when(capabilityResolver.resolveCurrentSubjectCapabilities()).thenReturn(new UiCapabilitySnapshot(List.of(
                new UiResourceCapabilities(UiResourceKeys.CRM_BUSINESS_PARTNER, Map.of(
                        UiCapabilityKeys.LIST, true,
                        UiCapabilityKeys.READ, true,
                        UiCapabilityKeys.CREATE, true,
                        UiCapabilityKeys.UPDATE, true,
                        UiCapabilityKeys.DELETE, false,
                        UiCapabilityKeys.EXPORT, false
                )),
                new UiResourceCapabilities(UiResourceKeys.ASSISTANT_SESSION, Map.of(UiCapabilityKeys.ASK, true))
        )));
    }

    @Test
    void customerScreenCarriesEntityKindDefaultsAndSchemaOnlyFields() {
        when(leadSources.findActiveOrdered()).thenReturn(List.of(new LeadSource("LINKEDIN", "LinkedIn", 10)));

        var screen = service.screen("customers");

        assertThat(screen.entityKind()).isEqualTo("CUSTOMER");
        assertThat(screen.capabilities().resourceKey()).isEqualTo(UiResourceKeys.CRM_BUSINESS_PARTNER);
        assertThat(screen.capabilities().allows(UiCapabilityKeys.CREATE)).isTrue();
        assertThat(screen.fields()).anySatisfy(field -> {
            assertThat(field.key()).isEqualTo("kind");
            assertThat(field.defaultValue()).isEqualTo("CUSTOMER");
            assertThat(field.formVisible()).isFalse();
        });
        assertThat(screen.fields()).anySatisfy(field -> {
            assertThat(field.key()).isEqualTo("statusCode");
            assertThat(field.defaultValue()).isEqualTo("ACTIVE");
            assertThat(field.options()).contains("ACTIVE", "INACTIVE");
        });
        assertThat(screen.fields()).anySatisfy(field -> {
            assertThat(field.key()).isEqualTo("sourceCode");
            assertThat(field.label()).isEqualTo("Marketing Source");
            assertThat(field.options()).contains("", "LINKEDIN");
        });
    }

    @Test
    void manifestIncludesPolicyAwareRoutesAndCapabilityMetadata() {
        var manifest = service.manifest();

        assertThat(manifest.routes()).extracting(route -> route.path())
                .contains("/dashboard", "/marketing-sources", "/reports", "/settings");
        assertThat(manifest.routes()).anySatisfy(route -> {
            assertThat(route.path()).isEqualTo("/customers");
            assertThat(route.requiredCapability().resourceKey()).isEqualTo(UiResourceKeys.CRM_BUSINESS_PARTNER);
            assertThat(route.visible()).isTrue();
        });
        assertThat(manifest.capabilities()).anySatisfy(capabilitySet -> {
            assertThat(capabilitySet.resourceKey()).isEqualTo(UiResourceKeys.CRM_BUSINESS_PARTNER);
            assertThat(capabilitySet.allows(UiCapabilityKeys.DELETE)).isFalse();
        });
    }

    @Test
    void manifestCanHideRoutesWhenRequiredCapabilityIsDenied() {
        when(capabilityResolver.resolveCurrentSubjectCapabilities()).thenReturn(new UiCapabilitySnapshot(List.of(
                new UiResourceCapabilities(UiResourceKeys.CRM_BUSINESS_PARTNER, Map.of(UiCapabilityKeys.LIST, false)),
                new UiResourceCapabilities(UiResourceKeys.ASSISTANT_SESSION, Map.of(UiCapabilityKeys.ASK, false))
        )));

        var manifest = service.manifest();

        assertThat(manifest.routes()).anySatisfy(route -> {
            assertThat(route.path()).isEqualTo("/customers");
            assertThat(route.visible()).isFalse();
        });
        assertThat(manifest.routes()).anySatisfy(route -> {
            assertThat(route.path()).isEqualTo("/dashboard");
            assertThat(route.visible()).isTrue();
        });
    }

    @Test
    void screenCarriesSchemaLevelValidationMetadata() {
        var screen = service.screen("contactPersons");

        assertThat(screen.validationRules()).anySatisfy(rule -> {
            assertThat(rule.type()).isEqualTo("atLeastOne");
            assertThat(rule.fields()).containsExactly("email", "phone", "mobile");
        });
        assertThat(screen.fields()).anySatisfy(field -> {
            assertThat(field.key()).isEqualTo("email");
            assertThat(field.validation().inputType()).isEqualTo("email");
            assertThat(field.validation().maxLength()).isEqualTo(255);
        });
        assertThat(screen.fields()).anySatisfy(field -> {
            assertThat(field.key()).isEqualTo("phone");
            assertThat(field.validation().pattern()).contains("+()0-9");
        });
    }

    @Test
    void unknownScreenFailsFast() {
        assertThatThrownBy(() -> service.screen("invoice"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("UI screen not found");
    }
}
