// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamActionDescriptor;
import com.contactcore.iam.domain.IamPrincipalRef;
import com.contactcore.iam.domain.IamPrincipalType;
import com.contactcore.iam.domain.IamResource;
import org.junit.jupiter.api.Test;

class IamDomainValidationTest {
    @Test
    void normalizesActionServiceName() {
        assertThat(IamAction.of("Commercial:ReadDocument").value()).isEqualTo("commercial:ReadDocument");
    }

    @Test
    void rejectsInvalidActions() {
        assertThatThrownBy(() -> IamAction.of("ReadDocument"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("service:Operation");
    }

    @Test
    void buildsContactCoreResources() {
        IamResource resource = IamResource.contactCore("default", "commercial", "document", "123");

        assertThat(resource.value()).isEqualTo("contactcore:default:commercial:document/123");
        assertThat(resource.tenantId()).isEqualTo("default");
        assertThat(resource.service()).isEqualTo("commercial");
    }

    @Test
    void rejectsWildcardActionDescriptors() {
        assertThatThrownBy(() -> IamActionDescriptor.read(IamAction.of("*"), "Wildcard"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("concrete actions");
    }

    @Test
    void rejectsBlankPrincipalId() {
        assertThatThrownBy(() -> new IamPrincipalRef(IamPrincipalType.USER, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("principal id");
    }
}
