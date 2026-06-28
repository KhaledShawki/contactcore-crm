// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.profile.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.contactcore.profile.api.UserProfileResponse;
import com.contactcore.profile.domain.UserProfile;
import com.contactcore.profile.domain.UserProfileRepository;
import com.contactcore.security.domain.AppUser;
import com.contactcore.shared.api.InvalidRequestException;
import com.contactcore.shared.api.NotFoundException;
import com.contactcore.storage.application.ObjectStorageService;
import com.contactcore.storage.application.StoredObject;
import com.contactcore.storage.application.ValidatedFileUpload;
import com.contactcore.storage.domain.StoredFile;
import com.contactcore.storage.domain.StoredFileRepository;
import com.contactcore.storage.security.FileUploadGuard;
import com.contactcore.storage.security.UploadPurpose;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImageUploadTest {
    @Mock
    private UserProfileRepository profiles;

    @Mock
    private StoredFileRepository storedFiles;

    @Mock
    private ObjectStorageService objectStorage;

    @Mock
    private FileUploadGuard uploadGuard;

    @InjectMocks
    private UserProfileService service;

    @Test
    void uploadsProfileImageThroughGuardAndStorageThenLinksStoredFile() throws IOException {
        UserProfile profile = new UserProfile(new AppUser("admin", "admin@example.test", "hash"), "Admin");
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", pngBytes());
        ValidatedFileUpload validatedUpload = new ValidatedFileUpload("avatar.png", "image/png", pngBytes());
        StoredObject storedObject = new StoredObject("profile-images/42/avatar.png", "avatar.png", "image/png", validatedUpload.sizeBytes());
        StoredFile savedFile = new StoredFile(storedObject.objectKey(), storedObject.originalFilename(), storedObject.contentType(), storedObject.sizeBytes());
        when(profiles.findByUserId(42L)).thenReturn(Optional.of(profile));
        when(uploadGuard.validateAndScan(UploadPurpose.PROFILE_IMAGE, file)).thenReturn(validatedUpload);
        when(objectStorage.upload("profile-images/42", validatedUpload)).thenReturn(storedObject);
        when(storedFiles.save(any(StoredFile.class))).thenReturn(savedFile);

        UserProfileResponse response = service.uploadImage(42L, file);

        assertThat(profile.getProfileImageFile()).isSameAs(savedFile);
        assertThat(response.profileImageUrl()).contains("/api/profile/image/content?v=");
        verify(uploadGuard).validateAndScan(UploadPurpose.PROFILE_IMAGE, file);
        verify(objectStorage).upload("profile-images/42", validatedUpload);
        ArgumentCaptor<StoredFile> storedFile = ArgumentCaptor.forClass(StoredFile.class);
        verify(storedFiles).save(storedFile.capture());
        assertThat(storedFile.getValue().getObjectKey()).isEqualTo("profile-images/42/avatar.png");
        assertThat(storedFile.getValue().getOriginalFilename()).isEqualTo("avatar.png");
        assertThat(storedFile.getValue().getContentType()).isEqualTo("image/png");
        assertThat(storedFile.getValue().getSizeBytes()).isEqualTo(validatedUpload.sizeBytes());
    }

    @Test
    void doesNotStoreImageWhenUploadGuardRejectsFile() throws IOException {
        UserProfile profile = new UserProfile(new AppUser("admin", "admin@example.test", "hash"), "Admin");
        MockMultipartFile file = new MockMultipartFile("file", "avatar.exe", "application/octet-stream", new byte[] {1});
        when(profiles.findByUserId(42L)).thenReturn(Optional.of(profile));
        when(uploadGuard.validateAndScan(UploadPurpose.PROFILE_IMAGE, file))
                .thenThrow(new InvalidRequestException("File type is not allowed."));

        assertThatThrownBy(() -> service.uploadImage(42L, file))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("not allowed");
        assertThat(profile.getProfileImageFile()).isNull();
        verifyNoInteractions(objectStorage, storedFiles);
    }

    @Test
    void throwsNotFoundWhenProfileDoesNotExist() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", pngBytes());
        when(profiles.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.uploadImage(99L, file))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Profile not found");
        verifyNoInteractions(uploadGuard, objectStorage, storedFiles);
    }

    private byte[] pngBytes() {
        return new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00};
    }
}
