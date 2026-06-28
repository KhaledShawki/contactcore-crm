// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessPartnerKindRepository extends JpaRepository<BusinessPartnerKindRef, Long> {
    Optional<BusinessPartnerKindRef> findByCodeIgnoreCase(String code);
}
