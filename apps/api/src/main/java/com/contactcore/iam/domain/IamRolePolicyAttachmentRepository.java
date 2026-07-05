// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IamRolePolicyAttachmentRepository extends JpaRepository<IamRolePolicyAttachment, Long> {
    boolean existsByTenantIdAndRole_IdAndPolicy_IdAndArchivedAtIsNull(String tenantId, Long roleId, Long policyId);

    @EntityGraph(attributePaths = {"policy", "role"})
    Optional<IamRolePolicyAttachment> findByTenantIdAndRole_IdAndPolicy_IdAndArchivedAtIsNull(String tenantId, Long roleId, Long policyId);

    @EntityGraph(attributePaths = {"policy", "role"})
    @Query("""
            select attachment from IamRolePolicyAttachment attachment
            where attachment.archivedAt is null
              and attachment.tenantId = :tenantId
              and upper(attachment.role.code) in :roleCodes
              and attachment.role.archivedAt is null
              and attachment.policy.archivedAt is null
              and attachment.policy.active = true
            """)
    List<IamRolePolicyAttachment> findActiveForRoleCodes(
            @Param("tenantId") String tenantId,
            @Param("roleCodes") Collection<String> roleCodes
    );
}
