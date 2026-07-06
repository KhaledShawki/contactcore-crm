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
import com.contactcore.storage.security.StorageIamActions;
import com.contactcore.storage.security.StorageIamResources;
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
                                StorageIamActions.READ_OBJECT,
                                StorageIamActions.DOWNLOAD_OBJECT,
                                AssistantIamActions.ASK,
                                AssistantIamActions.READ_CONVERSATIONS,
                                AssistantIamActions.USE_CRM_TOOLS,
                                AssistantIamActions.USE_COMMERCIAL_TOOLS
                        ),
                        List.of(
                                SchemaIamResources.manifest(tenantId),
                                CrmIamResources.businessPartners(tenantId),
                                CommercialIamResources.documents(tenantId),
                                CommercialIamResources.items(tenantId),
                                ConnectorIamResources.instances(tenantId),
                                ConnectorIamResources.sessions(tenantId),
                                StorageIamResources.businessPartnerDocuments(tenantId),
                                StorageIamResources.profileImages(tenantId),
                                AssistantIamResources.sessions(tenantId),
                                AssistantIamResources.conversations(tenantId),
                                AssistantIamResources.crmTools(tenantId),
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
                                StorageIamActions.READ_OBJECT,
                                StorageIamActions.UPLOAD_OBJECT,
                                StorageIamActions.DOWNLOAD_OBJECT,
                                StorageIamActions.DELETE_OBJECT,
                                AssistantIamActions.ASK,
                                AssistantIamActions.READ_CONVERSATIONS,
                                AssistantIamActions.ARCHIVE_CONVERSATION,
                                AssistantIamActions.USE_CRM_TOOLS
                        ),
                        List.of(
                                SchemaIamResources.manifest(tenantId),
                                CrmIamResources.businessPartners(tenantId),
                                StorageIamResources.businessPartnerDocuments(tenantId),
                                StorageIamResources.profileImages(tenantId),
                                AssistantIamResources.sessions(tenantId),
                                AssistantIamResources.conversations(tenantId),
                                AssistantIamResources.crmTools(tenantId)
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
                                StorageIamActions.READ_OBJECT,
                                StorageIamActions.UPLOAD_OBJECT,
                                StorageIamActions.DOWNLOAD_OBJECT,
                                StorageIamActions.DELETE_OBJECT,
                                AssistantIamActions.ASK,
                                AssistantIamActions.READ_CONVERSATIONS,
                                AssistantIamActions.ARCHIVE_CONVERSATION,
                                AssistantIamActions.USE_CRM_TOOLS,
                                AssistantIamActions.USE_COMMERCIAL_TOOLS,
                                AssistantIamActions.USE_REPORT_TOOLS
                        ),
                        List.of(
                                SchemaIamResources.manifest(tenantId),
                                CrmIamResources.businessPartners(tenantId),
                                CommercialIamResources.documents(tenantId),
                                CommercialIamResources.items(tenantId),
                                StorageIamResources.businessPartnerDocuments(tenantId),
                                StorageIamResources.profileImages(tenantId),
                                AssistantIamResources.sessions(tenantId),
                                AssistantIamResources.conversations(tenantId),
                                AssistantIamResources.crmTools(tenantId),
                                AssistantIamResources.commercialTools(tenantId),
                                AssistantIamResources.reportTools(tenantId)
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
                                CommercialIamActions.SYNC_ITEMS,
                                AssistantIamActions.USE_CONNECTOR_TOOLS
                        ),
                        List.of(
                                ConnectorIamResources.instances(tenantId),
                                ConnectorIamResources.sessions(tenantId),
                                ConnectorIamResources.businessPartners(tenantId),
                                CommercialIamResources.documents(tenantId),
                                CommercialIamResources.items(tenantId),
                                AssistantIamResources.connectorTools(tenantId)
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
