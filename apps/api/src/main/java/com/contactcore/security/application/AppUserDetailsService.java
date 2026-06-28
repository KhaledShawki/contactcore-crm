// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.application;

import com.contactcore.security.domain.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserDetailsService implements UserDetailsService {
    private final AppUserRepository users;

    public AppUserDetailsService(AppUserRepository users) {
        this.users = users;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) {
        return users.findActiveByLogin(login)
                .map(UserPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}
