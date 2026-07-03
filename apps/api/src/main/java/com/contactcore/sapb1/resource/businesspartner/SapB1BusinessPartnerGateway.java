// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.resource.businesspartner;

import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerType;
import com.contactcore.connector.port.ConnectorExecutionContext;
import com.contactcore.sapb1.client.SapB1ServiceLayerClient;
import com.contactcore.sapb1.model.SapB1ConnectorConfiguration;
import com.contactcore.sapb1.model.SapB1Session;
import com.contactcore.sapb1.odata.SapB1ODataFilter;
import com.contactcore.sapb1.odata.SapB1ODataPathBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SapB1BusinessPartnerGateway {
    private static final String[] SUMMARY_FIELDS = {
            "CardCode",
            "CardName",
            "CardType",
            "Phone1",
            "Phone2",
            "Cellular",
            "EmailAddress",
            "Website",
            "Currency",
            "CurrentAccountBalance",
            "Valid",
            "Frozen"
    };

    private static final String[] DETAIL_FIELDS = {
            "CardCode",
            "CardName",
            "CardType",
            "Phone1",
            "Phone2",
            "Cellular",
            "EmailAddress",
            "Website",
            "Currency",
            "CurrentAccountBalance",
            "CreditLimit",
            "MaxCommitment",
            "FederalTaxID",
            "VatLiable",
            "PayTermsGrpCode",
            "Valid",
            "Frozen",
            "DefaultBillingAddress",
            "DefaultShippingAddress",
            "ContactPerson",
            "SalesPersonCode",
            "GroupCode",
            "Territory",
            "BPAddresses",
            "ContactEmployees"
    };

    private final ObjectMapper objectMapper;
    private final SapB1ServiceLayerClient client;
    private final SapB1ODataPathBuilder pathBuilder;

    public SapB1BusinessPartnerGateway(ObjectMapper objectMapper,
                                       SapB1ServiceLayerClient client,
                                       SapB1ODataPathBuilder pathBuilder) {
        this.objectMapper = objectMapper;
        this.client = client;
        this.pathBuilder = pathBuilder;
    }

    public SapB1BusinessPartnerCollectionResponse search(ConnectorExecutionContext context, SapB1BusinessPartnerSearchRequest request) {
        String path = pathBuilder.entitySet("BusinessPartners")
                .select(SUMMARY_FIELDS)
                .filter(filter(request))
                .orderBy(orderBy(request))
                .skip(request.page() * request.size())
                .top(request.size())
                .build();
        return client.get(configuration(context), session(context), path, SapB1BusinessPartnerCollectionResponse.class);
    }

    public SapB1BusinessPartnerDto findByCardCode(ConnectorExecutionContext context, String cardCode) {
        String path = pathBuilder.entity("BusinessPartners", cardCode)
                .select(DETAIL_FIELDS)
                .build();
        return client.get(configuration(context), session(context), path, SapB1BusinessPartnerDto.class);
    }

    private SapB1ODataFilter filter(SapB1BusinessPartnerSearchRequest request) {
        List<SapB1ODataFilter> filters = new ArrayList<>();
        if (request.type() != null && request.type() != ConnectorBusinessPartnerType.UNKNOWN) {
            filters.add(SapB1ODataFilter.eq("CardType", cardType(request.type())));
        }
        if (!request.query().isBlank()) {
            filters.add(SapB1ODataFilter.or(
                    SapB1ODataFilter.contains("CardCode", request.query()),
                    SapB1ODataFilter.contains("CardName", request.query()),
                    SapB1ODataFilter.contains("EmailAddress", request.query())
            ));
        }
        return SapB1ODataFilter.all(filters);
    }

    private String orderBy(SapB1BusinessPartnerSearchRequest request) {
        return switch (request.sort()) {
            case NAME_ASC -> "CardName asc";
            case NAME_DESC -> "CardName desc";
            case CODE_DESC -> "CardCode desc";
            case CODE_ASC -> "CardCode asc";
        };
    }

    private String cardType(ConnectorBusinessPartnerType type) {
        return switch (type) {
            case CUSTOMER -> "C";
            case SUPPLIER -> "S";
            case LEAD -> "L";
            case UNKNOWN -> "";
        };
    }

    private SapB1ConnectorConfiguration configuration(ConnectorExecutionContext context) {
        return SapB1ConnectorConfiguration.from(context.instance(), objectMapper);
    }

    private SapB1Session session(ConnectorExecutionContext context) {
        if (context.session() instanceof SapB1Session sapSession) {
            return sapSession;
        }
        throw new IllegalStateException("Active connector session is not a SAP B1 session.");
    }
}
