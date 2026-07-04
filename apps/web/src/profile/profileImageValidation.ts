// Copyright (c) Khaled Shawki. All rights reserved.

const allowedProfileImageTypes = ['image/jpeg', 'image/png', 'image/webp'] as const;
const maxProfileImageBytes = 5 * 1024 * 1024;

export type ProfileImageValidationResult = {
  valid: boolean;
  messageKey: string | null;
};

export function validateProfileImageFile(file: Pick<File, 'type' | 'size'> | null): ProfileImageValidationResult {
  if (!file) {
    return { valid: true, messageKey: null };
  }
  if (!allowedProfileImageTypes.includes(file.type as (typeof allowedProfileImageTypes)[number])) {
    return { valid: false, messageKey: 'profile.image.validation.unsupportedType' };
  }
  if (file.size > maxProfileImageBytes) {
    return { valid: false, messageKey: 'profile.image.validation.tooLarge' };
  }
  return { valid: true, messageKey: null };
}
