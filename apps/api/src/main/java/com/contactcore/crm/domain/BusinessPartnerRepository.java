// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BusinessPartnerRepository extends JpaRepository<BusinessPartner, Long> {
    @Query("""
            select partner from BusinessPartner partner
            where partner.archivedAt is null and partner.id = :id
            """)
    Optional<BusinessPartner> findActiveById(@Param("id") Long id);

    @Query("""
            select partner from BusinessPartner partner
            where partner.archivedAt is null and upper(partner.code) = upper(:code)
            """)
    Optional<BusinessPartner> findActiveByCode(@Param("code") String code);
}
