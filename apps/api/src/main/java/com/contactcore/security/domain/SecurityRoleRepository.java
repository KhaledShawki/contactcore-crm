// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityRoleRepository extends JpaRepository<SecurityRole, Long> {
    Optional<SecurityRole> findByCodeIgnoreCase(String code);
}
