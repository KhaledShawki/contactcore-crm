// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamManagedPolicy;
import com.contactcore.iam.domain.IamPermissionBoundary;
import com.contactcore.iam.domain.IamPermissionBoundaryRepository;
import com.contactcore.iam.domain.IamPolicyDocument;
import com.contactcore.iam.domain.IamPolicyStatement;
import com.contactcore.iam.domain.IamPolicyVersion;
import com.contactcore.iam.domain.IamPolicyVersionRepository;
import com.contactcore.iam.domain.IamPrincipalPolicyAttachment;
import com.contactcore.iam.domain.IamPrincipalPolicyAttachmentRepository;
import com.contactcore.iam.domain.IamPrincipalRef;
import com.contactcore.iam.domain.IamResource;
import com.contactcore.iam.domain.IamRolePolicyAttachment;
import com.contactcore.iam.domain.IamRolePolicyAttachmentRepository;
import com.contactcore.security.domain.SecurityRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class PersistentIamPolicyResolverTest {
    private final IamRolePolicyAttachmentRepository rolePolicyAttachments = mock(IamRolePolicyAttachmentRepository.class);
    private final IamPrincipalPolicyAttachmentRepository principalPolicyAttachments = mock(IamPrincipalPolicyAttachmentRepository.class);
    private final IamPermissionBoundaryRepository permissionBoundaries = mock(IamPermissionBoundaryRepository.class);
    private final IamPolicyVersionRepository policyVersions = mock(IamPolicyVersionRepository.class);
    private final IamPolicyDocumentSerializer serializer = new IamPolicyDocumentSerializer(new ObjectMapper());
    private final PersistentIamPolicyResolver resolver = new PersistentIamPolicyResolver(
            rolePolicyAttachments,
            principalPolicyAttachments,
            permissionBoundaries,
            policyVersions,
            serializer
    );

    @Test
    void resolvesRolePrincipalAndBoundaryPoliciesFromPersistence() {
        IamPrincipalRef principal = IamPrincipalRef.user(42L);
        SecurityRole role = role("SALES_USER", 10L);
        IamManagedPolicy rolePolicy = policy("SALES_USER", 100L);
        IamManagedPolicy principalPolicy = policy("EXTRA_READ", 101L);
        IamManagedPolicy boundaryPolicy = policy("READ_BOUNDARY", 102L);

        when(rolePolicyAttachments.findActiveForRoleCodes(eq("default"), anyCollection()))
                .thenReturn(List.of(new IamRolePolicyAttachment("default", role, rolePolicy)));
        when(principalPolicyAttachments.findByTenantIdAndPrincipalTypeAndPrincipalIdAndArchivedAtIsNull(
                "default",
                principal.type(),
                principal.id()
        )).thenReturn(List.of(new IamPrincipalPolicyAttachment("default", principal, principalPolicy)));
        when(permissionBoundaries.findByTenantIdAndPrincipalTypeAndPrincipalIdAndArchivedAtIsNull(
                "default",
                principal.type(),
                principal.id()
        )).thenReturn(Optional.of(new IamPermissionBoundary("default", principal, boundaryPolicy)));
        when(policyVersions.findDefaultVersionsForPolicies(List.of(100L, 101L)))
                .thenReturn(List.of(version(rolePolicy), version(principalPolicy)));
        when(policyVersions.findDefaultVersionsForPolicies(List.of(102L)))
                .thenReturn(List.of(version(boundaryPolicy)));

        IamResolvedPolicies resolved = resolver.resolve(principal, List.of("ROLE_SALES_USER"), "default");

        assertThat(resolved.identityPolicies()).hasSize(2);
        assertThat(resolved.permissionBoundaryPolicies()).hasSize(1);
        assertThat(resolved.sessionPolicies()).isEmpty();
    }

    private SecurityRole role(String code, Long id) {
        SecurityRole role = new SecurityRole(code, code);
        ReflectionTestUtils.setField(role, "id", id);
        return role;
    }

    private IamManagedPolicy policy(String code, Long id) {
        IamManagedPolicy policy = new IamManagedPolicy(code, code, code, true);
        ReflectionTestUtils.setField(policy, "id", id);
        return policy;
    }

    private IamPolicyVersion version(IamManagedPolicy policy) {
        IamPolicyDocument document = new IamPolicyDocument(IamPolicyDocument.CURRENT_VERSION, List.of(
                IamPolicyStatement.allow(
                        "Allow" + policy.getCode(),
                        List.of(IamAction.of("commercial:ReadDocument")),
                        List.of(IamResource.of("contactcore:default:commercial:document/*"))
                )
        ));
        IamPolicyVersion version = new IamPolicyVersion(policy, 1, serializer.serialize(document), IamPrincipalRef.system("test"));
        version.markDefault();
        return version;
    }
}
