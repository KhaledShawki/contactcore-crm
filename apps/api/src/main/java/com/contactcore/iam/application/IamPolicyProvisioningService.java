// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamManagedPolicy;
import com.contactcore.iam.domain.IamManagedPolicyRepository;
import com.contactcore.iam.domain.IamPolicyDocument;
import com.contactcore.iam.domain.IamPolicyVersion;
import com.contactcore.iam.domain.IamPolicyVersionRepository;
import com.contactcore.iam.domain.IamPrincipalRef;
import com.contactcore.iam.domain.IamRolePolicyAttachment;
import com.contactcore.iam.domain.IamRolePolicyAttachmentRepository;
import com.contactcore.security.domain.SecurityRole;
import com.contactcore.security.domain.SecurityRoleRepository;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IamPolicyProvisioningService {
    private final IamManagedPolicyRepository policies;
    private final IamPolicyVersionRepository policyVersions;
    private final IamRolePolicyAttachmentRepository rolePolicyAttachments;
    private final SecurityRoleRepository securityRoles;
    private final IamPolicyDocumentSerializer serializer;

    public IamPolicyProvisioningService(IamManagedPolicyRepository policies, IamPolicyVersionRepository policyVersions,
                                        IamRolePolicyAttachmentRepository rolePolicyAttachments,
                                        SecurityRoleRepository securityRoles,
                                        IamPolicyDocumentSerializer serializer) {
        this.policies = policies;
        this.policyVersions = policyVersions;
        this.rolePolicyAttachments = rolePolicyAttachments;
        this.securityRoles = securityRoles;
        this.serializer = serializer;
    }

    @Transactional
    public IamManagedPolicy upsertManagedPolicy(String code, String name, String description, IamPolicyDocument document,
                                                IamPrincipalRef actor) {
        String normalizedCode = normalizeCode(code);
        IamManagedPolicy policy = policies.findByCodeIgnoreCase(normalizedCode)
                .orElseGet(() -> new IamManagedPolicy(normalizedCode, name, description, true));
        policy.refresh(name, description, true);
        IamManagedPolicy savedPolicy = policies.save(policy);
        upsertDefaultVersion(savedPolicy, document, actor);
        return savedPolicy;
    }

    @Transactional
    public void attachPolicyToRole(String tenantId, String roleCode, String roleName, String policyCode) {
        IamManagedPolicy policy = policies.findByCodeIgnoreCase(policyCode)
                .orElseThrow(() -> new IllegalStateException("IAM managed policy not found: " + policyCode));
        SecurityRole role = securityRoles.findByCodeIgnoreCase(roleCode)
                .orElseGet(() -> securityRoles.save(new SecurityRole(normalizeRoleCode(roleCode), roleName)));
        if (!rolePolicyAttachments.existsByTenantIdAndRole_IdAndPolicy_IdAndArchivedAtIsNull(tenantId, role.getId(), policy.getId())) {
            rolePolicyAttachments.save(new IamRolePolicyAttachment(tenantId, role, policy));
        }
    }

    private void upsertDefaultVersion(IamManagedPolicy policy, IamPolicyDocument document, IamPrincipalRef actor) {
        String documentJson = serializer.serialize(Objects.requireNonNull(document, "document must not be null"));
        var currentDefault = policyVersions.findByPolicy_IdAndDefaultVersionTrueAndArchivedAtIsNull(policy.getId());
        if (currentDefault.map(IamPolicyVersion::getDocumentJson).filter(documentJson::equals).isPresent()) {
            return;
        }
        currentDefault.ifPresent(IamPolicyVersion::clearDefault);
        int nextVersion = policyVersions.findTopByPolicy_IdAndArchivedAtIsNullOrderByVersionNumberDesc(policy.getId())
                .map(version -> version.getVersionNumber() + 1)
                .orElse(1);
        IamPolicyVersion version = new IamPolicyVersion(policy, nextVersion, documentJson, actor);
        version.markDefault();
        policyVersions.save(version);
    }

    private static String normalizeCode(String code) {
        return require(code, "code").toUpperCase(Locale.ROOT).replace('-', '_');
    }

    private static String normalizeRoleCode(String roleCode) {
        return require(roleCode, "roleCode").toUpperCase(Locale.ROOT).replace('-', '_');
    }

    private static String require(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
