// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.assistant.security.AssistantIamActions;
import com.contactcore.assistant.security.AssistantIamResources;
import com.contactcore.commercial.security.CommercialIamActions;
import com.contactcore.commercial.security.CommercialIamResources;
import com.contactcore.connector.security.ConnectorIamActions;
import com.contactcore.connector.security.ConnectorIamResources;
import com.contactcore.crm.security.CrmIamActions;
import com.contactcore.crm.security.CrmIamResources;
import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamPolicyDocument;
import com.contactcore.iam.domain.IamPolicyStatement;
import com.contactcore.iam.domain.IamResource;
import com.contactcore.schema.security.SchemaIamActions;
import com.contactcore.schema.security.SchemaIamResources;
import java.util.List;

public final class ManagedIamPolicies {
    private ManagedIamPolicies() {}

    public static IamPolicyDocument contactCoreAdministrator(String tenantId) {
        return new IamPolicyDocument(IamPolicyDocument.CURRENT_VERSION, List.of(
                IamPolicyStatement.allow(
                        "AllowContactCoreAdministration",
                        List.of(IamAction.of("*")),
                        List.of(IamResource.of("contactcore:%s:*".formatted(tenantId)))
                )
        ));
    }

    public static IamPolicyDocument salesUser(String tenantId) {
        return new IamPolicyDocument(IamPolicyDocument.CURRENT_VERSION, List.of(
                IamPolicyStatement.allow(
                        "AllowSalesCrmReadWrite",
                        List.of(
                                SchemaIamActions.READ_MANIFEST,
                                CrmIamActions.LIST_BUSINESS_PARTNERS,
                                CrmIamActions.READ_BUSINESS_PARTNER,
                                CrmIamActions.CREATE_BUSINESS_PARTNER,
                                CrmIamActions.UPDATE_BUSINESS_PARTNER,
                                AssistantIamActions.ASK
                        ),
                        List.of(
                                SchemaIamResources.manifest(tenantId),
                                CrmIamResources.businessPartners(tenantId),
                                AssistantIamResources.sessions(tenantId)
                        )
                ),
                IamPolicyStatement.allow(
                        "AllowCommercialRead",
                        List.of(
                                CommercialIamActions.LIST_DOCUMENTS,
                                CommercialIamActions.READ_DOCUMENT,
                                CommercialIamActions.LIST_ITEMS,
                                CommercialIamActions.READ_ITEM,
                                AssistantIamActions.USE_COMMERCIAL_TOOLS
                        ),
                        List.of(
                                CommercialIamResources.documents(tenantId),
                                CommercialIamResources.items(tenantId),
                                AssistantIamResources.commercialTools(tenantId)
                        )
                )
        ));
    }

    public static IamPolicyDocument connectorAdministrator(String tenantId) {
        return new IamPolicyDocument(IamPolicyDocument.CURRENT_VERSION, List.of(
                IamPolicyStatement.allow(
                        "AllowConnectorAdministration",
                        List.of(
                                ConnectorIamActions.READ,
                                ConnectorIamActions.CONFIGURE,
                                ConnectorIamActions.START_SYNC,
                                CommercialIamActions.SYNC_DOCUMENTS,
                                CommercialIamActions.SYNC_ITEMS
                        ),
                        List.of(
                                ConnectorIamResources.instances(tenantId),
                                CommercialIamResources.documents(tenantId),
                                CommercialIamResources.items(tenantId)
                        )
                )
        ));
    }
}
