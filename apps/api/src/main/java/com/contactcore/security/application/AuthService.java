// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.application;

import com.contactcore.profile.domain.UserProfileRepository;
import com.contactcore.security.api.AuthResponse;
import com.contactcore.security.api.CurrentUserResponse;
import com.contactcore.security.api.LoginRequest;
import com.contactcore.security.domain.AppUser;
import com.contactcore.security.domain.AppUserRepository;
import com.contactcore.shared.api.NotFoundException;
import com.contactcore.shared.localization.SupportedLocale;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService tokenService;
    private final AppUserRepository users;
    private final UserProfileRepository profiles;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenService tokenService, AppUserRepository users, UserProfileRepository profiles) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.users = users;
        this.profiles = profiles;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        AppUser user = users.findById(principal.id()).orElseThrow(() -> new NotFoundException("User not found."));
        user.markLogin();
        CurrentUserResponse currentUser = toCurrentUser(user);
        return AuthResponse.bearer(tokenService.createToken(principal), currentUser);
    }

    @Transactional(readOnly = true)
    public CurrentUserResponse currentUser(Long userId) {
        return users.findById(userId).map(this::toCurrentUser).orElseThrow(() -> new NotFoundException("User not found."));
    }

    private CurrentUserResponse toCurrentUser(AppUser user) {
        var profile = profiles.findByUserId(user.getId());
        String displayName = profile.map(value -> value.getDisplayName()).orElse(user.getUsername());
        SupportedLocale locale = SupportedLocale.normalizeOrDefault(profile.map(value -> value.getLocale()).orElse(null));
        List<String> roles = user.getRoles().stream().map(role -> role.getCode()).toList();
        return new CurrentUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                displayName,
                locale.tag(),
                locale.direction().htmlValue(),
                roles
        );
    }
}
