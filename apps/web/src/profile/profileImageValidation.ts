// Copyright (c) Khaled Shawki. All rights reserved.

const allowedProfileImageTypes = ['image/jpeg', 'image/png', 'image/webp'] as const;
const maxProfileImageBytes = 5 * 1024 * 1024;

export type ProfileImageValidationResult = {
  valid: boolean;
  message: string | null;
};

export function validateProfileImageFile(file: Pick<File, 'type' | 'size'> | null): ProfileImageValidationResult {
  if (!file) {
    return { valid: true, message: null };
  }
  if (!allowedProfileImageTypes.includes(file.type as (typeof allowedProfileImageTypes)[number])) {
    return { valid: false, message: 'Only JPEG, PNG, and WebP profile images are allowed.' };
  }
  if (file.size > maxProfileImageBytes) {
    return { valid: false, message: 'Profile image must be 5 MB or smaller.' };
  }
  return { valid: true, message: null };
}
