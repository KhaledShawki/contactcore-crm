// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.security;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamActionDescriptor;
import com.contactcore.iam.domain.IamActions;
import java.util.List;

public final class CrmIamActions {
    public static final String SERVICE = "crm";

    public static final IamAction LIST_BUSINESS_PARTNERS = IamActions.of(SERVICE, "ListBusinessPartners");
    public static final IamAction READ_BUSINESS_PARTNER = IamActions.of(SERVICE, "ReadBusinessPartner");
    public static final IamAction CREATE_BUSINESS_PARTNER = IamActions.of(SERVICE, "CreateBusinessPartner");
    public static final IamAction UPDATE_BUSINESS_PARTNER = IamActions.of(SERVICE, "UpdateBusinessPartner");
    public static final IamAction DELETE_BUSINESS_PARTNER = IamActions.of(SERVICE, "DeleteBusinessPartner");
    public static final IamAction EXPORT_BUSINESS_PARTNERS = IamActions.of(SERVICE, "ExportBusinessPartners");

    private static final List<IamActionDescriptor> CATALOG = List.of(
            IamActionDescriptor.read(LIST_BUSINESS_PARTNERS, "List CRM business partners"),
            IamActionDescriptor.read(READ_BUSINESS_PARTNER, "Read CRM business partner details"),
            IamActionDescriptor.write(CREATE_BUSINESS_PARTNER, "Create CRM business partners"),
            IamActionDescriptor.write(UPDATE_BUSINESS_PARTNER, "Update CRM business partners"),
            IamActionDescriptor.write(DELETE_BUSINESS_PARTNER, "Delete CRM business partners"),
            IamActionDescriptor.write(EXPORT_BUSINESS_PARTNERS, "Export CRM business partners")
    );

    private CrmIamActions() {}

    public static List<IamActionDescriptor> catalog() {
        return CATALOG;
    }
}
