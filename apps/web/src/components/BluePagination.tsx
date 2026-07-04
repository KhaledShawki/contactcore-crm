// Copyright (c) Khaled Shawki. All rights reserved.

import BlueButton from './BlueButton';
import { buildPaginationSummary } from './paginationModel';
import { useLocale } from '../i18n/LocaleProvider';
import { formatNumber } from '../i18n/formatters';

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
  const { locale, t } = useLocale();
  const summary = buildPaginationSummary(page, size, totalElements, totalPages);

  return (
    <div className="blue-pagination" aria-label={t('pagination.aria')}>
      <div className="blue-pagination__summary">
        <strong>{formatNumber(summary.from, locale)}-{formatNumber(summary.to, locale)}</strong>
        <span>{t('pagination.of', { total: formatNumber(totalElements, locale) })}</span>
      </div>
      <label className="blue-pagination__size">
        <span>{t('pagination.rows')}</span>
        <select value={size} disabled={disabled} onChange={(event) => onSizeChange(Number(event.target.value))}>
          {pageSizeOptions.map((option) => <option key={option} value={option}>{formatNumber(option, locale)}</option>)}
        </select>
      </label>
      <div className="blue-pagination__actions">
        <BlueButton variant="secondary" disabled={disabled || !summary.canGoFirst} onClick={() => onPageChange(0)}>{t('pagination.first')}</BlueButton>
        <BlueButton variant="secondary" disabled={disabled || !summary.canGoPrevious} onClick={() => onPageChange(summary.currentPage - 1)}>{t('pagination.previous')}</BlueButton>
        <span className="blue-pagination__page">{t('pagination.pageOf', {
          page: formatNumber(summary.currentPage + 1, locale),
          total: formatNumber(summary.safeTotalPages, locale),
        })}</span>
        <BlueButton variant="secondary" disabled={disabled || !summary.canGoNext} onClick={() => onPageChange(summary.currentPage + 1)}>{t('pagination.next')}</BlueButton>
      </div>
    </div>
  );
}
