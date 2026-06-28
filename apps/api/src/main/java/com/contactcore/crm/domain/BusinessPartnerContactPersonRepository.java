// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BusinessPartnerContactPersonRepository extends JpaRepository<BusinessPartnerContactPerson, Long> {
    @Query("""
            select person from BusinessPartnerContactPerson person
            where person.archivedAt is null and person.businessPartner.id = :businessPartnerId
            order by person.primaryContact desc, lower(person.lastName) asc, lower(person.firstName) asc, person.id asc
            """)
    List<BusinessPartnerContactPerson> findActiveByBusinessPartnerId(@Param("businessPartnerId") Long businessPartnerId);

    @Query("""
            select person from BusinessPartnerContactPerson person
            where person.archivedAt is null
              and person.id = :id
              and person.businessPartner.id = :businessPartnerId
            """)
    Optional<BusinessPartnerContactPerson> findActiveForPartner(@Param("businessPartnerId") Long businessPartnerId, @Param("id") Long id);

    @Query("""
            select person from BusinessPartnerContactPerson person
            where person.archivedAt is null
              and person.businessPartner.id = :businessPartnerId
              and person.primaryContact = true
            """)
    List<BusinessPartnerContactPerson> findPrimaryContacts(@Param("businessPartnerId") Long businessPartnerId);
}
