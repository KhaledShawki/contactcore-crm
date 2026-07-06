// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.crm.api.BusinessPartnerWriteRequest;
import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import com.contactcore.iam.domain.IamResource;
import com.contactcore.iam.evaluation.IamRequestContext;
import com.contactcore.shared.api.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CrmAuthorizationGuardTest {
    private final CurrentIamAuthorizationService authorization = mock(CurrentIamAuthorizationService.class);
    private final ContactCoreTenantContext tenantContext = mock(ContactCoreTenantContext.class);
    private final CrmAuthorizationGuard guard = new CrmAuthorizationGuard(authorization, tenantContext);

    @BeforeEach
    void setUp() {
        when(tenantContext.currentTenantId()).thenReturn("tenant-1");
    }

    @Test
    void requiresListBusinessPartnersWithTenantAwareCollectionResourceAndContext() {
        guard.requireListBusinessPartners(BusinessPartnerAuthorizationContext.forSearch("CUSTOMER", "Acme"));

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(CrmIamActions.LIST_BUSINESS_PARTNERS),
                eq(IamResource.of("contactcore:tenant-1:crm:business-partner/*")),
                context.capture()
        );
        assertThat(context.getValue().get("businessPartnerKind")).isEqualTo("CUSTOMER");
        assertThat(context.getValue().get("query")).isEqualTo("Acme");
    }

    @Test
    void requiresReadBusinessPartnerWithSpecificTenantAwareResource() {
        guard.requireReadBusinessPartner(42L);

        verify(authorization).requireAllowed(
                CrmIamActions.READ_BUSINESS_PARTNER,
                IamResource.of("contactcore:tenant-1:crm:business-partner/42")
        );
    }

    @Test
    void requiresCreateBusinessPartnerWithWriteContext() {
        BusinessPartnerWriteRequest request = new BusinessPartnerWriteRequest(
                "LEAD", "NEW", "L1000", "Acme AG", "info@example.com", null, null,
                "WEB", null, null, null, null, null, null
        );

        guard.requireCreateBusinessPartner(BusinessPartnerAuthorizationContext.forWrite(request, "create"));

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(CrmIamActions.CREATE_BUSINESS_PARTNER),
                eq(IamResource.of("contactcore:tenant-1:crm:business-partner/*")),
                context.capture()
        );
        assertThat(context.getValue().get("businessPartnerKind")).isEqualTo("LEAD");
        assertThat(context.getValue().get("businessPartnerStatus")).isEqualTo("NEW");
        assertThat(context.getValue().get("leadSourceCode")).isEqualTo("WEB");
        assertThat(context.getValue().get("operation")).isEqualTo("create");
    }

    @Test
    void requiresUpdateBusinessPartnerWithSpecificResource() {
        guard.requireUpdateBusinessPartner(42L, BusinessPartnerAuthorizationContext.forOperation("updateContactPerson"));

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(CrmIamActions.UPDATE_BUSINESS_PARTNER),
                eq(IamResource.of("contactcore:tenant-1:crm:business-partner/42")),
                context.capture()
        );
        assertThat(context.getValue().get("operation")).isEqualTo("updateContactPerson");
    }

    @Test
    void requiresExportBusinessPartnersWithCollectionResource() {
        guard.requireExportBusinessPartners(BusinessPartnerAuthorizationContext.forOperation("crmSummaryExport"));

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(CrmIamActions.EXPORT_BUSINESS_PARTNERS),
                eq(IamResource.of("contactcore:tenant-1:crm:business-partner/*")),
                context.capture()
        );
        assertThat(context.getValue().get("operation")).isEqualTo("crmSummaryExport");
    }

    @Test
    void rejectsNullSpecificResourceIdentifiers() {
        assertThatThrownBy(() -> guard.requireReadBusinessPartner(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("businessPartnerId");
        assertThatThrownBy(() -> guard.requireUpdateBusinessPartner(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("businessPartnerId");
        assertThatThrownBy(() -> guard.requireDeleteBusinessPartner(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("businessPartnerId");
    }
}
