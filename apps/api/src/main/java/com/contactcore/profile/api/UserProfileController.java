// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.profile.api;

import com.contactcore.profile.application.UserProfileService;
import com.contactcore.security.application.UserPrincipal;
import com.contactcore.storage.application.StoredObjectContent;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {
    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @GetMapping
    public UserProfileResponse get(@AuthenticationPrincipal UserPrincipal principal) {
        return service.get(principal.id());
    }

    @PutMapping
    public UserProfileResponse update(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody UserProfileWriteRequest request) {
        return service.update(principal.id(), request);
    }

    @PatchMapping("/locale")
    public UserProfileResponse updateLocale(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody UserLocaleUpdateRequest request) {
        return service.updateLocale(principal.id(), request);
    }

    @PostMapping("/image")
    public UserProfileResponse uploadImage(@AuthenticationPrincipal UserPrincipal principal, @RequestParam("file") MultipartFile file) throws IOException {
        return service.uploadImage(principal.id(), file);
    }

    @GetMapping("/image/content")
    public ResponseEntity<Resource> imageContent(@AuthenticationPrincipal UserPrincipal principal) throws IOException {
        StoredObjectContent content = service.downloadProfileImage(principal.id());
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_TYPE, content.contentType())
                .contentLength(content.sizeBytes())
                .body(content.resource());
    }
}
