// Copyright (c) Khaled Shawki. All rights reserved.

import BlueBarChart from '../components/BlueBarChart';
import BlueCard from '../components/BlueCard';
import BlueKpiGrid from '../components/BlueKpiGrid';
import BlueTable, { type BlueColumn } from '../components/BlueTable';
import ErrorState from '../components/ErrorState';
import LoadingState from '../components/LoadingState';
import { useGetDashboardQuery } from '../analytics/analyticsApi';
import { CodeText } from '../i18n/components/CodeText';
import { DirectionalText } from '../i18n/components/DirectionalText';
import { useLocale } from '../i18n/LocaleProvider';
import type { ChartPoint, RecentBusinessPartner } from '../schema/types';

export default function DashboardPage() {
  const { t } = useLocale();
  const { data, isLoading, error } = useGetDashboardQuery();

  if (isLoading) return <LoadingState />;
  if (error || !data) return <ErrorState message={t('dashboard.errors.load')} />;

  const recentColumns: BlueColumn<RecentBusinessPartner>[] = [
    { key: 'kind', header: t('report.columns.type'), render: (row) => translateDataLabel(row.kind, t) },
    { key: 'code', header: t('schema.field.code'), render: (row) => <CodeText value={row.code} /> },
    { key: 'name', header: t('schema.field.name'), render: (row) => <DirectionalText value={row.name} /> },
    { key: 'status', header: t('schema.field.statusCode'), render: (row) => translateDataLabel(row.status, t) },
    { key: 'source', header: t('report.columns.marketingSource'), render: (row) => <DirectionalText value={row.marketingSource} /> },
  ];

  return (
    <div className="page-stack">
      <BlueCard eyebrow={t('dashboard.eyebrow')} title={t('navigation.dashboard')}>
        <BlueKpiGrid metrics={data.kpis} />
      </BlueCard>

      <div className="chart-grid">
        <BlueBarChart title={t('dashboard.charts.crmMix.title')} description={t('dashboard.charts.crmMix.description')} points={translateChartPoints(data.businessPartnersByKind, t)} />
        <BlueBarChart title={t('dashboard.charts.leadsBySource.title')} description={t('dashboard.charts.leadsBySource.description')} points={data.leadsByMarketingSource} />
        <BlueBarChart title={t('dashboard.charts.status.title')} description={t('dashboard.charts.status.description')} points={translateChartPoints(data.businessPartnersByStatus, t)} />
        <BlueBarChart title={t('dashboard.charts.newByMonth.title')} description={t('dashboard.charts.newByMonth.description')} points={data.newBusinessPartnersByMonth} labelKey="month" />
        <BlueBarChart title={t('dashboard.charts.contactsByRole.title')} description={t('dashboard.charts.contactsByRole.description')} points={data.contactPersonsByRole} />
        <BlueBarChart title={t('dashboard.charts.contactCoverage.title')} description={t('dashboard.charts.contactCoverage.description')} points={translateChartPoints(data.contactCoverageByKind, t)} />
      </div>

      <BlueCard eyebrow={t('dashboard.recent.eyebrow')} title={t('dashboard.recent.title')}>
        <BlueTable columns={recentColumns} rows={data.recentBusinessPartners} rowKey={(row) => row.id} />
      </BlueCard>
    </div>
  );
}

function translateChartPoints(points: ChartPoint[], t: (key: string) => string): ChartPoint[] {
  return points.map((point) => ({ ...point, label: translateDataLabel(point.label, t) }));
}

function translateDataLabel(value: string | null | undefined, t: (key: string) => string): string {
  if (!value) return '';
  const normalized = value.trim().toUpperCase().replace(/\s+/g, '_');
  const key = `data.label.${normalized}`;
  const translated = t(key);
  return translated === key ? value : translated;
}
