// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamResource;
import org.junit.jupiter.api.Test;

class IamMatcherTest {
    private final IamMatcher matcher = new IamMatcher();

    @Test
    void matchesActionWildcards() {
        assertThat(matcher.matchesAction(IamAction.of("commercial:*"), IamAction.of("commercial:ReadDocument"))).isTrue();
        assertThat(matcher.matchesAction(IamAction.of("*:ReadDocument"), IamAction.of("commercial:ReadDocument"))).isTrue();
        assertThat(matcher.matchesAction(IamAction.of("crm:*"), IamAction.of("commercial:ReadDocument"))).isFalse();
    }

    @Test
    void matchesResourceWildcards() {
        assertThat(matcher.matchesResource(
                IamResource.of("contactcore:default:commercial:document/*"),
                IamResource.of("contactcore:default:commercial:document/123")
        )).isTrue();
        assertThat(matcher.matchesResource(
                IamResource.of("contactcore:default:commercial:item/*"),
                IamResource.of("contactcore:default:commercial:document/123")
        )).isFalse();
    }
}
