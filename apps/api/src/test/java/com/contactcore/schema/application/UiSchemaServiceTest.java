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
                new UiResourceCapabilities(UiResourceKeys.ASSISTANT_SESSION, Map.of(UiCapabilityKeys.ASK, true)),
                new UiResourceCapabilities(UiResourceKeys.DASHBOARD_COMMERCIAL, Map.of(UiCapabilityKeys.READ, true)),
                new UiResourceCapabilities(UiResourceKeys.DASHBOARD_COMMERCIAL_FINANCIALS, Map.of(UiCapabilityKeys.READ, true))
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
                .contains("/dashboard", "/dashboard/commercial", "/marketing-sources", "/reports", "/settings");
        assertThat(manifest.routes()).anySatisfy(route -> {
            assertThat(route.path()).isEqualTo("/dashboard/commercial");
            assertThat(route.screenKey()).isEqualTo("commercialDashboard");
            assertThat(route.requiredCapability().resourceKey()).isEqualTo(UiResourceKeys.DASHBOARD_COMMERCIAL);
            assertThat(route.visible()).isTrue();
        });
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
            assertThat(route.visible()).isFalse();
        });
    }

    @Test
    void dashboardScreenUsesSchemaDrivenLayoutWidgets() {
        var screen = service.screen("dashboard");

        assertThat(screen.layout()).isNotNull();
        assertThat(screen.layout().type()).isEqualTo("dashboard");
        assertThat(screen.layout().sections()).extracting(section -> section.key())
                .containsExactly("overview", "crmInsights", "relationships", "recent");
        assertThat(screen.layout().sections()).flatExtracting(section -> section.widgets())
                .anySatisfy(widget -> {
                    assertThat(widget.key()).isEqualTo("overviewKpis");
                    assertThat(widget.type()).isEqualTo("kpiGrid");
                    assertThat(widget.dataSource().key()).isEqualTo("analytics.dashboard");
                    assertThat(widget.dataPath()).isEqualTo("kpis");
                })
                .anySatisfy(widget -> {
                    assertThat(widget.key()).isEqualTo("newByMonth");
                    assertThat(widget.type()).isEqualTo("lineChart");
                    assertThat(widget.bindings()).containsEntry("label", "month");
                })
                .anySatisfy(widget -> {
                    assertThat(widget.key()).isEqualTo("recentBusinessPartners");
                    assertThat(widget.type()).isEqualTo("table");
                    assertThat(widget.tableColumns()).extracting(column -> column.key())
                            .containsExactly("kind", "code", "name", "status", "marketingSource");
                });
    }


    @Test
    void commercialDashboardScreenUsesCommercialDataSourcesAndFinancialWidgetVisibility() {
        var screen = service.screen("commercialDashboard");

        assertThat(screen.layout()).isNotNull();
        assertThat(screen.capabilities().resourceKey()).isEqualTo(UiResourceKeys.DASHBOARD_COMMERCIAL);
        assertThat(screen.layout().sections()).extracting(section -> section.key())
                .containsExactly("commercialOverview", "salesPerformance", "commercialRankings", "commercialReceivables");
        assertThat(screen.layout().sections()).flatExtracting(section -> section.widgets())
                .anySatisfy(widget -> {
                    assertThat(widget.key()).isEqualTo("commercialSummaryKpis");
                    assertThat(widget.dataSource().key()).isEqualTo("commercialDashboard.summary");
                    assertThat(widget.requiredCapability().resourceKey()).isEqualTo(UiResourceKeys.DASHBOARD_COMMERCIAL_FINANCIALS);
                    assertThat(widget.visible()).isTrue();
                })
                .anySatisfy(widget -> {
                    assertThat(widget.key()).isEqualTo("commercialTopSellingItems");
                    assertThat(widget.type()).isEqualTo("barChart");
                    assertThat(widget.bindings()).containsEntry("label", "itemName").containsEntry("value", "netAmount");
                })
                .anySatisfy(widget -> {
                    assertThat(widget.key()).isEqualTo("commercialUnpaidInvoices");
                    assertThat(widget.type()).isEqualTo("table");
                    assertThat(widget.tableColumns()).extracting(column -> column.key())
                            .containsExactly("businessPartnerName", "businessPartnerCode", "openAmount", "invoiceCount", "oldestDueDate", "maxOverdueDays");
                });
    }

    @Test
    void commercialDashboardHidesFinancialWidgetsWithoutFinancialPermission() {
        when(capabilityResolver.resolveCurrentSubjectCapabilities()).thenReturn(new UiCapabilitySnapshot(List.of(
                new UiResourceCapabilities(UiResourceKeys.DASHBOARD_COMMERCIAL, Map.of(UiCapabilityKeys.READ, true)),
                new UiResourceCapabilities(UiResourceKeys.DASHBOARD_COMMERCIAL_FINANCIALS, Map.of(UiCapabilityKeys.READ, false))
        )));

        var screen = service.screen("commercialDashboard");

        assertThat(screen.layout().sections()).flatExtracting(section -> section.widgets())
                .filteredOn(widget -> UiResourceKeys.DASHBOARD_COMMERCIAL_FINANCIALS.equals(widget.requiredCapability() == null ? null : widget.requiredCapability().resourceKey()))
                .allSatisfy(widget -> assertThat(widget.visible()).isFalse());
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
