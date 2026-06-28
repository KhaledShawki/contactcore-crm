// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMethodTypeRepository extends JpaRepository<ContactMethodType, Long> {
    Optional<ContactMethodType> findByCodeIgnoreCase(String code);
}
