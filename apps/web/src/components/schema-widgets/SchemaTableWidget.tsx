// Copyright (c) Khaled Shawki. All rights reserved.

import BlueCard from '../BlueCard';
import BlueTable, { type BlueColumn } from '../BlueTable';
import { CodeText } from '../../i18n/components/CodeText';
import { DirectionalText } from '../../i18n/components/DirectionalText';
import { useLocale } from '../../i18n/LocaleProvider';
import type { UiWidget, UiWidgetTableColumn } from '../../schema/types';
import { asArray, readPath } from '../../schema-renderer/schemaData';
import { translatedLabel, translateDataLabel } from '../../schema-renderer/schemaLabels';

interface Props {
  widget: UiWidget;
  source: unknown;
}

type Row = Record<string, unknown>;

export default function SchemaTableWidget({ widget, source }: Props) {
  const { t } = useLocale();
  const rows = asArray<Row>(readPath(source, widget.dataPath));
  const columns = (widget.tableColumns ?? []).map((column): BlueColumn<Row> => ({
    key: column.key,
    header: translatedLabel(column.titleKey, column.title, t),
    render: (row) => renderCell(row[column.key], column, t),
  }));

  return (
    <BlueCard eyebrow={translatedLabel(widget.descriptionKey, widget.description, t)} title={translatedLabel(widget.titleKey, widget.title, t)}>
      <BlueTable columns={columns} rows={rows} rowKey={rowKey} />
    </BlueCard>
  );
}

function renderCell(value: unknown, column: UiWidgetTableColumn, t: (key: string) => string) {
  const text = value == null ? '' : String(value);
  if (column.valueKind === 'code') return <CodeText value={text} />;
  if (column.valueKind === 'translated') return translateDataLabel(text, t);
  return <DirectionalText value={text} />;
}

function rowKey(row: Row): string | number {
  const id = row.id;
  if (typeof id === 'string' || typeof id === 'number') return id;
  const code = row.code;
  if (typeof code === 'string' || typeof code === 'number') return code;
  return JSON.stringify(row);
}
