// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.resource.businesspartner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerType;
import com.contactcore.connector.businesspartner.query.ConnectorBusinessPartnerSort;
import com.contactcore.connector.domain.CrmConnectorInstance;
import com.contactcore.connector.model.CrmConnectorEnvironment;
import com.contactcore.connector.model.CrmConnectorType;
import com.contactcore.connector.port.ConnectorExecutionContext;
import com.contactcore.sapb1.client.SapB1ServiceLayerClient;
import com.contactcore.sapb1.model.SapB1ConnectorConfiguration;
import com.contactcore.sapb1.model.SapB1Session;
import com.contactcore.sapb1.odata.SapB1ODataPathBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SapB1BusinessPartnerGatewayTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SapB1ServiceLayerClient client = mock(SapB1ServiceLayerClient.class);
    private final SapB1BusinessPartnerGateway gateway = new SapB1BusinessPartnerGateway(objectMapper, client, new SapB1ODataPathBuilder());

    @Test
    void buildsBusinessPartnerSearchPathOutsideServiceLayerClient() {
        ConnectorExecutionContext context = context();
        when(client.get(any(), any(), any(), eq(SapB1BusinessPartnerCollectionResponse.class)))
                .thenReturn(new SapB1BusinessPartnerCollectionResponse(List.of()));

        gateway.search(context, new SapB1BusinessPartnerSearchRequest("Meyer & Sons", ConnectorBusinessPartnerType.CUSTOMER, 2, 25, ConnectorBusinessPartnerSort.NAME_ASC));

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(client).get(any(SapB1ConnectorConfiguration.class), any(SapB1Session.class), path.capture(), eq(SapB1BusinessPartnerCollectionResponse.class));
        assertThat(path.getValue()).startsWith("BusinessPartners?$select=");
        assertThat(path.getValue()).contains("CardType%20eq%20%27C%27");
        assertThat(path.getValue()).contains("contains%28CardName%2C%27Meyer%20%26%20Sons%27%29");
        assertThat(path.getValue()).contains("&$skip=50");
        assertThat(path.getValue()).contains("&$top=25");
        assertThat(path.getValue()).contains("&$orderby=CardName%20asc");
    }

    @Test
    void buildsCardCodeLookupPathOutsideServiceLayerClient() {
        ConnectorExecutionContext context = context();
        when(client.get(any(), any(), any(), eq(SapB1BusinessPartnerDto.class)))
                .thenReturn(new SapB1BusinessPartnerDto("C'100", "Example", "C", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, List.of(), List.of()));

        gateway.findByCardCode(context, "C'100");

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(client).get(any(SapB1ConnectorConfiguration.class), any(SapB1Session.class), path.capture(), eq(SapB1BusinessPartnerDto.class));
        assertThat(path.getValue()).contains("BusinessPartners('C''100')");
        assertThat(path.getValue()).contains("$select=CardCode");
        assertThat(path.getValue()).contains("BPAddresses");
        assertThat(path.getValue()).contains("ContactEmployees");
    }

    private ConnectorExecutionContext context() {
        var instance = new CrmConnectorInstance(
                CrmConnectorType.SAP_B1,
                "SAP Demo",
                CrmConnectorEnvironment.TEST,
                "{\"serviceLayerBaseUrl\":\"https://sap.example/b1s/v2\",\"companyDb\":\"SBODEMO\"}"
        );
        var session = new SapB1Session("manager", "session", "route", Instant.now());
        return new ConnectorExecutionContext(1L, instance, session);
    }
}
