// Copyright (c) Khaled Shawki. All rights reserved.

import ErrorState from '../components/ErrorState';
import SchemaBarChartWidget from '../components/schema-widgets/SchemaBarChartWidget';
import SchemaKpiGridWidget from '../components/schema-widgets/SchemaKpiGridWidget';
import SchemaLineChartWidget from '../components/schema-widgets/SchemaLineChartWidget';
import SchemaTableWidget from '../components/schema-widgets/SchemaTableWidget';
import { useLocale } from '../i18n/LocaleProvider';
import type { UiWidget } from '../schema/types';
import type { SchemaDataSourceEntry } from './useSchemaDataSources';
import SchemaWidgetState from './SchemaWidgetState';

interface Props {
  widget: UiWidget;
  dataSources: Record<string, unknown>;
  dataSourceEntries: Record<string, SchemaDataSourceEntry>;
}

export default function SchemaWidgetRenderer({ widget, dataSources, dataSourceEntries }: Props) {
  const { t } = useLocale();
  if (widget.visible === false) return null;

  const sourceKey = widget.dataSource?.key;
  const source = sourceKey ? dataSources[sourceKey] : undefined;
  const entry = sourceKey ? dataSourceEntries[sourceKey] : undefined;

  if (sourceKey) {
    if (!entry) {
      return <ErrorState message={t('schema.widgets.unsupported')} />;
    }
    if (entry.status !== 'ready') {
      return <SchemaWidgetState status={entry.status} />;
    }
  }

  switch (widget.type) {
    case 'kpiGrid':
      return <SchemaKpiGridWidget widget={widget} source={source} />;
    case 'barChart':
      return <SchemaBarChartWidget widget={widget} source={source} />;
    case 'lineChart':
      return <SchemaLineChartWidget widget={widget} source={source} />;
    case 'table':
      return <SchemaTableWidget widget={widget} source={source} />;
    default:
      return <ErrorState message={t('schema.widgets.unsupported')} />;
  }
}
