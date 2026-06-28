// Copyright (c) Khaled Shawki. All rights reserved.

export interface PaginationSummary {
  currentPage: number;
  safeTotalPages: number;
  from: number;
  to: number;
  canGoFirst: boolean;
  canGoPrevious: boolean;
  canGoNext: boolean;
}

export function buildPaginationSummary(page: number, size: number, totalElements: number, totalPages: number): PaginationSummary {
  const safeSize = Math.max(size, 1);
  const safeTotalElements = Math.max(totalElements, 0);
  const safeTotalPages = Math.max(totalPages, 1);
  const currentPage = Math.min(Math.max(page, 0), safeTotalPages - 1);
  const from = safeTotalElements === 0 ? 0 : currentPage * safeSize + 1;
  const to = Math.min((currentPage + 1) * safeSize, safeTotalElements);

  return {
    currentPage,
    safeTotalPages,
    from,
    to,
    canGoFirst: currentPage > 0,
    canGoPrevious: currentPage > 0,
    canGoNext: currentPage + 1 < safeTotalPages,
  };
}
