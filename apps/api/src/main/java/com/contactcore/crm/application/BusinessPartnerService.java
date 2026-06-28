// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.application;

import com.contactcore.crm.api.BusinessPartnerResponse;
import com.contactcore.crm.api.BusinessPartnerWriteRequest;
import com.contactcore.crm.domain.Address;
import com.contactcore.crm.domain.AddressRepository;
import com.contactcore.crm.domain.BusinessPartner;
import com.contactcore.crm.domain.BusinessPartnerAddress;
import com.contactcore.crm.domain.BusinessPartnerContactMethod;
import com.contactcore.crm.domain.BusinessPartnerRepository;
import com.contactcore.crm.domain.ContactMethodType;
import com.contactcore.crm.search.BusinessPartnerSearchCriteria;
import com.contactcore.crm.search.BusinessPartnerSearchQueryService;
import com.contactcore.crm.search.BusinessPartnerSearchSort;
import com.contactcore.shared.api.InvalidRequestException;
import com.contactcore.shared.api.NotFoundException;
import com.contactcore.shared.api.PageResponse;
import com.contactcore.shared.api.PageRequestNormalizer;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessPartnerService {
    private final BusinessPartnerRepository partners;
    private final AddressRepository addresses;
    private final ReferenceDataResolver references;
    private final BusinessPartnerSearchQueryService searchQueries;

    public BusinessPartnerService(BusinessPartnerRepository partners, AddressRepository addresses, ReferenceDataResolver references,
                                  BusinessPartnerSearchQueryService searchQueries) {
        this.partners = partners;
        this.addresses = addresses;
        this.references = references;
        this.searchQueries = searchQueries;
    }

    @Transactional(readOnly = true)
    public PageResponse<BusinessPartnerResponse> search(String kindCode, String query, int page, int size, String sort) {
        int normalizedPage = PageRequestNormalizer.page(page);
        int normalizedSize = PageRequestNormalizer.size(size);
        String normalizedQuery = PageRequestNormalizer.query(query);
        BusinessPartnerSearchCriteria criteria = new BusinessPartnerSearchCriteria(
                BusinessPartnerInputNormalizer.requiredCode(kindCode),
                normalizedQuery,
                "%" + normalizedQuery.toLowerCase(Locale.ROOT) + "%",
                normalizedPage,
                normalizedSize,
                (long) normalizedPage * normalizedSize,
                BusinessPartnerSearchSort.from(sort)
        );
        return searchQueries.search(criteria);
    }

    @Transactional(readOnly = true)
    public BusinessPartnerResponse get(Long id) {
        return BusinessPartnerMapper.toResponse(findActive(id));
    }

    @Transactional
    public BusinessPartnerResponse create(BusinessPartnerWriteRequest request) {
        NormalizedBusinessPartnerInput input = BusinessPartnerInputNormalizer.normalize(request);
        ensureCodeAvailable(input.code(), null);
        BusinessPartner partner = new BusinessPartner(references.kind(input.kind()), references.status(input.statusCode()), input.code(), input.name());
        apply(partner, input);
        return BusinessPartnerMapper.toResponse(partners.save(partner));
    }

    @Transactional
    public BusinessPartnerResponse update(Long id, BusinessPartnerWriteRequest request) {
        BusinessPartner partner = findActive(id);
        NormalizedBusinessPartnerInput input = BusinessPartnerInputNormalizer.normalize(request);
        ensureCodeAvailable(input.code(), id);
        apply(partner, input);
        return BusinessPartnerMapper.toResponse(partner);
    }

    @Transactional
    public void archive(Long id) {
        findActive(id).archive();
    }

    @Transactional(readOnly = true)
    public BusinessPartner findActive(Long id) {
        return partners.findActiveById(id).orElseThrow(() -> new NotFoundException("Business partner not found: " + id));
    }

    private void apply(BusinessPartner partner, NormalizedBusinessPartnerInput input) {
        partner.updateCore(
                references.kind(input.kind()),
                references.status(input.statusCode()),
                references.optionalLeadSource(input.sourceCode()).orElse(null),
                input.code(),
                input.name(),
                input.notes()
        );
        applyPrimaryContact(partner, "EMAIL", input.primaryEmail());
        applyPrimaryContact(partner, "PHONE", input.primaryPhone());
        applyPrimaryContact(partner, "WEBSITE", input.website());
        applyPrimaryAddress(partner, input);
    }

    private void applyPrimaryContact(BusinessPartner partner, String typeCode, String value) {
        var existing = partner.activePrimaryContact(typeCode);
        if (value == null) {
            existing.ifPresent(BusinessPartnerContactMethod::archive);
            return;
        }
        existing.ifPresentOrElse(
                method -> method.updateValue(value),
                () -> {
                    ContactMethodType type = references.contactMethodType(typeCode);
                    partner.addContactMethod(new BusinessPartnerContactMethod(type, "Primary", value, true));
                }
        );
    }

    private void applyPrimaryAddress(BusinessPartner partner, NormalizedBusinessPartnerInput input) {
        boolean emptyAddress = input.addressLine1() == null && input.addressLine2() == null && input.city() == null
                && input.postalCode() == null && input.countryCode() == null;
        var existing = partner.activePrimaryAddress();
        if (emptyAddress) {
            existing.ifPresent(BusinessPartnerAddress::archive);
            return;
        }
        existing.ifPresentOrElse(
                assignment -> assignment.getAddress().update(input.addressLine1(), input.addressLine2(), input.city(), input.postalCode(), input.countryCode()),
                () -> {
                    Address address = addresses.save(new Address(input.addressLine1(), input.addressLine2(), input.city(), input.postalCode(), input.countryCode()));
                    partner.addAddress(new BusinessPartnerAddress(address));
                }
        );
    }

    private void ensureCodeAvailable(String code, Long currentId) {
        partners.findActiveByCode(code).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new InvalidRequestException("Business partner code already exists: " + code);
            }
        });
    }
}
