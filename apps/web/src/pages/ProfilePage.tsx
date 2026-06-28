// Copyright (c) Khaled Shawki. All rights reserved.

import { skipToken } from '@reduxjs/toolkit/query';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import BlueAlert from '../components/BlueAlert';
import BlueCard from '../components/BlueCard';
import ErrorState from '../components/ErrorState';
import LoadingState from '../components/LoadingState';
import SchemaForm from '../components/SchemaForm';
import {
  useGetProfileImageQuery,
  useGetProfileQuery,
  useUpdateProfileMutation,
  useUploadProfileImageMutation,
} from '../profile/profileApi';
import { isFormModified } from '../forms/formComparison';
import { useSingleFlightAction } from '../hooks/useSingleFlightAction';
import { toWritablePayload, type SchemaRecord } from '../schema/schemaValues';
import { useGetScreenQuery } from '../schema/schemaApi';
import type { UiScreen, UserProfile } from '../schema/types';
import { selectVisibleProfileImageUrl, type LocalProfileImagePreviewState } from '../profile/profileImageDisplay';
import { validateProfileImageFile } from '../profile/profileImageValidation';
import { useNotifications } from '../notifications/useNotifications';

interface ProfileEditorProps {
  profile: UserProfile;
  screen: UiScreen;
}

function ProfileEditor({ profile, screen }: ProfileEditorProps) {
  const profileImageQueryArg = profile.profileImageUrl?.trim() ? profile.profileImageUrl : skipToken;
  const { currentData: serverProfileImageUrl, isFetching: imageLoading } = useGetProfileImageQuery(profileImageQueryArg);
  const [updateProfile, { isLoading: saving }] = useUpdateProfileMutation();
  const [uploadProfileImage, { isLoading: uploading }] = useUploadProfileImageMutation();
  const [formValue, setFormValue] = useState<SchemaRecord>(profile as unknown as SchemaRecord);
  const [savedPayload, setSavedPayload] = useState<SchemaRecord>(() => toWritablePayload(screen, profile as unknown as SchemaRecord));
  const [uploadError, setUploadError] = useState<string | null>(null);
  const [localPreview, setLocalPreview] = useState<LocalProfileImagePreviewState | null>(null);
  const localPreviewUrlRef = useRef<string | null>(null);
  const { notifySuccess, notifyError } = useNotifications();
  const currentPayload = useMemo(() => toWritablePayload(screen, formValue), [formValue, screen]);
  const formModified = isFormModified(savedPayload, currentPayload);

  const revokeLocalPreviewUrl = useCallback(() => {
    if (localPreviewUrlRef.current) {
      URL.revokeObjectURL(localPreviewUrlRef.current);
      localPreviewUrlRef.current = null;
    }
  }, []);

  const clearLocalPreview = useCallback(() => {
    revokeLocalPreviewUrl();
    setLocalPreview(null);
  }, [revokeLocalPreviewUrl]);

  const setLocalPreviewFromFile = useCallback((file: File) => {
    if (localPreviewUrlRef.current) {
      URL.revokeObjectURL(localPreviewUrlRef.current);
    }
    const objectUrl = URL.createObjectURL(file);
    localPreviewUrlRef.current = objectUrl;
    setLocalPreview({ objectUrl, expectedProfileImageUrl: null });
  }, []);

  const markLocalPreviewAsServerBacked = useCallback((profileImageUrl: string | null | undefined) => {
    setLocalPreview((currentPreview) => (
      currentPreview ? { ...currentPreview, expectedProfileImageUrl: profileImageUrl ?? null } : null
    ));
  }, []);

  useEffect(() => revokeLocalPreviewUrl, [revokeLocalPreviewUrl]);

  const saveTask = useSingleFlightAction(async () => {
    if (!formModified) return;
    try {
      const saved = await updateProfile(currentPayload).unwrap();
      const nextValue = saved as unknown as SchemaRecord;
      const nextPayload = toWritablePayload(screen, nextValue);
      setFormValue(nextValue);
      setSavedPayload(nextPayload);
      notifySuccess('Profile saved.');
    } catch {
      notifyError('Could not save the profile. Check the entered values and try again.');
    }
  });

  const uploadTask = useSingleFlightAction(async (file: File | null) => {
    setUploadError(null);
    const validation = validateProfileImageFile(file);
    if (!validation.valid) {
      setUploadError(validation.message);
      if (validation.message) notifyError(validation.message);
      return;
    }
    if (!file) return;
    setLocalPreviewFromFile(file);
    try {
      const saved = await uploadProfileImage(file).unwrap();
      markLocalPreviewAsServerBacked(saved.profileImageUrl);
      notifySuccess('Profile image uploaded.');
    } catch {
      clearLocalPreview();
      const message = 'Profile image upload failed. Check that MinIO and the file scanner are running, and that the file is safe.';
      setUploadError(message);
      notifyError(message);
    }
  });

  const saveBusy = saving || saveTask.running;
  const uploadBusy = uploading || uploadTask.running;
  const visibleProfileImageUrl = selectVisibleProfileImageUrl({
    localPreview,
    profileImageUrl: profile.profileImageUrl,
    serverImageObjectUrl: serverProfileImageUrl,
  });
  const visibleLocalPreviewUrl = visibleProfileImageUrl === localPreview?.objectUrl ? visibleProfileImageUrl : null;

  return (
    <BlueCard eyebrow="Settings" title="User Profile">
      <div className="profile-grid">
        <div className="profile-image-card">
          {visibleProfileImageUrl ? (
            <img
              src={visibleProfileImageUrl}
              alt="Profile"
              onLoad={() => {
                if (!visibleLocalPreviewUrl) {
                  clearLocalPreview();
                }
              }}
            />
          ) : (
            <div className="profile-placeholder">{imageLoading ? 'Loading image' : 'Profile Image'}</div>
          )}
          <label className="profile-upload-control">
            <span>{uploadBusy ? 'Uploading...' : 'Upload profile image'}</span>
            <input
              type="file"
              accept="image/png,image/jpeg,image/webp"
              disabled={uploadBusy}
              onChange={(event) => {
                const file = event.target.files?.[0] ?? null;
                event.currentTarget.value = '';
                void uploadTask.run(file);
              }}
            />
          </label>
          {uploadError ? <BlueAlert tone="error" message={uploadError} /> : null}
        </div>
        <SchemaForm
          screen={screen}
          value={formValue}
          onChange={setFormValue}
          onSubmit={() => { void saveTask.run(); }}
          submitLabel="Save Profile"
          busy={saveBusy}
          canSubmit={formModified}
          submitDisabledReason="No changes to save."
        />
      </div>
    </BlueCard>
  );
}

export default function ProfilePage() {
  const { data: screen, isLoading: screenLoading, error: screenError } = useGetScreenQuery('profile');
  const { data: profile, isLoading: profileLoading, error: profileError } = useGetProfileQuery();

  if (screenLoading || profileLoading) return <LoadingState />;
  if (screenError || profileError || !screen || !profile) return <ErrorState message="Could not load profile." />;

  return <ProfileEditor key={String(profile.id ?? profile.userId)} profile={profile} screen={screen} />;
}
