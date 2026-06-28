// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { filenameFromContentDisposition } from './reportDownload';

describe('filenameFromContentDisposition', () => {
  it('reads quoted filenames', () => {
    expect(filenameFromContentDisposition('attachment; filename="contactcore-customers-2026-06-26.xlsx"')).toBe('contactcore-customers-2026-06-26.xlsx');
  });

  it('reads utf8 filenames', () => {
    expect(filenameFromContentDisposition("attachment; filename*=UTF-8''contactcore-leads-2026-06-26.xlsx")).toBe('contactcore-leads-2026-06-26.xlsx');
  });

  it('falls back for unsafe filenames', () => {
    expect(filenameFromContentDisposition('attachment; filename="../evil.txt"')).toBe('contactcore-report.xlsx');
  });
});
