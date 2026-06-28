// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.profile.application;

import com.contactcore.profile.api.UserProfileResponse;
import com.contactcore.profile.api.UserProfileWriteRequest;
import com.contactcore.profile.domain.UserProfile;
import com.contactcore.profile.domain.UserProfileRepository;
import com.contactcore.shared.api.NotFoundException;
import com.contactcore.storage.application.ObjectStorageService;
import com.contactcore.storage.application.StoredObject;
import com.contactcore.storage.application.StoredObjectContent;
import com.contactcore.storage.application.ValidatedFileUpload;
import com.contactcore.storage.domain.StoredFile;
import com.contactcore.storage.security.FileUploadGuard;
import com.contactcore.storage.security.UploadPurpose;
import com.contactcore.storage.domain.StoredFileRepository;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserProfileService {
    private final UserProfileRepository profiles;
    private final StoredFileRepository storedFiles;
    private final ObjectStorageService objectStorage;
    private final FileUploadGuard uploadGuard;

    public UserProfileService(UserProfileRepository profiles, StoredFileRepository storedFiles, ObjectStorageService objectStorage,
                              FileUploadGuard uploadGuard) {
        this.profiles = profiles;
        this.storedFiles = storedFiles;
        this.objectStorage = objectStorage;
        this.uploadGuard = uploadGuard;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse get(Long userId) {
        return toResponse(findProfile(userId));
    }

    @Transactional
    public UserProfileResponse update(Long userId, UserProfileWriteRequest request) {
        UserProfile profile = findProfile(userId);
        profile.update(
                trimRequired(request.displayName()),
                trim(request.phone()),
                trim(request.jobTitle()),
                trim(request.bio()),
                trimRequired(request.locale()),
                trimRequired(request.timezone())
        );
        return toResponse(profile);
    }

    @Transactional
    public UserProfileResponse uploadImage(Long userId, MultipartFile file) throws IOException {
        UserProfile profile = findProfile(userId);
        StoredFile previousImage = profile.getProfileImageFile();
        ValidatedFileUpload upload = uploadGuard.validateAndScan(UploadPurpose.PROFILE_IMAGE, file);
        StoredObject storedObject = objectStorage.upload("profile-images/" + userId, upload);
        StoredFile storedFile = storedFiles.save(new StoredFile(
                storedObject.objectKey(),
                storedObject.originalFilename(),
                storedObject.contentType(),
                storedObject.sizeBytes()
        ));

        profile.setProfileImageFile(storedFile);
        if (previousImage != null) {
            storedFiles.delete(previousImage);
        }
        registerProfileImageStorageCleanup(previousImage, storedFile);
        return toResponse(profile);
    }

    private UserProfile findProfile(Long userId) {
        return profiles.findByUserId(userId).orElseThrow(() -> new NotFoundException("Profile not found."));
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        StoredFile image = profile.getProfileImageFile();
        return new UserProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getUser().getUsername(),
                profile.getUser().getEmail(),
                profile.getDisplayName(),
                profile.getPhone(),
                profile.getJobTitle(),
                profile.getBio(),
                profile.getLocale(),
                profile.getTimezone(),
                image == null ? null : "/api/profile/image/content?v=" + image.getId()
        );
    }

    @Transactional(readOnly = true)
    public StoredObjectContent downloadProfileImage(Long userId) throws IOException {
        UserProfile profile = findProfile(userId);
        StoredFile image = profile.getProfileImageFile();
        if (image == null) {
            throw new NotFoundException("Profile image not found.");
        }
        return objectStorage.download(image.getObjectKey(), image.getContentType(), image.getSizeBytes());
    }

    private void registerProfileImageStorageCleanup(StoredFile previousImage, StoredFile newImage) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_COMMITTED) {
                    deleteQuietly(previousImage);
                } else {
                    deleteQuietly(newImage);
                }
            }
        });
    }

    private void deleteQuietly(StoredFile file) {
        if (file == null) {
            return;
        }
        try {
            objectStorage.delete(file.getObjectKey());
        } catch (IOException ignored) {
            // Best-effort cleanup only. The profile row already points to the committed image.
        }
    }

    private String trimRequired(String value) {
        return value.trim();
    }

    private String trim(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
