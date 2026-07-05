// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamPolicyDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class IamPolicyDocumentSerializer {
    private final ObjectMapper objectMapper;

    public IamPolicyDocumentSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serialize(IamPolicyDocument document) {
        try {
            return objectMapper.writeValueAsString(document);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize IAM policy document.", exception);
        }
    }

    public IamPolicyDocument deserialize(String documentJson) {
        try {
            return objectMapper.readValue(documentJson, IamPolicyDocument.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to deserialize IAM policy document.", exception);
        }
    }
}
