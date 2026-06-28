// Copyright (c) Khaled Shawki. All rights reserved.

import BlueButton from './BlueButton';
import { buildPaginationSummary } from './paginationModel';

interface Props {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  onSizeChange: (size: number) => void;
  disabled?: boolean;
}

const pageSizeOptions = [10, 20, 50, 100];

export default function BluePagination({ page, size, totalElements, totalPages, onPageChange, onSizeChange, disabled = false }: Props) {
  const summary = buildPaginationSummary(page, size, totalElements, totalPages);

  return (
    <div className="blue-pagination" aria-label="Pagination">
      <div className="blue-pagination__summary">
        <strong>{summary.from}-{summary.to}</strong>
        <span>of {totalElements}</span>
      </div>
      <label className="blue-pagination__size">
        <span>Rows</span>
        <select value={size} disabled={disabled} onChange={(event) => onSizeChange(Number(event.target.value))}>
          {pageSizeOptions.map((option) => <option key={option} value={option}>{option}</option>)}
        </select>
      </label>
      <div className="blue-pagination__actions">
        <BlueButton variant="secondary" disabled={disabled || !summary.canGoFirst} onClick={() => onPageChange(0)}>First</BlueButton>
        <BlueButton variant="secondary" disabled={disabled || !summary.canGoPrevious} onClick={() => onPageChange(summary.currentPage - 1)}>Prev</BlueButton>
        <span className="blue-pagination__page">Page {summary.currentPage + 1} / {summary.safeTotalPages}</span>
        <BlueButton variant="secondary" disabled={disabled || !summary.canGoNext} onClick={() => onPageChange(summary.currentPage + 1)}>Next</BlueButton>
      </div>
    </div>
  );
}
