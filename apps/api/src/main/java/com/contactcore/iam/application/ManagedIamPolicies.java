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
import com.contactcore.iam.security.IamManagementActions;
import com.contactcore.iam.security.IamManagementResources;
import com.contactcore.schema.security.SchemaIamActions;
import com.contactcore.schema.security.SchemaIamResources;
import java.util.List;

public final class ManagedIamPolicies {
    public static final String CONTACTCORE_ADMINISTRATOR = "CONTACTCORE_ADMINISTRATOR";
    public static final String CONTACTCORE_READ_ONLY = "CONTACTCORE_READ_ONLY";
    public static final String SALES_USER = "SALES_USER";
    public static final String SALES_MANAGER = "SALES_MANAGER";
    public static final String CONNECTOR_ADMINISTRATOR = "CONNECTOR_ADMINISTRATOR";
    public static final String IAM_ADMINISTRATOR = "IAM_ADMINISTRATOR";

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

    public static IamPolicyDocument contactCoreReadOnly(String tenantId) {
        return new IamPolicyDocument(IamPolicyDocument.CURRENT_VERSION, List.of(
                IamPolicyStatement.allow(
                        "AllowReadOnlyApplicationAccess",
                        List.of(
                                SchemaIamActions.READ_MANIFEST,
                                CrmIamActions.LIST_BUSINESS_PARTNERS,
                                CrmIamActions.READ_BUSINESS_PARTNER,
                                CommercialIamActions.LIST_DOCUMENTS,
                                CommercialIamActions.READ_DOCUMENT,
                                CommercialIamActions.LIST_ITEMS,
                                CommercialIamActions.READ_ITEM,
                                ConnectorIamActions.READ,
                                AssistantIamActions.ASK,
                                AssistantIamActions.USE_COMMERCIAL_TOOLS
                        ),
                        List.of(
                                SchemaIamResources.manifest(tenantId),
                                CrmIamResources.businessPartners(tenantId),
                                CommercialIamResources.documents(tenantId),
                                CommercialIamResources.items(tenantId),
                                ConnectorIamResources.instances(tenantId),
                                ConnectorIamResources.sessions(tenantId),
                                AssistantIamResources.sessions(tenantId),
                                AssistantIamResources.commercialTools(tenantId)
                        )
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

    public static IamPolicyDocument salesManager(String tenantId) {
        return new IamPolicyDocument(IamPolicyDocument.CURRENT_VERSION, List.of(
                IamPolicyStatement.allow(
                        "AllowSalesManagement",
                        List.of(
                                SchemaIamActions.READ_MANIFEST,
                                CrmIamActions.LIST_BUSINESS_PARTNERS,
                                CrmIamActions.READ_BUSINESS_PARTNER,
                                CrmIamActions.CREATE_BUSINESS_PARTNER,
                                CrmIamActions.UPDATE_BUSINESS_PARTNER,
                                CrmIamActions.EXPORT_BUSINESS_PARTNERS,
                                CommercialIamActions.LIST_DOCUMENTS,
                                CommercialIamActions.READ_DOCUMENT,
                                CommercialIamActions.EXPORT_DOCUMENTS,
                                CommercialIamActions.LIST_ITEMS,
                                CommercialIamActions.READ_ITEM,
                                AssistantIamActions.ASK,
                                AssistantIamActions.USE_COMMERCIAL_TOOLS
                        ),
                        List.of(
                                SchemaIamResources.manifest(tenantId),
                                CrmIamResources.businessPartners(tenantId),
                                CommercialIamResources.documents(tenantId),
                                CommercialIamResources.items(tenantId),
                                AssistantIamResources.sessions(tenantId),
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
                                ConnectorIamActions.CONNECT_SESSION,
                                ConnectorIamActions.DISCONNECT_SESSION,
                                ConnectorIamActions.READ_BUSINESS_PARTNERS,
                                ConnectorIamActions.CONFIGURE,
                                ConnectorIamActions.START_SYNC,
                                CommercialIamActions.SYNC_DOCUMENTS,
                                CommercialIamActions.SYNC_ITEMS
                        ),
                        List.of(
                                ConnectorIamResources.instances(tenantId),
                                ConnectorIamResources.sessions(tenantId),
                                ConnectorIamResources.businessPartners(tenantId),
                                CommercialIamResources.documents(tenantId),
                                CommercialIamResources.items(tenantId)
                        )
                )
        ));
    }

    public static IamPolicyDocument iamAdministrator(String tenantId) {
        return new IamPolicyDocument(IamPolicyDocument.CURRENT_VERSION, List.of(
                IamPolicyStatement.allow(
                        "AllowIamAdministration",
                        List.of(
                                IamManagementActions.READ_POLICY,
                                IamManagementActions.MANAGE_POLICY,
                                IamManagementActions.MANAGE_ROLE
                        ),
                        List.of(
                                IamManagementResources.policies(tenantId),
                                IamManagementResources.roles(tenantId)
                        )
                )
        ));
    }
}
