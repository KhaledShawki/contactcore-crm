// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.security;

import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import com.contactcore.shared.api.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class CommercialAuthorizationGuard {
    private final CurrentIamAuthorizationService authorization;
    private final ContactCoreTenantContext tenantContext;

    public CommercialAuthorizationGuard(CurrentIamAuthorizationService authorization, ContactCoreTenantContext tenantContext) {
        this.authorization = authorization;
        this.tenantContext = tenantContext;
    }

    public void requireListDocuments(CommercialDocumentAuthorizationContext context) {
        authorization.requireAllowed(
                CommercialIamActions.LIST_DOCUMENTS,
                CommercialIamResources.documents(currentTenant()),
                safe(context).toIamContext()
        );
    }

    public void requireReadDocument(Long documentId) {
        authorization.requireAllowed(
                CommercialIamActions.READ_DOCUMENT,
                CommercialIamResources.document(currentTenant(), requireId(documentId, "documentId"))
        );
    }

    public void requireListItems(ItemAuthorizationContext context) {
        authorization.requireAllowed(
                CommercialIamActions.LIST_ITEMS,
                CommercialIamResources.items(currentTenant()),
                safe(context).toIamContext()
        );
    }

    public void requireReadItem(Long itemId) {
        authorization.requireAllowed(
                CommercialIamActions.READ_ITEM,
                CommercialIamResources.item(currentTenant(), requireId(itemId, "itemId"))
        );
    }

    private String currentTenant() {
        return tenantContext.currentTenantId();
    }

    private Long requireId(Long id, String fieldName) {
        if (id == null) {
            throw new InvalidRequestException(fieldName + " must not be null");
        }
        return id;
    }

    private CommercialDocumentAuthorizationContext safe(CommercialDocumentAuthorizationContext context) {
        return context == null ? new CommercialDocumentAuthorizationContext(null, null, null, null, null, null) : context;
    }

    private ItemAuthorizationContext safe(ItemAuthorizationContext context) {
        return context == null ? new ItemAuthorizationContext(null, null) : context;
    }
}
