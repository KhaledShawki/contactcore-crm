// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IamPolicyVersionRepository extends JpaRepository<IamPolicyVersion, Long> {
    Optional<IamPolicyVersion> findByPolicy_IdAndDefaultVersionTrueAndArchivedAtIsNull(Long policyId);

    Optional<IamPolicyVersion> findTopByPolicy_IdAndArchivedAtIsNullOrderByVersionNumberDesc(Long policyId);

    @Query("""
            select version from IamPolicyVersion version
            where version.archivedAt is null
              and version.defaultVersion = true
              and version.policy.archivedAt is null
              and version.policy.active = true
              and version.policy.id in :policyIds
            """)
    List<IamPolicyVersion> findDefaultVersionsForPolicies(@Param("policyIds") Collection<Long> policyIds);

    @Query("""
            select version from IamPolicyVersion version
            where version.archivedAt is null
              and version.defaultVersion = true
              and version.policy.archivedAt is null
              and version.policy.active = true
              and upper(version.policy.code) in :policyCodes
            """)
    List<IamPolicyVersion> findDefaultVersionsByPolicyCodes(@Param("policyCodes") Collection<String> policyCodes);
}
