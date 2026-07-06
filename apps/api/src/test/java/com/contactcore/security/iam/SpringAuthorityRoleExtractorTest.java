// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.iam;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.iam.application.IamRoleCodeNormalizer;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class SpringAuthorityRoleExtractorTest {
    private final SpringAuthorityRoleExtractor extractor = new SpringAuthorityRoleExtractor(new IamRoleCodeNormalizer());

    @Test
    void extractsNormalizedRoleCodesFromAuthorities() {
        assertThat(extractor.extract(List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_SALES_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        ))).containsExactly("ADMIN", "SALES_USER");
    }
}
