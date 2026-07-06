// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class IamRoleCodeNormalizerTest {
    private final IamRoleCodeNormalizer normalizer = new IamRoleCodeNormalizer();

    @Test
    void normalizesSpringAuthoritiesToRoleCodes() {
        assertThat(normalizer.normalize(List.of("ROLE_ADMIN", "sales_user", " ROLE_ADMIN ", "", "ROLE_")))
                .containsExactly("ADMIN", "SALES_USER");
    }

    @Test
    void ignoresMissingRoleCollections() {
        assertThat(normalizer.normalize(null)).isEmpty();
        assertThat(normalizer.normalize(List.of())).isEmpty();
    }
}
