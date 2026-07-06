// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IamPermissionBoundaryRepository extends JpaRepository<IamPermissionBoundary, Long> {
    @EntityGraph(attributePaths = {"policy"})
    Optional<IamPermissionBoundary> findByTenantIdAndPrincipalTypeAndPrincipalIdAndArchivedAtIsNull(
            String tenantId,
            IamPrincipalType principalType,
            String principalId
    );
}
