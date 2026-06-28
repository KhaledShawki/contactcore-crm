// Copyright (c) Khaled Shawki. All rights reserved.

import type { ReactNode } from 'react';

export interface BlueColumn<T> {
  key: string;
  header: string;
  render: (row: T) => ReactNode;
  align?: 'left' | 'right';
}

interface Props<T> {
  columns: BlueColumn<T>[];
  rows: T[];
  rowKey: (row: T) => string | number;
  emptyText?: string;
}

export default function BlueTable<T>({ columns, rows, rowKey, emptyText = 'No records found.' }: Props<T>) {
  if (rows.length === 0) {
    return <div className="empty-state">{emptyText}</div>;
  }

  return (
    <div className="table-shell">
      <table className="blue-table">
        <thead>
          <tr>
            {columns.map((column) => <th key={column.key} className={column.align === 'right' ? 'is-right' : undefined}>{column.header}</th>)}
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={rowKey(row)}>
              {columns.map((column) => (
                <td key={column.key} data-label={column.header} className={column.align === 'right' ? 'is-right' : undefined}>{column.render(row)}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
