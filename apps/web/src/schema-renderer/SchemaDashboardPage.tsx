// Copyright (c) Khaled Shawki. All rights reserved.

import ErrorState from '../components/ErrorState';
import LoadingState from '../components/LoadingState';
import { useLocale } from '../i18n/LocaleProvider';
import { useGetScreenQuery } from '../schema/schemaApi';
import SchemaLayoutRenderer from './SchemaLayoutRenderer';
import { useSchemaDataSources } from './useSchemaDataSources';

interface Props {
  screenKey: string;
}

export default function SchemaDashboardPage({ screenKey }: Props) {
  const { t } = useLocale();
  const { data: screen, isLoading: screenLoading, error: screenError } = useGetScreenQuery(screenKey);
  const dataSources = useSchemaDataSources(screen);

  if (screenLoading || dataSources.isLoading) return <LoadingState />;
  if (screenError || !screen?.layout) return <ErrorState message={t('layout.errors.schema')} />;
  if (dataSources.hasError) return <ErrorState message={t('dashboard.errors.load')} />;

  return <SchemaLayoutRenderer layout={screen.layout} dataSources={dataSources.values} />;
}
