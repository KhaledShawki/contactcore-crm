// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { selectVisibleProfileImageUrl, shouldShowLocalProfileImagePreview } from './profileImageDisplay';

const localPreview = {
  objectUrl: 'blob:local-preview',
  expectedProfileImageUrl: '/api/profile/image/content?v=7',
};

describe('profile image display selection', () => {
  it('shows the local preview while the upload is still pending', () => {
    expect(selectVisibleProfileImageUrl({
      localPreview: { objectUrl: 'blob:local-preview', expectedProfileImageUrl: null },
      profileImageUrl: '/api/profile/image/content?v=6',
      serverImageObjectUrl: 'blob:old-server-image',
    })).toBe('blob:local-preview');
  });

  it('keeps the local preview after the profile URL changes until the matching server image is ready', () => {
    expect(shouldShowLocalProfileImagePreview({
      localPreview,
      profileImageUrl: '/api/profile/image/content?v=7',
      serverImageObjectUrl: null,
    })).toBe(true);

    expect(selectVisibleProfileImageUrl({
      localPreview,
      profileImageUrl: '/api/profile/image/content?v=7',
      serverImageObjectUrl: null,
    })).toBe('blob:local-preview');
  });

  it('switches to the server-backed image only after it is available for the current profile URL', () => {
    expect(selectVisibleProfileImageUrl({
      localPreview,
      profileImageUrl: '/api/profile/image/content?v=7',
      serverImageObjectUrl: 'blob:server-image-v7',
    })).toBe('blob:server-image-v7');
  });

  it('uses the server image when there is no local preview', () => {
    expect(selectVisibleProfileImageUrl({
      localPreview: null,
      profileImageUrl: '/api/profile/image/content?v=7',
      serverImageObjectUrl: 'blob:server-image-v7',
    })).toBe('blob:server-image-v7');
  });
});
