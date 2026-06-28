// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { validateProfileImageFile } from './profileImageValidation';

const maxProfileImageBytes = 5 * 1024 * 1024;

describe('validateProfileImageFile', () => {
  it('accepts empty selection so the UI can ignore cancelled file dialogs', () => {
    expect(validateProfileImageFile(null)).toEqual({ valid: true, message: null });
  });

  it('accepts supported image types within size limit', () => {
    expect(validateProfileImageFile({ type: 'image/png', size: 1024 })).toEqual({ valid: true, message: null });
    expect(validateProfileImageFile({ type: 'image/jpeg', size: maxProfileImageBytes })).toEqual({ valid: true, message: null });
    expect(validateProfileImageFile({ type: 'image/webp', size: 4096 })).toEqual({ valid: true, message: null });
  });

  it('rejects unsupported types before the request is sent', () => {
    expect(validateProfileImageFile({ type: 'application/javascript', size: 10 })).toEqual({
      valid: false,
      message: 'Only JPEG, PNG, and WebP profile images are allowed.',
    });
  });

  it('rejects oversized files before the request is sent', () => {
    expect(validateProfileImageFile({ type: 'image/png', size: maxProfileImageBytes + 1 })).toEqual({
      valid: false,
      message: 'Profile image must be 5 MB or smaller.',
    });
  });
});
