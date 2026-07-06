// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.application;

import com.contactcore.profile.domain.UserProfile;
import com.contactcore.profile.domain.UserProfileRepository;
import com.contactcore.security.domain.AppUser;
import com.contactcore.security.domain.AppUserRepository;
import com.contactcore.security.domain.SecurityRole;
import com.contactcore.security.domain.SecurityRoleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(0)
public class AdminUserInitializer implements ApplicationRunner {
    private final AdminUserProperties properties;
    private final AppUserRepository users;
    private final SecurityRoleRepository roles;
    private final UserProfileRepository profiles;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(AdminUserProperties properties, AppUserRepository users, SecurityRoleRepository roles,
                                UserProfileRepository profiles, PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.users = users;
        this.roles = roles;
        this.profiles = profiles;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (users.existsByUsernameIgnoreCase(properties.username())) {
            return;
        }
        SecurityRole admin = roles.findByCodeIgnoreCase("ADMIN").orElseGet(() -> roles.save(new SecurityRole("ADMIN", "Administrator")));
        AppUser user = new AppUser(properties.username(), properties.email(), passwordEncoder.encode(properties.password()));
        user.addRole(admin);
        users.save(user);
        profiles.save(new UserProfile(user, properties.displayName()));
    }
}
