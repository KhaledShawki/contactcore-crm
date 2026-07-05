// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamManagedPolicy;
import com.contactcore.iam.domain.IamPermissionBoundary;
import com.contactcore.iam.domain.IamPermissionBoundaryRepository;
import com.contactcore.iam.domain.IamPolicyVersion;
import com.contactcore.iam.domain.IamPolicyVersionRepository;
import com.contactcore.iam.domain.IamPrincipalPolicyAttachmentRepository;
import com.contactcore.iam.domain.IamPrincipalRef;
import com.contactcore.iam.domain.IamRolePolicyAttachmentRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PersistentIamPolicyResolver implements IamPolicyResolver {
    private final IamRolePolicyAttachmentRepository rolePolicyAttachments;
    private final IamPrincipalPolicyAttachmentRepository principalPolicyAttachments;
    private final IamPermissionBoundaryRepository permissionBoundaries;
    private final IamPolicyVersionRepository policyVersions;
    private final IamPolicyDocumentSerializer serializer;

    public PersistentIamPolicyResolver(IamRolePolicyAttachmentRepository rolePolicyAttachments,
                                       IamPrincipalPolicyAttachmentRepository principalPolicyAttachments,
                                       IamPermissionBoundaryRepository permissionBoundaries,
                                       IamPolicyVersionRepository policyVersions,
                                       IamPolicyDocumentSerializer serializer) {
        this.rolePolicyAttachments = rolePolicyAttachments;
        this.principalPolicyAttachments = principalPolicyAttachments;
        this.permissionBoundaries = permissionBoundaries;
        this.policyVersions = policyVersions;
        this.serializer = serializer;
    }

    @Override
    @Transactional(readOnly = true)
    public IamResolvedPolicies resolve(IamPrincipalRef principal, Collection<String> roleCodes, String tenantId) {
        Set<String> normalizedRoleCodes = normalizeRoleCodes(roleCodes);
        List<IamManagedPolicy> identityPolicies = new ArrayList<>();
        if (!normalizedRoleCodes.isEmpty()) {
            rolePolicyAttachments.findActiveForRoleCodes(tenantId, normalizedRoleCodes).stream()
                    .map(attachment -> attachment.getPolicy())
                    .forEach(identityPolicies::add);
        }
        principalPolicyAttachments.findByTenantIdAndPrincipalTypeAndPrincipalIdAndArchivedAtIsNull(
                        tenantId,
                        principal.type(),
                        principal.id()
                ).stream()
                .map(attachment -> attachment.getPolicy())
                .forEach(identityPolicies::add);

        List<IamManagedPolicy> boundaryPolicies = permissionBoundaries
                .findByTenantIdAndPrincipalTypeAndPrincipalIdAndArchivedAtIsNull(tenantId, principal.type(), principal.id())
                .map(IamPermissionBoundary::getPolicy)
                .map(List::of)
                .orElse(List.of());

        return new IamResolvedPolicies(
                documentsFor(identityPolicies),
                documentsFor(boundaryPolicies),
                List.of()
        );
    }

    private List<com.contactcore.iam.domain.IamPolicyDocument> documentsFor(List<IamManagedPolicy> policies) {
        List<Long> policyIds = policies.stream()
                .filter(policy -> policy != null && policy.isActive() && !policy.isArchived())
                .map(IamManagedPolicy::getId)
                .distinct()
                .toList();
        if (policyIds.isEmpty()) {
            return List.of();
        }
        return policyVersions.findDefaultVersionsForPolicies(policyIds).stream()
                .map(IamPolicyVersion::getDocumentJson)
                .map(serializer::deserialize)
                .toList();
    }

    private Set<String> normalizeRoleCodes(Collection<String> roleCodes) {
        Set<String> normalized = new LinkedHashSet<>();
        if (roleCodes == null) {
            return normalized;
        }
        roleCodes.stream()
                .filter(role -> role != null && !role.isBlank())
                .map(role -> role.replaceFirst("^ROLE_", ""))
                .map(role -> role.toUpperCase(Locale.ROOT).replace('-', '_'))
                .forEach(normalized::add);
        return normalized;
    }
}
