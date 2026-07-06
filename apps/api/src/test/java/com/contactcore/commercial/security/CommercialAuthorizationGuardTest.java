// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.commercial.domain.CommercialDocumentStatus;
import com.contactcore.commercial.domain.CommercialDocumentType;
import com.contactcore.commercial.domain.CommercialSourceSystem;
import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import com.contactcore.iam.domain.IamResource;
import com.contactcore.iam.evaluation.IamRequestContext;
import com.contactcore.shared.api.InvalidRequestException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CommercialAuthorizationGuardTest {
    private final CurrentIamAuthorizationService authorization = mock(CurrentIamAuthorizationService.class);
    private final ContactCoreTenantContext tenantContext = mock(ContactCoreTenantContext.class);
    private final CommercialAuthorizationGuard guard = new CommercialAuthorizationGuard(authorization, tenantContext);

    @BeforeEach
    void setUp() {
        when(tenantContext.currentTenantId()).thenReturn("tenant-1");
    }

    @Test
    void requiresListDocumentsWithTenantAwareCollectionResourceAndContext() {
        guard.requireListDocuments(new CommercialDocumentAuthorizationContext(
                42L,
                CommercialDocumentType.SALES_ORDER,
                CommercialDocumentStatus.OPEN,
                CommercialSourceSystem.SAP_B1,
                LocalDate.parse("2026-07-01"),
                LocalDate.parse("2026-07-31")
        ));

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(CommercialIamActions.LIST_DOCUMENTS),
                eq(IamResource.of("contactcore:tenant-1:commercial:document/*")),
                context.capture()
        );
        assertThat(context.getValue().get("businessPartnerId")).isEqualTo(42L);
        assertThat(context.getValue().get("documentType")).isEqualTo("SALES_ORDER");
        assertThat(context.getValue().get("documentStatus")).isEqualTo("OPEN");
        assertThat(context.getValue().get("sourceSystem")).isEqualTo("SAP_B1");
    }

    @Test
    void requiresReadDocumentWithSpecificTenantAwareResource() {
        guard.requireReadDocument(99L);

        verify(authorization).requireAllowed(
                CommercialIamActions.READ_DOCUMENT,
                IamResource.of("contactcore:tenant-1:commercial:document/99")
        );
    }

    @Test
    void requiresListItemsWithContext() {
        guard.requireListItems(new ItemAuthorizationContext(CommercialSourceSystem.EXTERNAL, true));

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(CommercialIamActions.LIST_ITEMS),
                eq(IamResource.of("contactcore:tenant-1:commercial:item/*")),
                context.capture()
        );
        assertThat(context.getValue().get("sourceSystem")).isEqualTo("EXTERNAL");
        assertThat(context.getValue().get("active")).isEqualTo(true);
    }

    @Test
    void rejectsNullSpecificResourceIdentifiers() {
        assertThatThrownBy(() -> guard.requireReadDocument(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("documentId");
        assertThatThrownBy(() -> guard.requireReadItem(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("itemId");
    }
}
