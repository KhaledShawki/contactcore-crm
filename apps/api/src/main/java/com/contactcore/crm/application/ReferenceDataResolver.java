// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.application;

import com.contactcore.crm.domain.BusinessPartnerKindRef;
import com.contactcore.crm.domain.BusinessPartnerKindRepository;
import com.contactcore.crm.domain.BusinessPartnerStatusRef;
import com.contactcore.crm.domain.BusinessPartnerStatusRepository;
import com.contactcore.crm.domain.ContactMethodType;
import com.contactcore.crm.domain.ContactMethodTypeRepository;
import com.contactcore.crm.domain.DocumentType;
import com.contactcore.crm.domain.DocumentTypeRepository;
import com.contactcore.crm.domain.LeadSource;
import com.contactcore.crm.domain.LeadSourceRepository;
import com.contactcore.shared.api.InvalidRequestException;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ReferenceDataResolver {
    private final BusinessPartnerKindRepository kinds;
    private final BusinessPartnerStatusRepository statuses;
    private final LeadSourceRepository leadSources;
    private final ContactMethodTypeRepository contactMethodTypes;
    private final DocumentTypeRepository documentTypes;

    public ReferenceDataResolver(BusinessPartnerKindRepository kinds, BusinessPartnerStatusRepository statuses,
                                 LeadSourceRepository leadSources, ContactMethodTypeRepository contactMethodTypes,
                                 DocumentTypeRepository documentTypes) {
        this.kinds = kinds;
        this.statuses = statuses;
        this.leadSources = leadSources;
        this.contactMethodTypes = contactMethodTypes;
        this.documentTypes = documentTypes;
    }

    BusinessPartnerKindRef kind(String code) {
        return kinds.findByCodeIgnoreCase(code).orElseThrow(() -> new InvalidRequestException("Unknown business partner kind: " + code));
    }

    BusinessPartnerStatusRef status(String code) {
        return statuses.findByCodeIgnoreCase(code).orElseThrow(() -> new InvalidRequestException("Unknown business partner status: " + code));
    }

    Optional<LeadSource> optionalLeadSource(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.of(leadSources.findActiveByCode(code).orElseThrow(() -> new InvalidRequestException("Unknown marketing source: " + code)));
    }

    ContactMethodType contactMethodType(String code) {
        return contactMethodTypes.findByCodeIgnoreCase(code).orElseThrow(() -> new InvalidRequestException("Unknown contact method type: " + code));
    }

    public DocumentType documentType(String code) {
        return documentTypes.findByCodeIgnoreCase(code).orElseThrow(() -> new InvalidRequestException("Unknown document type: " + code));
    }
}
