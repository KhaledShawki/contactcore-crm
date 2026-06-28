// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.settings.domain;

import com.contactcore.security.domain.AppUser;
import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_ui_settings")
public class UserUiSettings extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser user;

    @Column(nullable = false, length = 32)
    private String theme = "ocean";

    @Column(name = "text_size", nullable = false, length = 32)
    private String textSize = "comfortable";

    @Column(nullable = false, length = 32)
    private String density = "comfortable";

    @Column(name = "sidebar_mode", nullable = false, length = 32)
    private String sidebarMode = "expanded";

    @Column(name = "reduce_motion", nullable = false)
    private boolean reduceMotion;

    @Column(name = "high_contrast", nullable = false)
    private boolean highContrast;

    @Column(name = "default_landing_page", nullable = false, length = 120)
    private String defaultLandingPage = "/dashboard";

    protected UserUiSettings() {}

    public UserUiSettings(AppUser user) {
        this.user = user;
    }

    public AppUser getUser() {
        return user;
    }

    public String getTheme() {
        return theme;
    }

    public String getTextSize() {
        return textSize;
    }

    public String getDensity() {
        return density;
    }

    public String getSidebarMode() {
        return sidebarMode;
    }

    public boolean isReduceMotion() {
        return reduceMotion;
    }

    public boolean isHighContrast() {
        return highContrast;
    }

    public String getDefaultLandingPage() {
        return defaultLandingPage;
    }

    public void update(String theme, String textSize, String density, String sidebarMode,
                       boolean reduceMotion, boolean highContrast, String defaultLandingPage) {
        this.theme = theme;
        this.textSize = textSize;
        this.density = density;
        this.sidebarMode = sidebarMode;
        this.reduceMotion = reduceMotion;
        this.highContrast = highContrast;
        this.defaultLandingPage = defaultLandingPage;
    }
}
