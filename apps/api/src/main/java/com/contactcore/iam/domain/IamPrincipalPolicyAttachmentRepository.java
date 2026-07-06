// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IamPrincipalPolicyAttachmentRepository extends JpaRepository<IamPrincipalPolicyAttachment, Long> {
    boolean existsByTenantIdAndPrincipalTypeAndPrincipalIdAndPolicy_IdAndArchivedAtIsNull(
            String tenantId,
            IamPrincipalType principalType,
            String principalId,
            Long policyId
    );

    @EntityGraph(attributePaths = {"policy"})
    Optional<IamPrincipalPolicyAttachment> findByTenantIdAndPrincipalTypeAndPrincipalIdAndPolicy_IdAndArchivedAtIsNull(
            String tenantId,
            IamPrincipalType principalType,
            String principalId,
            Long policyId
    );

    @EntityGraph(attributePaths = {"policy"})
    List<IamPrincipalPolicyAttachment> findByTenantIdAndPrincipalTypeAndPrincipalIdAndArchivedAtIsNull(
            String tenantId,
            IamPrincipalType principalType,
            String principalId
    );
}
