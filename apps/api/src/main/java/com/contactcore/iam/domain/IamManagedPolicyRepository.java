// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IamManagedPolicyRepository extends JpaRepository<IamManagedPolicy, Long> {
    Optional<IamManagedPolicy> findByCodeIgnoreCase(String code);

    List<IamManagedPolicy> findByCodeIn(Collection<String> codes);
}
