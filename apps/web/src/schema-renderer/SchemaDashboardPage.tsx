// Copyright (c) Khaled Shawki. All rights reserved.

import { useMemo, useState } from 'react';
import ErrorState from '../components/ErrorState';
import LoadingState from '../components/LoadingState';
import { useLocale } from '../i18n/LocaleProvider';
import { useGetScreenQuery } from '../schema/schemaApi';
import type { UiScreen, UiScreenLayout } from '../schema/types';
import SchemaFilterBar from './SchemaFilterBar';
import SchemaLayoutRenderer from './SchemaLayoutRenderer';
import { initialFilterValues, updateFilterValue, type SchemaFilterValues } from './schemaFilters';
import { useSchemaDataSources } from './useSchemaDataSources';

interface Props {
  screenKey: string;
}

export default function SchemaDashboardPage({ screenKey }: Props) {
  const { t } = useLocale();
  const { data: screen, isLoading: screenLoading, error: screenError } = useGetScreenQuery(screenKey);

  if (screenLoading) return <LoadingState />;
  if (screenError || !screen?.layout) return <ErrorState message={t('layout.errors.schema')} />;

  return <SchemaDashboardScreen key={screen.key} screen={screen} layout={screen.layout} />;
}

function SchemaDashboardScreen({ screen, layout }: { screen: UiScreen; layout: UiScreenLayout }) {
  const defaults = useMemo(() => initialFilterValues(screen.filters), [screen.filters]);
  const [filterValues, setFilterValues] = useState<SchemaFilterValues>(() => defaults);
  const dataSources = useSchemaDataSources(screen, filterValues);

  return (
    <div className="schema-dashboard-page">
      <SchemaFilterBar
        filters={screen.filters ?? []}
        values={filterValues}
        onChange={(key, value) => setFilterValues((current) => updateFilterValue(current, key, value))}
      />
      <SchemaLayoutRenderer layout={layout} dataSources={dataSources.values} dataSourceEntries={dataSources.entries} />
    </div>
  );
}
