// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.application;

import com.contactcore.crm.api.ContactPersonResponse;
import com.contactcore.crm.api.ContactPersonWriteRequest;
import com.contactcore.crm.domain.BusinessPartner;
import com.contactcore.crm.domain.BusinessPartnerContactPerson;
import com.contactcore.crm.domain.BusinessPartnerContactPersonRepository;
import com.contactcore.shared.api.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactPersonService {
    private final BusinessPartnerService businessPartners;
    private final BusinessPartnerContactPersonRepository contactPersons;

    public ContactPersonService(BusinessPartnerService businessPartners, BusinessPartnerContactPersonRepository contactPersons) {
        this.businessPartners = businessPartners;
        this.contactPersons = contactPersons;
    }

    @Transactional(readOnly = true)
    public List<ContactPersonResponse> list(Long businessPartnerId) {
        businessPartners.findActive(businessPartnerId);
        return contactPersons.findActiveByBusinessPartnerId(businessPartnerId).stream()
                .map(ContactPersonService::toResponse)
                .toList();
    }

    @Transactional
    public ContactPersonResponse create(Long businessPartnerId, ContactPersonWriteRequest request) {
        BusinessPartner partner = businessPartners.findActive(businessPartnerId);
        NormalizedContactPersonInput input = ContactPersonNormalizer.normalize(request);
        BusinessPartnerContactPerson person = new BusinessPartnerContactPerson(
                input.firstName(), input.lastName(), input.roleTitle(), input.email(), input.phone(), input.mobile(),
                input.department(), input.primaryContact(), input.notes()
        );
        if (input.primaryContact()) {
            clearOtherPrimaryContacts(businessPartnerId, null);
        }
        partner.addContactPerson(person);
        return toResponse(contactPersons.save(person));
    }

    @Transactional
    public ContactPersonResponse update(Long businessPartnerId, Long id, ContactPersonWriteRequest request) {
        BusinessPartnerContactPerson person = findActiveForPartner(businessPartnerId, id);
        NormalizedContactPersonInput input = ContactPersonNormalizer.normalize(request);
        if (input.primaryContact()) {
            clearOtherPrimaryContacts(businessPartnerId, id);
        }
        person.update(
                input.firstName(), input.lastName(), input.roleTitle(), input.email(), input.phone(), input.mobile(),
                input.department(), input.primaryContact(), input.notes()
        );
        return toResponse(person);
    }

    @Transactional
    public void archive(Long businessPartnerId, Long id) {
        findActiveForPartner(businessPartnerId, id).archive();
    }

    private BusinessPartnerContactPerson findActiveForPartner(Long businessPartnerId, Long id) {
        return contactPersons.findActiveForPartner(businessPartnerId, id)
                .orElseThrow(() -> new NotFoundException("Contact person not found: " + id));
    }

    private void clearOtherPrimaryContacts(Long businessPartnerId, Long keepId) {
        contactPersons.findPrimaryContacts(businessPartnerId).stream()
                .filter(person -> keepId == null || !person.getId().equals(keepId))
                .forEach(person -> person.setPrimaryContact(false));
    }

    static ContactPersonResponse toResponse(BusinessPartnerContactPerson person) {
        return new ContactPersonResponse(
                person.getId(),
                person.getBusinessPartner().getId(),
                person.getVersion(),
                person.getCreatedAt(),
                person.getUpdatedAt(),
                person.getFirstName(),
                person.getLastName(),
                person.displayName(),
                person.getRoleTitle(),
                person.getEmail(),
                person.getPhone(),
                person.getMobile(),
                person.getDepartment(),
                person.isPrimaryContact(),
                person.getNotes()
        );
    }
}
