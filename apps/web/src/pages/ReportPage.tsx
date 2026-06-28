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
import type { MarketingSourceReportRow, RecentBusinessPartner } from '../schema/types';

const sourceColumns: BlueColumn<MarketingSourceReportRow>[] = [
  { key: 'marketingSource', header: 'Marketing source', render: (row) => row.marketingSource },
  { key: 'leads', header: 'Leads', render: (row) => row.leads },
  { key: 'qualifiedLeads', header: 'Qualified leads', render: (row) => row.qualifiedLeads },
  { key: 'customers', header: 'Customers', render: (row) => row.customers },
  { key: 'rate', header: 'Qualification rate', render: (row) => `${row.leadQualificationRate.toFixed(1)}%` },
];

const recentColumns: BlueColumn<RecentBusinessPartner>[] = [
  { key: 'kind', header: 'Type', render: (row) => row.kind },
  { key: 'code', header: 'Code', render: (row) => row.code },
  { key: 'name', header: 'Name', render: (row) => row.name },
  { key: 'status', header: 'Status', render: (row) => row.status },
  { key: 'source', header: 'Marketing source', render: (row) => row.marketingSource },
];

export default function ReportPage() {
  const { notifyError, notifySuccess } = useNotifications();
  const { data, isLoading, error } = useGetCrmReportQuery();
  const [downloadReport, { isLoading: reportDownloading }] = useDownloadCrmSummaryReportMutation();

  async function exportReport() {
    try {
      const report = await downloadReport().unwrap();
      saveDownloadedReport(report);
      notifySuccess('CRM report downloaded.');
    } catch {
      notifyError('Could not generate the CRM report. Try again.');
    }
  }

  if (isLoading) return <LoadingState />;
  if (error || !data) return <ErrorState message="Could not load CRM report." />;

  return (
    <div className="page-stack">
      <BlueCard
        eyebrow="Reports"
        title="CRM Report"
        action={(
          <BlueButton type="button" variant="secondary" disabled={reportDownloading} onClick={() => { void exportReport(); }}>
            {reportDownloading ? 'Exporting...' : 'Export XLSX'}
          </BlueButton>
        )}
      >
        <BlueKpiGrid metrics={data.kpis} />
      </BlueCard>

      <div className="chart-grid">
        <BlueBarChart title="Records by type" points={data.kindBreakdown} />
        <BlueBarChart title="Records by status" points={data.statusBreakdown} />
        <BlueBarChart title="Records by marketing source" points={data.marketingSourceBreakdown} />
        <BlueBarChart title="Contact persons by role" points={data.contactPersonsByRole} />
        <BlueBarChart title="Contact coverage by type" points={data.contactCoverageByKind} />
      </div>

      <BlueCard eyebrow="Marketing" title="Marketing Source Performance">
        <BlueTable columns={sourceColumns} rows={data.marketingSourcePerformance} rowKey={(row) => row.marketingSource} />
      </BlueCard>

      <BlueCard eyebrow="Audit" title="Recent CRM Records">
        <BlueTable columns={recentColumns} rows={data.recentBusinessPartners} rowKey={(row) => row.id} />
      </BlueCard>
    </div>
  );
}
