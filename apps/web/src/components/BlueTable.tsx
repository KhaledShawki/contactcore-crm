// Copyright (c) Khaled Shawki. All rights reserved.

import type { ReactNode } from 'react';
import { useLocale } from '../i18n/LocaleProvider';

export interface BlueColumn<T> {
  key: string;
  header: string;
  render: (row: T) => ReactNode;
  align?: 'start' | 'end' | 'left' | 'right';
}

interface Props<T> {
  columns: BlueColumn<T>[];
  rows: T[];
  rowKey: (row: T) => string | number;
  emptyText?: string;
}

export default function BlueTable<T>({ columns, rows, rowKey, emptyText }: Props<T>) {
  const { t } = useLocale();
  if (rows.length === 0) {
    return <div className="empty-state">{emptyText ?? t('common.empty.noRecords')}</div>;
  }

  return (
    <div className="table-shell">
      <table className="blue-table">
        <thead>
          <tr>
            {columns.map((column) => <th key={column.key} className={isEndAligned(column.align) ? 'is-right' : undefined}>{column.header}</th>)}
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={rowKey(row)}>
              {columns.map((column) => (
                <td key={column.key} data-label={column.header} className={isEndAligned(column.align) ? 'is-right' : undefined}>{column.render(row)}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function isEndAligned(align: BlueColumn<unknown>['align']) {
  return align === 'end' || align === 'right';
}
