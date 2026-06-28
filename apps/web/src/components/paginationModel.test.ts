// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { buildPaginationSummary } from './paginationModel';

describe('buildPaginationSummary', () => {
  it('builds a stable first-page summary', () => {
    expect(buildPaginationSummary(0, 20, 53, 3)).toEqual({
      currentPage: 0,
      safeTotalPages: 3,
      from: 1,
      to: 20,
      canGoFirst: false,
      canGoPrevious: false,
      canGoNext: true,
    });
  });

  it('clamps invalid pages and handles empty result sets', () => {
    expect(buildPaginationSummary(99, 20, 0, 0)).toEqual({
      currentPage: 0,
      safeTotalPages: 1,
      from: 0,
      to: 0,
      canGoFirst: false,
      canGoPrevious: false,
      canGoNext: false,
    });
  });
});
