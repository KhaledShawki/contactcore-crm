// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamPolicyDocument;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class SecurityRolePolicyResolver {
    public List<IamPolicyDocument> resolve(Collection<String> roleCodes, String tenantId) {
        Set<String> normalizedRoles = new LinkedHashSet<>();
        if (roleCodes != null) {
            roleCodes.stream()
                    .filter(role -> role != null && !role.isBlank())
                    .map(role -> role.replaceFirst("^ROLE_", ""))
                    .map(role -> role.toUpperCase(Locale.ROOT))
                    .forEach(normalizedRoles::add);
        }
        if (normalizedRoles.contains("ADMIN")) {
            return List.of(ManagedIamPolicies.contactCoreAdministrator(tenantId));
        }
        if (normalizedRoles.contains("CONNECTOR_ADMIN")) {
            return List.of(
                    ManagedIamPolicies.salesUser(tenantId),
                    ManagedIamPolicies.connectorAdministrator(tenantId)
            );
        }
        if (normalizedRoles.contains("SALES_MANAGER") || normalizedRoles.contains("SALES_USER") || normalizedRoles.contains("USER")) {
            return List.of(ManagedIamPolicies.salesUser(tenantId));
        }
        return List.of();
    }
}
