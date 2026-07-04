// Copyright (c) Khaled Shawki. All rights reserved.

import BlueBarChart from '../components/BlueBarChart';
import BlueButton from '../components/BlueButton';
import BlueCard from '../components/BlueCard';
import BlueKpiGrid from '../components/BlueKpiGrid';
import BlueTable, { type BlueColumn } from '../components/BlueTable';
import ErrorState from '../components/ErrorState';
import LoadingState from '../components/LoadingState';
import { useGetCrmReportQuery } from '../analytics/analyticsApi';
import { useDownloadCrmSummaryReportMutation } from '../reports/reportsApi';
import { saveDownloadedReport } from '../reports/reportDownload';
import { useNotifications } from '../notifications/useNotifications';
import { CodeText } from '../i18n/components/CodeText';
import { DirectionalText } from '../i18n/components/DirectionalText';
import { useLocale } from '../i18n/LocaleProvider';
import { formatNumber } from '../i18n/formatters';
import type { ChartPoint, MarketingSourceReportRow, RecentBusinessPartner } from '../schema/types';

export default function ReportPage() {
  const { locale, t } = useLocale();
  const { notifyError, notifySuccess } = useNotifications();
  const { data, isLoading, error } = useGetCrmReportQuery();
  const [downloadReport, { isLoading: reportDownloading }] = useDownloadCrmSummaryReportMutation();

  async function exportReport() {
    try {
      const report = await downloadReport().unwrap();
      saveDownloadedReport(report);
      notifySuccess(t('reports.notifications.downloaded'));
    } catch {
      notifyError(t('reports.errors.export'));
    }
  }

  if (isLoading) return <LoadingState />;
  if (error || !data) return <ErrorState message={t('reports.errors.load')} />;

  const sourceColumns: BlueColumn<MarketingSourceReportRow>[] = [
    { key: 'marketingSource', header: t('report.columns.marketingSource'), render: (row) => <DirectionalText value={row.marketingSource} /> },
    { key: 'leads', header: t('report.columns.leads'), render: (row) => formatNumber(row.leads, locale), align: 'end' },
    { key: 'qualifiedLeads', header: t('report.columns.qualifiedLeads'), render: (row) => formatNumber(row.qualifiedLeads, locale), align: 'end' },
    { key: 'customers', header: t('navigation.customers'), render: (row) => formatNumber(row.customers, locale), align: 'end' },
    { key: 'rate', header: t('report.columns.qualificationRate'), render: (row) => `${formatNumber(Number(row.leadQualificationRate.toFixed(1)), locale)}%`, align: 'end' },
  ];

  const recentColumns: BlueColumn<RecentBusinessPartner>[] = [
    { key: 'kind', header: t('report.columns.type'), render: (row) => translateDataLabel(row.kind, t) },
    { key: 'code', header: t('schema.field.code'), render: (row) => <CodeText value={row.code} /> },
    { key: 'name', header: t('schema.field.name'), render: (row) => <DirectionalText value={row.name} /> },
    { key: 'status', header: t('schema.field.statusCode'), render: (row) => translateDataLabel(row.status, t) },
    { key: 'source', header: t('report.columns.marketingSource'), render: (row) => <DirectionalText value={row.marketingSource} /> },
  ];

  return (
    <div className="page-stack">
      <BlueCard
        eyebrow={t('reports.eyebrow')}
        title={t('reports.title')}
        action={(
          <BlueButton type="button" variant="secondary" disabled={reportDownloading} onClick={() => { void exportReport(); }}>
            {reportDownloading ? t('crm.actions.exporting') : t('crm.actions.export')}
          </BlueButton>
        )}
      >
        <BlueKpiGrid metrics={data.kpis} />
      </BlueCard>

      <div className="chart-grid">
        <BlueBarChart title={t('reports.charts.recordsByType')} points={translateChartPoints(data.kindBreakdown, t)} />
        <BlueBarChart title={t('reports.charts.recordsByStatus')} points={translateChartPoints(data.statusBreakdown, t)} />
        <BlueBarChart title={t('reports.charts.recordsBySource')} points={data.marketingSourceBreakdown} />
        <BlueBarChart title={t('reports.charts.contactsByRole')} points={data.contactPersonsByRole} />
        <BlueBarChart title={t('reports.charts.contactCoverage')} points={translateChartPoints(data.contactCoverageByKind, t)} />
      </div>

      <BlueCard eyebrow={t('reports.marketing.eyebrow')} title={t('reports.marketing.title')}>
        <BlueTable columns={sourceColumns} rows={data.marketingSourcePerformance} rowKey={(row) => row.marketingSource} />
      </BlueCard>

      <BlueCard eyebrow={t('reports.audit.eyebrow')} title={t('dashboard.recent.title')}>
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
