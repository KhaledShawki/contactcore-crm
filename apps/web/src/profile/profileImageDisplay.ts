// Copyright (c) Khaled Shawki. All rights reserved.

export interface LocalProfileImagePreviewState {
  objectUrl: string;
  expectedProfileImageUrl: string | null;
}

interface SelectVisibleProfileImageUrlInput {
  localPreview: LocalProfileImagePreviewState | null;
  profileImageUrl: string | null | undefined;
  serverImageObjectUrl: string | null | undefined;
}

export function shouldShowLocalProfileImagePreview({
  localPreview,
  profileImageUrl,
  serverImageObjectUrl,
}: SelectVisibleProfileImageUrlInput): boolean {
  if (!localPreview) return false;
  if (!localPreview.expectedProfileImageUrl) return true;
  if (profileImageUrl !== localPreview.expectedProfileImageUrl) return true;
  return !serverImageObjectUrl;
}

export function selectVisibleProfileImageUrl(input: SelectVisibleProfileImageUrlInput): string | null {
  return shouldShowLocalProfileImagePreview(input)
    ? input.localPreview?.objectUrl ?? null
    : input.serverImageObjectUrl ?? null;
}
