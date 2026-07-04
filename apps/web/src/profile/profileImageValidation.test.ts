// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { validateProfileImageFile } from './profileImageValidation';

const maxProfileImageBytes = 5 * 1024 * 1024;

describe('validateProfileImageFile', () => {
  it('accepts empty selection so the UI can ignore cancelled file dialogs', () => {
    expect(validateProfileImageFile(null)).toEqual({ valid: true, messageKey: null });
  });

  it('accepts supported image types within size limit', () => {
    expect(validateProfileImageFile({ type: 'image/png', size: 1024 })).toEqual({ valid: true, messageKey: null });
    expect(validateProfileImageFile({ type: 'image/jpeg', size: maxProfileImageBytes })).toEqual({ valid: true, messageKey: null });
    expect(validateProfileImageFile({ type: 'image/webp', size: 4096 })).toEqual({ valid: true, messageKey: null });
  });

  it('rejects unsupported types before the request is sent', () => {
    expect(validateProfileImageFile({ type: 'application/javascript', size: 10 })).toEqual({
      valid: false,
      messageKey: 'profile.image.validation.unsupportedType',
    });
  });

  it('rejects oversized files before the request is sent', () => {
    expect(validateProfileImageFile({ type: 'image/png', size: maxProfileImageBytes + 1 })).toEqual({
      valid: false,
      messageKey: 'profile.image.validation.tooLarge',
    });
  });
});
