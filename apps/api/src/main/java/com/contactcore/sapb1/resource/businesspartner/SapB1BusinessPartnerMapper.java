// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.resource.businesspartner;

import com.contactcore.connector.businesspartner.model.ConnectorAddressType;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerAddress;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerCommercialProfile;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerContactPerson;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerContactPoint;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerDetail;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerFinancialProfile;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerIdentity;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerSourceReference;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerStatus;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerSummary;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerTransactionSummary;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerType;
import com.contactcore.connector.businesspartner.model.ConnectorContactPointType;
import com.contactcore.connector.domain.CrmConnectorInstance;
import com.contactcore.connector.model.CrmConnectorCapability;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SapB1BusinessPartnerMapper {
    private static final List<CrmConnectorCapability> READ_ONLY_CAPABILITIES = List.of(CrmConnectorCapability.READ_BUSINESS_PARTNERS);

    public ConnectorBusinessPartnerSummary toSummary(CrmConnectorInstance instance, SapB1BusinessPartnerDto dto) {
        ConnectorBusinessPartnerFinancialProfile financial = financialProfile(dto);
        return new ConnectorBusinessPartnerSummary(
                identity(instance, dto),
                type(dto.cardType()),
                status(dto.valid(), dto.frozen()),
                blankToNull(dto.emailAddress()),
                firstNonBlank(dto.phone1(), dto.cellular(), dto.phone2()),
                blankToNull(dto.website()),
                financial.currency(),
                financial.currentBalance(),
                null,
                READ_ONLY_CAPABILITIES
        );
    }

    public ConnectorBusinessPartnerDetail toDetail(CrmConnectorInstance instance, SapB1BusinessPartnerDto dto) {
        return new ConnectorBusinessPartnerDetail(
                identity(instance, dto),
                type(dto.cardType()),
                status(dto.valid(), dto.frozen()),
                commercialProfile(dto),
                financialProfile(dto),
                addresses(instance, dto),
                contactPersons(instance, dto),
                contactPoints(instance, dto),
                ConnectorBusinessPartnerTransactionSummary.empty(),
                extensionFields(dto),
                READ_ONLY_CAPABILITIES
        );
    }

    private ConnectorBusinessPartnerIdentity identity(CrmConnectorInstance instance, SapB1BusinessPartnerDto dto) {
        return new ConnectorBusinessPartnerIdentity(
                instance.getId(),
                instance.getType().name(),
                required(dto.cardCode(), "CardCode"),
                required(dto.cardCode(), "CardCode"),
                required(dto.cardName(), "CardName"),
                source(instance, "BusinessPartners", dto.cardCode())
        );
    }

    private ConnectorBusinessPartnerSourceReference source(CrmConnectorInstance instance, String resourceType, String externalId) {
        return new ConnectorBusinessPartnerSourceReference(
                instance.getType().name(),
                instance.getId(),
                instance.getDisplayName(),
                resourceType,
                externalId,
                resourceType + ":" + externalId
        );
    }

    private ConnectorBusinessPartnerType type(String cardType) {
        if ("C".equalsIgnoreCase(cardType)) {
            return ConnectorBusinessPartnerType.CUSTOMER;
        }
        if ("S".equalsIgnoreCase(cardType)) {
            return ConnectorBusinessPartnerType.SUPPLIER;
        }
        if ("L".equalsIgnoreCase(cardType)) {
            return ConnectorBusinessPartnerType.LEAD;
        }
        return ConnectorBusinessPartnerType.UNKNOWN;
    }

    private ConnectorBusinessPartnerStatus status(String valid, String frozen) {
        boolean frozenFlag = sapYes(frozen);
        boolean validFlag = !sapNo(valid);
        boolean active = validFlag && !frozenFlag;
        String lifecycle = frozenFlag ? "FROZEN" : active ? "ACTIVE" : "INACTIVE";
        return new ConnectorBusinessPartnerStatus(active, frozenFlag, validFlag, lifecycle, statusCode(valid, frozen));
    }

    private ConnectorBusinessPartnerFinancialProfile financialProfile(SapB1BusinessPartnerDto dto) {
        return new ConnectorBusinessPartnerFinancialProfile(
                blankToNull(dto.currency()),
                dto.currentAccountBalance(),
                dto.creditLimit(),
                null,
                null,
                null,
                null,
                null,
                dto.paymentTermsGroupCode(),
                null,
                dto.federalTaxId(),
                dto.vatLiable(),
                null,
                null
        );
    }

    private ConnectorBusinessPartnerCommercialProfile commercialProfile(SapB1BusinessPartnerDto dto) {
        return new ConnectorBusinessPartnerCommercialProfile(
                dto.salesPersonCode() == null ? null : dto.salesPersonCode().toString(),
                null,
                null,
                null,
                dto.territory() == null ? null : dto.territory().toString(),
                null,
                null,
                null,
                dto.groupCode() == null ? null : dto.groupCode().toString(),
                null
        );
    }

    private List<ConnectorBusinessPartnerAddress> addresses(CrmConnectorInstance instance, SapB1BusinessPartnerDto dto) {
        List<ConnectorBusinessPartnerAddress> result = new ArrayList<>();
        for (SapB1BusinessPartnerAddressDto address : dto.addresses()) {
            ConnectorAddressType type = addressType(address.addressType());
            String externalAddressId = address.rowNum() == null ? address.addressName() : address.rowNum().toString();
            boolean defaultBilling = equalsIgnoreCase(address.addressName(), dto.defaultBillingAddress());
            boolean defaultShipping = equalsIgnoreCase(address.addressName(), dto.defaultShippingAddress());
            result.add(new ConnectorBusinessPartnerAddress(
                    externalAddressId,
                    type,
                    address.addressName(),
                    address.street(),
                    address.block(),
                    address.zipCode(),
                    address.city(),
                    address.county(),
                    address.state(),
                    address.country(),
                    defaultBilling,
                    defaultShipping,
                    source(instance, "BusinessPartnerAddress", dto.cardCode() + ":" + externalAddressId)
            ));
        }
        return result;
    }

    private List<ConnectorBusinessPartnerContactPerson> contactPersons(CrmConnectorInstance instance, SapB1BusinessPartnerDto dto) {
        List<ConnectorBusinessPartnerContactPerson> result = new ArrayList<>();
        for (SapB1BusinessPartnerContactEmployeeDto contact : dto.contactEmployees()) {
            String externalContactId = contact.internalCode() == null ? contact.name() : contact.internalCode().toString();
            String displayName = firstNonBlank(contact.name(), joinName(contact.firstName(), contact.middleName(), contact.lastName()));
            result.add(new ConnectorBusinessPartnerContactPerson(
                    externalContactId,
                    contact.firstName(),
                    contact.middleName(),
                    contact.lastName(),
                    displayName,
                    contact.title(),
                    contact.position(),
                    contact.email(),
                    contact.phone(),
                    contact.mobilePhone(),
                    equalsIgnoreCase(displayName, dto.defaultContactPerson()),
                    !sapNo(contact.active()),
                    source(instance, "BusinessPartnerContact", dto.cardCode() + ":" + externalContactId)
            ));
        }
        return result;
    }

    private List<ConnectorBusinessPartnerContactPoint> contactPoints(CrmConnectorInstance instance, SapB1BusinessPartnerDto dto) {
        List<ConnectorBusinessPartnerContactPoint> points = new ArrayList<>();
        add(points, ConnectorContactPointType.EMAIL, "Primary email", dto.emailAddress(), true, source(instance, "BusinessPartners", dto.cardCode()));
        add(points, ConnectorContactPointType.PHONE, "Phone 1", dto.phone1(), true, source(instance, "BusinessPartners", dto.cardCode()));
        add(points, ConnectorContactPointType.PHONE, "Phone 2", dto.phone2(), false, source(instance, "BusinessPartners", dto.cardCode()));
        add(points, ConnectorContactPointType.MOBILE, "Mobile", dto.cellular(), false, source(instance, "BusinessPartners", dto.cardCode()));
        add(points, ConnectorContactPointType.WEBSITE, "Website", dto.website(), true, source(instance, "BusinessPartners", dto.cardCode()));
        return points;
    }

    private void add(List<ConnectorBusinessPartnerContactPoint> points,
                     ConnectorContactPointType type,
                     String label,
                     String value,
                     boolean primary,
                     ConnectorBusinessPartnerSourceReference source) {
        if (value != null && !value.isBlank()) {
            points.add(new ConnectorBusinessPartnerContactPoint(type, label, value, primary, false, source));
        }
    }

    private Map<String, Object> extensionFields(SapB1BusinessPartnerDto dto) {
        Map<String, Object> fields = new LinkedHashMap<>();
        put(fields, "sapPaymentTermsGroupCode", dto.paymentTermsGroupCode());
        put(fields, "sapSalesPersonCode", dto.salesPersonCode());
        put(fields, "sapGroupCode", dto.groupCode());
        put(fields, "sapTerritory", dto.territory());
        return fields;
    }

    private ConnectorAddressType addressType(String value) {
        if ("bo_BillTo".equalsIgnoreCase(value) || "B".equalsIgnoreCase(value)) {
            return ConnectorAddressType.BILLING;
        }
        if ("bo_ShipTo".equalsIgnoreCase(value) || "S".equalsIgnoreCase(value)) {
            return ConnectorAddressType.SHIPPING;
        }
        return ConnectorAddressType.UNKNOWN;
    }

    private void put(Map<String, Object> fields, String key, Object value) {
        if (value != null) {
            fields.put(key, value);
        }
    }

    private boolean sapYes(String value) {
        return "tYES".equalsIgnoreCase(value) || "Y".equalsIgnoreCase(value) || "YES".equalsIgnoreCase(value);
    }

    private boolean sapNo(String value) {
        return "tNO".equalsIgnoreCase(value) || "N".equalsIgnoreCase(value) || "NO".equalsIgnoreCase(value);
    }

    private String statusCode(String valid, String frozen) {
        return "Valid=" + blankToNull(valid) + ";Frozen=" + blankToNull(frozen);
    }

    private String joinName(String firstName, String middleName, String lastName) {
        return String.join(" ", List.of(blankToEmpty(firstName), blankToEmpty(middleName), blankToEmpty(lastName))).trim();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    private boolean equalsIgnoreCase(String left, String right) {
        return left != null && right != null && left.trim().equalsIgnoreCase(right.trim());
    }

    private String required(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SAP B1 Business Partner " + fieldName + " is required.");
        }
        return value.trim();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String blankToEmpty(String value) {
        return value == null || value.isBlank() ? "" : value.trim();
    }
}
