// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.application;

import com.contactcore.crm.api.BusinessPartnerResponse;
import com.contactcore.crm.api.ContactMethodResponse;
import com.contactcore.crm.api.ContactPersonResponse;
import com.contactcore.crm.domain.BusinessPartner;
import com.contactcore.crm.domain.BusinessPartnerAddress;
import com.contactcore.crm.domain.BusinessPartnerContactMethod;
import com.contactcore.crm.domain.BusinessPartnerContactPerson;
import java.util.Comparator;
import java.util.List;

final class BusinessPartnerMapper {
    private BusinessPartnerMapper() {}

    static BusinessPartnerResponse toResponse(BusinessPartner entity) {
        BusinessPartnerAddress address = entity.activePrimaryAddress().orElse(null);
        List<ContactMethodResponse> methods = entity.getContactMethods().stream()
                .filter(method -> !method.isArchived())
                .sorted(Comparator.comparing(method -> method.getType().getCode()))
                .map(BusinessPartnerMapper::toContactMethodResponse)
                .toList();
        List<ContactPersonResponse> persons = entity.getContactPersons().stream()
                .filter(person -> !person.isArchived())
                .sorted(Comparator.comparing(BusinessPartnerContactPerson::isPrimaryContact).reversed()
                        .thenComparing(person -> person.getLastName().toLowerCase())
                        .thenComparing(person -> person.getFirstName().toLowerCase()))
                .map(ContactPersonService::toResponse)
                .toList();
        return new BusinessPartnerResponse(
                entity.getId(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getKind().getCode(),
                entity.getStatus().getCode(),
                entity.getStatus().getName(),
                entity.getLeadSource() == null ? null : entity.getLeadSource().getCode(),
                entity.getCode(),
                entity.getName(),
                primaryContact(entity, "EMAIL"),
                primaryContact(entity, "PHONE"),
                primaryContact(entity, "WEBSITE"),
                address == null ? null : address.getAddress().getLine1(),
                address == null ? null : address.getAddress().getLine2(),
                address == null ? null : address.getAddress().getCity(),
                address == null ? null : address.getAddress().getPostalCode(),
                address == null ? null : address.getAddress().getCountryCode(),
                entity.getNotes(),
                methods,
                persons
        );
    }

    private static ContactMethodResponse toContactMethodResponse(BusinessPartnerContactMethod method) {
        return new ContactMethodResponse(method.getType().getCode(), method.getLabel(), method.getValue(), method.isPrimaryContact());
    }

    private static String primaryContact(BusinessPartner entity, String typeCode) {
        return entity.activePrimaryContact(typeCode).map(BusinessPartnerContactMethod::getValue).orElse(null);
    }
}
