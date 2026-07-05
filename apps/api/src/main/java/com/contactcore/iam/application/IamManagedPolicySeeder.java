// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamPrincipalRef;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class IamManagedPolicySeeder implements ApplicationRunner {
    private static final IamPrincipalRef SYSTEM_ACTOR = IamPrincipalRef.system("iam-policy-seeder");

    private final IamPolicyProvisioningService provisioning;
    private final ContactCoreTenantContext tenantContext;

    public IamManagedPolicySeeder(IamPolicyProvisioningService provisioning, ContactCoreTenantContext tenantContext) {
        this.provisioning = provisioning;
        this.tenantContext = tenantContext;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String tenantId = tenantContext.currentTenantId();
        seedPolicies(tenantId);
        seedRoleAttachments(tenantId);
    }

    private void seedPolicies(String tenantId) {
        provisioning.upsertManagedPolicy(
                ManagedIamPolicies.CONTACTCORE_ADMINISTRATOR,
                "ContactCore Administrator",
                "Full ContactCore administration within the tenant product boundary.",
                ManagedIamPolicies.contactCoreAdministrator(tenantId),
                SYSTEM_ACTOR
        );
        provisioning.upsertManagedPolicy(
                ManagedIamPolicies.CONTACTCORE_READ_ONLY,
                "ContactCore Read Only",
                "Read-only access to CRM, commercial, connector, schema, and assistant context.",
                ManagedIamPolicies.contactCoreReadOnly(tenantId),
                SYSTEM_ACTOR
        );
        provisioning.upsertManagedPolicy(
                ManagedIamPolicies.SALES_USER,
                "Sales User",
                "Operational sales access for CRM maintenance and commercial visibility.",
                ManagedIamPolicies.salesUser(tenantId),
                SYSTEM_ACTOR
        );
        provisioning.upsertManagedPolicy(
                ManagedIamPolicies.SALES_MANAGER,
                "Sales Manager",
                "Sales management access including exports and commercial visibility.",
                ManagedIamPolicies.salesManager(tenantId),
                SYSTEM_ACTOR
        );
        provisioning.upsertManagedPolicy(
                ManagedIamPolicies.CONNECTOR_ADMINISTRATOR,
                "Connector Administrator",
                "Connector configuration and synchronization permissions.",
                ManagedIamPolicies.connectorAdministrator(tenantId),
                SYSTEM_ACTOR
        );
        provisioning.upsertManagedPolicy(
                ManagedIamPolicies.IAM_ADMINISTRATOR,
                "IAM Administrator",
                "IAM policy and role administration permissions.",
                ManagedIamPolicies.iamAdministrator(tenantId),
                SYSTEM_ACTOR
        );
    }

    private void seedRoleAttachments(String tenantId) {
        provisioning.attachPolicyToRole(tenantId, "ADMIN", "Administrator", ManagedIamPolicies.CONTACTCORE_ADMINISTRATOR);
        provisioning.attachPolicyToRole(tenantId, "ADMIN", "Administrator", ManagedIamPolicies.IAM_ADMINISTRATOR);
        provisioning.attachPolicyToRole(tenantId, "USER", "User", ManagedIamPolicies.SALES_USER);
        provisioning.attachPolicyToRole(tenantId, "SALES_USER", "Sales User", ManagedIamPolicies.SALES_USER);
        provisioning.attachPolicyToRole(tenantId, "SALES_MANAGER", "Sales Manager", ManagedIamPolicies.SALES_MANAGER);
        provisioning.attachPolicyToRole(tenantId, "CONNECTOR_ADMIN", "Connector Administrator", ManagedIamPolicies.SALES_USER);
        provisioning.attachPolicyToRole(tenantId, "CONNECTOR_ADMIN", "Connector Administrator", ManagedIamPolicies.CONNECTOR_ADMINISTRATOR);
    }
}
