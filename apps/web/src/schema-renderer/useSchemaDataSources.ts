// Copyright (c) Khaled Shawki. All rights reserved.

import { useGetDashboardQuery } from '../analytics/analyticsApi';
import type { UiScreen } from '../schema/types';

export interface SchemaDataSourceState {
  values: Record<string, unknown>;
  isLoading: boolean;
  hasError: boolean;
}

export function useSchemaDataSources(screen: UiScreen | undefined): SchemaDataSourceState {
  const requiresAnalyticsDashboard = Boolean(screen?.layout?.sections.some((section) =>
    section.widgets.some((widget) => widget.dataSource?.key === 'analytics.dashboard')));
  const dashboard = useGetDashboardQuery(undefined, { skip: !requiresAnalyticsDashboard });

  return {
    values: {
      ...(dashboard.data ? { 'analytics.dashboard': dashboard.data } : {}),
    },
    isLoading: dashboard.isLoading || dashboard.isFetching,
    hasError: Boolean(dashboard.error),
  };
}
