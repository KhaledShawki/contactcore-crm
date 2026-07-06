// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.application;

import com.contactcore.assistant.security.AssistantIamActions;
import com.contactcore.assistant.security.AssistantIamResources;
import com.contactcore.commercial.security.CommercialIamActions;
import com.contactcore.commercial.security.CommercialIamResources;
import com.contactcore.connector.security.ConnectorIamActions;
import com.contactcore.connector.security.ConnectorIamResources;
import com.contactcore.crm.security.CrmIamActions;
import com.contactcore.crm.security.CrmIamResources;
import com.contactcore.iam.security.IamManagementActions;
import com.contactcore.iam.security.IamManagementResources;
import com.contactcore.schema.security.SchemaIamActions;
import com.contactcore.schema.security.SchemaIamResources;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UiCapabilityCatalog {
    public List<UiCapabilityDefinition> definitionsForTenant(String tenantId) {
        return List.of(
                definition(UiResourceKeys.SCHEMA_MANIFEST, UiCapabilityKeys.READ, SchemaIamActions.READ_MANIFEST, SchemaIamResources.manifest(tenantId)),

                definition(UiResourceKeys.CRM_BUSINESS_PARTNER, UiCapabilityKeys.LIST, CrmIamActions.LIST_BUSINESS_PARTNERS, CrmIamResources.businessPartners(tenantId)),
                definition(UiResourceKeys.CRM_BUSINESS_PARTNER, UiCapabilityKeys.READ, CrmIamActions.READ_BUSINESS_PARTNER, CrmIamResources.businessPartners(tenantId)),
                definition(UiResourceKeys.CRM_BUSINESS_PARTNER, UiCapabilityKeys.CREATE, CrmIamActions.CREATE_BUSINESS_PARTNER, CrmIamResources.businessPartners(tenantId)),
                definition(UiResourceKeys.CRM_BUSINESS_PARTNER, UiCapabilityKeys.UPDATE, CrmIamActions.UPDATE_BUSINESS_PARTNER, CrmIamResources.businessPartners(tenantId)),
                definition(UiResourceKeys.CRM_BUSINESS_PARTNER, UiCapabilityKeys.DELETE, CrmIamActions.DELETE_BUSINESS_PARTNER, CrmIamResources.businessPartners(tenantId)),
                definition(UiResourceKeys.CRM_BUSINESS_PARTNER, UiCapabilityKeys.EXPORT, CrmIamActions.EXPORT_BUSINESS_PARTNERS, CrmIamResources.businessPartners(tenantId)),

                definition(UiResourceKeys.COMMERCIAL_DOCUMENT, UiCapabilityKeys.LIST, CommercialIamActions.LIST_DOCUMENTS, CommercialIamResources.documents(tenantId)),
                definition(UiResourceKeys.COMMERCIAL_DOCUMENT, UiCapabilityKeys.READ, CommercialIamActions.READ_DOCUMENT, CommercialIamResources.documents(tenantId)),
                definition(UiResourceKeys.COMMERCIAL_DOCUMENT, UiCapabilityKeys.EXPORT, CommercialIamActions.EXPORT_DOCUMENTS, CommercialIamResources.documents(tenantId)),
                definition(UiResourceKeys.COMMERCIAL_DOCUMENT, UiCapabilityKeys.SYNC, CommercialIamActions.SYNC_DOCUMENTS, CommercialIamResources.documents(tenantId)),

                definition(UiResourceKeys.COMMERCIAL_ITEM, UiCapabilityKeys.LIST, CommercialIamActions.LIST_ITEMS, CommercialIamResources.items(tenantId)),
                definition(UiResourceKeys.COMMERCIAL_ITEM, UiCapabilityKeys.READ, CommercialIamActions.READ_ITEM, CommercialIamResources.items(tenantId)),
                definition(UiResourceKeys.COMMERCIAL_ITEM, UiCapabilityKeys.SYNC, CommercialIamActions.SYNC_ITEMS, CommercialIamResources.items(tenantId)),

                definition(UiResourceKeys.CONNECTOR_INSTANCE, UiCapabilityKeys.READ, ConnectorIamActions.READ, ConnectorIamResources.instances(tenantId)),
                definition(UiResourceKeys.CONNECTOR_SESSION, UiCapabilityKeys.READ, ConnectorIamActions.READ, ConnectorIamResources.sessions(tenantId)),
                definition(UiResourceKeys.CONNECTOR_SESSION, UiCapabilityKeys.CONNECT, ConnectorIamActions.CONNECT_SESSION, ConnectorIamResources.sessions(tenantId)),
                definition(UiResourceKeys.CONNECTOR_SESSION, UiCapabilityKeys.DISCONNECT, ConnectorIamActions.DISCONNECT_SESSION, ConnectorIamResources.sessions(tenantId)),
                definition(UiResourceKeys.CONNECTOR_BUSINESS_PARTNER, UiCapabilityKeys.READ, ConnectorIamActions.READ_BUSINESS_PARTNERS, ConnectorIamResources.businessPartners(tenantId)),
                definition(UiResourceKeys.CONNECTOR_INSTANCE, UiCapabilityKeys.CONFIGURE, ConnectorIamActions.CONFIGURE, ConnectorIamResources.instances(tenantId)),
                definition(UiResourceKeys.CONNECTOR_INSTANCE, UiCapabilityKeys.START_SYNC, ConnectorIamActions.START_SYNC, ConnectorIamResources.instances(tenantId)),

                definition(UiResourceKeys.ASSISTANT_SESSION, UiCapabilityKeys.ASK, AssistantIamActions.ASK, AssistantIamResources.sessions(tenantId)),
                definition(UiResourceKeys.ASSISTANT_CONVERSATION, UiCapabilityKeys.READ, AssistantIamActions.READ_CONVERSATIONS, AssistantIamResources.conversations(tenantId)),
                definition(UiResourceKeys.ASSISTANT_CONVERSATION, UiCapabilityKeys.DELETE, AssistantIamActions.ARCHIVE_CONVERSATION, AssistantIamResources.conversations(tenantId)),
                definition(UiResourceKeys.ASSISTANT_CRM_TOOLS, UiCapabilityKeys.USE, AssistantIamActions.USE_CRM_TOOLS, AssistantIamResources.crmTools(tenantId)),
                definition(UiResourceKeys.ASSISTANT_COMMERCIAL_TOOLS, UiCapabilityKeys.USE, AssistantIamActions.USE_COMMERCIAL_TOOLS, AssistantIamResources.commercialTools(tenantId)),
                definition(UiResourceKeys.ASSISTANT_CONNECTOR_TOOLS, UiCapabilityKeys.USE, AssistantIamActions.USE_CONNECTOR_TOOLS, AssistantIamResources.connectorTools(tenantId)),
                definition(UiResourceKeys.ASSISTANT_SCHEMA_TOOLS, UiCapabilityKeys.USE, AssistantIamActions.USE_SCHEMA_TOOLS, AssistantIamResources.schemaTools(tenantId)),
                definition(UiResourceKeys.ASSISTANT_REPORT_TOOLS, UiCapabilityKeys.USE, AssistantIamActions.USE_REPORT_TOOLS, AssistantIamResources.reportTools(tenantId)),

                definition(UiResourceKeys.IAM_POLICY, UiCapabilityKeys.READ, IamManagementActions.READ_POLICY, IamManagementResources.policies(tenantId)),
                definition(UiResourceKeys.IAM_POLICY, UiCapabilityKeys.MANAGE, IamManagementActions.MANAGE_POLICY, IamManagementResources.policies(tenantId)),
                definition(UiResourceKeys.IAM_ROLE, UiCapabilityKeys.MANAGE, IamManagementActions.MANAGE_ROLE, IamManagementResources.roles(tenantId))
        );
    }

    private static UiCapabilityDefinition definition(String resourceKey, String capability,
                                                     com.contactcore.iam.domain.IamAction action,
                                                     com.contactcore.iam.domain.IamResource resource) {
        return new UiCapabilityDefinition(resourceKey, capability, action, resource);
    }
}
