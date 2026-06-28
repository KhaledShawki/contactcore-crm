// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { profileImageContentEndpoint } from './profileApi';

describe('profileImageContentEndpoint', () => {
  it('keeps the backend image version query parameter', () => {
    expect(profileImageContentEndpoint('/api/profile/image/content?v=42')).toBe('/profile/image/content?v=42');
  });

  it('returns the default content endpoint for blank image urls', () => {
    expect(profileImageContentEndpoint('   ')).toBe('/profile/image/content');
  });

  it('preserves already-normalized API paths', () => {
    expect(profileImageContentEndpoint('/profile/image/content?v=7')).toBe('/profile/image/content?v=7');
  });
});
