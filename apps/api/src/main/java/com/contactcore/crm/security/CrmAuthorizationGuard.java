// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.security;

import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import com.contactcore.iam.evaluation.IamRequestContext;
import com.contactcore.shared.api.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class CrmAuthorizationGuard {
    private final CurrentIamAuthorizationService authorization;
    private final ContactCoreTenantContext tenantContext;

    public CrmAuthorizationGuard(CurrentIamAuthorizationService authorization, ContactCoreTenantContext tenantContext) {
        this.authorization = authorization;
        this.tenantContext = tenantContext;
    }

    public void requireListBusinessPartners(BusinessPartnerAuthorizationContext context) {
        authorization.requireAllowed(
                CrmIamActions.LIST_BUSINESS_PARTNERS,
                CrmIamResources.businessPartners(currentTenant()),
                safe(context).toIamContext()
        );
    }

    public void requireReadBusinessPartner(Long businessPartnerId) {
        authorization.requireAllowed(
                CrmIamActions.READ_BUSINESS_PARTNER,
                CrmIamResources.businessPartner(currentTenant(), requireId(businessPartnerId, "businessPartnerId"))
        );
    }

    public void requireCreateBusinessPartner(BusinessPartnerAuthorizationContext context) {
        authorization.requireAllowed(
                CrmIamActions.CREATE_BUSINESS_PARTNER,
                CrmIamResources.businessPartners(currentTenant()),
                safe(context).toIamContext()
        );
    }

    public void requireUpdateBusinessPartner(Long businessPartnerId, BusinessPartnerAuthorizationContext context) {
        authorization.requireAllowed(
                CrmIamActions.UPDATE_BUSINESS_PARTNER,
                CrmIamResources.businessPartner(currentTenant(), requireId(businessPartnerId, "businessPartnerId")),
                safe(context).toIamContext()
        );
    }

    public void requireUpdateBusinessPartner(Long businessPartnerId) {
        requireUpdateBusinessPartner(businessPartnerId, BusinessPartnerAuthorizationContext.forOperation("update"));
    }

    public void requireDeleteBusinessPartner(Long businessPartnerId) {
        authorization.requireAllowed(
                CrmIamActions.DELETE_BUSINESS_PARTNER,
                CrmIamResources.businessPartner(currentTenant(), requireId(businessPartnerId, "businessPartnerId")),
                BusinessPartnerAuthorizationContext.forOperation("archive").toIamContext()
        );
    }

    public void requireExportBusinessPartners(BusinessPartnerAuthorizationContext context) {
        authorization.requireAllowed(
                CrmIamActions.EXPORT_BUSINESS_PARTNERS,
                CrmIamResources.businessPartners(currentTenant()),
                safe(context).toIamContext()
        );
    }

    public void requireManageBusinessPartnerDocuments(Long businessPartnerId, String operation) {
        requireUpdateBusinessPartner(businessPartnerId, BusinessPartnerAuthorizationContext.forOperation(operation));
    }

    public void requireManageBusinessPartnerDocuments(String operation) {
        authorization.requireAllowed(
                CrmIamActions.UPDATE_BUSINESS_PARTNER,
                CrmIamResources.businessPartners(currentTenant()),
                BusinessPartnerAuthorizationContext.forOperation(operation).toIamContext()
        );
    }

    private String currentTenant() {
        return tenantContext.currentTenantId();
    }

    private BusinessPartnerAuthorizationContext safe(BusinessPartnerAuthorizationContext context) {
        return context == null ? BusinessPartnerAuthorizationContext.empty() : context;
    }

    private Long requireId(Long id, String fieldName) {
        if (id == null) {
            throw new InvalidRequestException(fieldName + " must not be null");
        }
        return id;
    }
}
