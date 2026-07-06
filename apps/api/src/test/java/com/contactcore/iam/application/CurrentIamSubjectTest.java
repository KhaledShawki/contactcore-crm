// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.iam.domain.IamPrincipalRef;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CurrentIamSubjectTest {
    @Test
    void supportsNonUserRuntimeSubjects() {
        CurrentIamSubject subject = new CurrentIamSubject(
                IamPrincipalRef.system("sync-worker"),
                List.of("CONNECTOR_ADMIN"),
                new IamSubjectAttributes(null, null, null, null, Map.of("job", "commercial-sync"))
        );

        assertThat(subject.principal()).isEqualTo(IamPrincipalRef.system("sync-worker"));
        assertThat(subject.hasRole("connector_admin")).isTrue();
        assertThat(subject.attributes().values()).containsEntry("job", "commercial-sync");
    }
}
