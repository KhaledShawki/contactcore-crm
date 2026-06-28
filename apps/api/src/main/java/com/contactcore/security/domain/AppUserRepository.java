// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    @Query("""
            select user from AppUser user
            where user.archivedAt is null
              and (lower(user.username) = lower(:login) or lower(user.email) = lower(:login))
            """)
    Optional<AppUser> findActiveByLogin(@Param("login") String login);

    boolean existsByUsernameIgnoreCase(String username);
}
