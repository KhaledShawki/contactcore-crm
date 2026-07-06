// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.iam;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.iam.application.IamRoleCodeNormalizer;
import com.contactcore.iam.domain.IamPrincipalRef;
import com.contactcore.security.application.UserPrincipal;
import com.contactcore.security.domain.AppUser;
import com.contactcore.security.domain.SecurityRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

class UserPrincipalIamSubjectMapperTest {
    private final UserPrincipalIamSubjectMapper mapper = new UserPrincipalIamSubjectMapper(
            new SpringAuthorityRoleExtractor(new IamRoleCodeNormalizer())
    );

    @Test
    void mapsUserPrincipalToIamSubject() {
        AppUser user = new AppUser("khaled", "khaled@example.com", "hash");
        ReflectionTestUtils.setField(user, "id", 42L);
        user.addRole(new SecurityRole("ADMIN", "Administrator"));
        user.addRole(new SecurityRole("SALES_USER", "Sales User"));
        UserPrincipal principal = UserPrincipal.from(user);
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        var subject = mapper.map(authentication);

        assertThat(mapper.supports(authentication)).isTrue();
        assertThat(subject.principal()).isEqualTo(IamPrincipalRef.user(42L));
        assertThat(subject.roleCodes()).containsExactly("ADMIN", "SALES_USER");
        assertThat(subject.attributes().userId()).isEqualTo(42L);
        assertThat(subject.attributes().username()).isEqualTo("khaled");
        assertThat(subject.attributes().email()).isEqualTo("khaled@example.com");
    }
}
