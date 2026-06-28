// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.profile.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.profile.application.UserProfileService;
import com.contactcore.security.application.UserPrincipal;
import com.contactcore.security.domain.AppUser;
import com.contactcore.storage.application.StoredObjectContent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerImageContentTest {
    @Mock
    private UserProfileService service;

    @InjectMocks
    private UserProfileController controller;

    @Test
    void servesProfileImageContentForAuthenticatedUserWithNoStoreCacheControl() throws Exception {
        UserPrincipal principal = principal(42L);
        byte[] imageBytes = new byte[] {1, 2, 3};
        when(service.downloadProfileImage(42L))
                .thenReturn(new StoredObjectContent(new ByteArrayResource(imageBytes), "image/png", imageBytes.length));

        ResponseEntity<?> response = controller.imageContent(principal);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo("image/png");
		assertThat(response.getHeaders().getCacheControl()).isEqualTo(CacheControl.noStore().getHeaderValue());
        assertThat(response.getHeaders().getContentLength()).isEqualTo(imageBytes.length);
        assertThat(response.getBody()).isInstanceOf(ByteArrayResource.class);
        verify(service).downloadProfileImage(42L);
    }

    private UserPrincipal principal(Long userId) {
        AppUser user = new AppUser("admin", "admin@example.test", "hash");
        ReflectionTestUtils.setField(user, "id", userId);
        return UserPrincipal.from(user);
    }
}
