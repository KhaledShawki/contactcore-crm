// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.profile.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.iam.application.IamAccessDeniedException;
import com.contactcore.profile.application.UserProfileService;
import com.contactcore.security.application.UserPrincipal;
import com.contactcore.security.domain.AppUser;
import com.contactcore.storage.application.StoredObjectContent;
import com.contactcore.storage.security.StorageAuthorizationGuard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerImageContentTest {
    @Mock
    private UserProfileService service;

    @Mock
    private StorageAuthorizationGuard storageAuthorization;

    @InjectMocks
    private UserProfileController controller;

    @Test
    void uploadImageRequiresStorageUploadPermissionBeforeServiceCall() throws Exception {
        UserPrincipal principal = principal(42L);
        MultipartFile file = mock(MultipartFile.class);

        controller.uploadImage(principal, file);

        InOrder inOrder = inOrder(storageAuthorization, service);
        inOrder.verify(storageAuthorization).requireUploadProfileImage(42L);
        inOrder.verify(service).uploadImage(42L, file);
    }

    @Test
    void skipsImageUploadWhenStorageAuthorizationFails() throws Exception {
        UserPrincipal principal = principal(42L);
        MultipartFile file = mock(MultipartFile.class);
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        org.mockito.Mockito.doThrow(denied).when(storageAuthorization).requireUploadProfileImage(42L);

        assertThatThrownBy(() -> controller.uploadImage(principal, file)).isSameAs(denied);

        verify(service, never()).uploadImage(42L, file);
    }

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
        InOrder inOrder = inOrder(storageAuthorization, service);
        inOrder.verify(storageAuthorization).requireDownloadProfileImage(42L);
        inOrder.verify(service).downloadProfileImage(42L);
    }

    @Test
    void skipsProfileImageDownloadWhenStorageAuthorizationFails() throws Exception {
        UserPrincipal principal = principal(42L);
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        org.mockito.Mockito.doThrow(denied).when(storageAuthorization).requireDownloadProfileImage(42L);

        assertThatThrownBy(() -> controller.imageContent(principal)).isSameAs(denied);

        verify(service, never()).downloadProfileImage(42L);
    }

    private UserPrincipal principal(Long userId) {
        AppUser user = new AppUser("admin", "admin@example.test", "hash");
        ReflectionTestUtils.setField(user, "id", userId);
        return UserPrincipal.from(user);
    }
}
