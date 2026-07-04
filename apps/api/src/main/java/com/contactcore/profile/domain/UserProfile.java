// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.profile.domain;

import com.contactcore.security.domain.AppUser;
import com.contactcore.shared.domain.BaseEntity;
import com.contactcore.storage.domain.StoredFile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_profile")
public class UserProfile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser user;

    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Column(length = 64)
    private String phone;

    @Column(name = "job_title", length = 128)
    private String jobTitle;

    @Column(columnDefinition = "text")
    private String bio;

    @Column(nullable = false, length = 32)
    private String locale = "en";

    @Column(nullable = false, length = 64)
    private String timezone = "Europe/Berlin";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_file_id")
    private StoredFile profileImageFile;

    protected UserProfile() {}

    public UserProfile(AppUser user, String displayName) {
        this.user = user;
        this.displayName = displayName;
    }

    public AppUser getUser() {
        return user;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhone() {
        return phone;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getBio() {
        return bio;
    }

    public String getLocale() {
        return locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public StoredFile getProfileImageFile() {
        return profileImageFile;
    }

    public void update(String displayName, String phone, String jobTitle, String bio, String locale, String timezone) {
        this.displayName = displayName;
        this.phone = phone;
        this.jobTitle = jobTitle;
        this.bio = bio;
        this.locale = locale;
        this.timezone = timezone;
    }

    public void updateLocale(String locale) {
        this.locale = locale;
    }

    public void setProfileImageFile(StoredFile profileImageFile) {
        this.profileImageFile = profileImageFile;
    }
}
