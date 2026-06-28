// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.contactcore.security.config.JwtProperties;
import com.contactcore.security.domain.AppUser;
import com.contactcore.security.domain.SecurityRole;
import com.contactcore.shared.api.InvalidRequestException;
import com.contactcore.shared.domain.BaseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class JwtTokenServiceTest {
    private final JwtTokenService service = new JwtTokenService(
            new JwtProperties("contactcore-test", "12345678901234567890123456789012", 60),
            new ObjectMapper()
    );

    @Test
    void createsTokenThatCanBeValidatedBackToSubject() throws Exception {
        AppUser user = new AppUser("admin", "admin@example.test", "hash");
        setId(user, 42L);
        user.addRole(new SecurityRole("ADMIN", "Administrator"));
        UserPrincipal principal = UserPrincipal.from(user);

        String token = service.createToken(principal);

        assertThat(service.validateAndGetSubject(token)).isEqualTo("admin");
    }

    @Test
    void rejectsTamperedToken() throws Exception {
        AppUser user = new AppUser("admin", "admin@example.test", "hash");
        setId(user, 42L);
        UserPrincipal principal = UserPrincipal.from(user);
        String token = service.createToken(principal);
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThatThrownBy(() -> service.validateAndGetSubject(tampered))
                .isInstanceOf(InvalidRequestException.class);
    }

    private void setId(BaseEntity entity, Long id) throws Exception {
        Field field = BaseEntity.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}
