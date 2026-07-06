// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamPolicyDocument;
import com.contactcore.iam.domain.IamPolicyStatement;
import com.contactcore.iam.domain.IamResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class IamPolicyDocumentSerializerTest {
    private final IamPolicyDocumentSerializer serializer = new IamPolicyDocumentSerializer(new ObjectMapper());

    @Test
    void roundTripsPolicyDocumentsWithoutLosingStatements() {
        IamPolicyDocument document = new IamPolicyDocument(IamPolicyDocument.CURRENT_VERSION, List.of(
                IamPolicyStatement.allow(
                        "AllowCommercialRead",
                        List.of(IamAction.of("commercial:ReadDocument")),
                        List.of(IamResource.of("contactcore:default:commercial:document/*"))
                )
        ));

        String serialized = serializer.serialize(document);

        assertThat(serialized).doesNotContain("\"empty\"");
        IamPolicyDocument roundTripped = serializer.deserialize(serialized);

        assertThat(roundTripped.version()).isEqualTo(IamPolicyDocument.CURRENT_VERSION);
        assertThat(roundTripped.statements()).hasSize(1);
        assertThat(roundTripped.statements().getFirst().sid()).isEqualTo("AllowCommercialRead");
        assertThat(roundTripped.statements().getFirst().actions().getFirst().value()).isEqualTo("commercial:ReadDocument");
    }
}
