// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.settings.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserUiSettingsRepository extends JpaRepository<UserUiSettings, Long> {
    Optional<UserUiSettings> findByUserId(Long userId);
}
