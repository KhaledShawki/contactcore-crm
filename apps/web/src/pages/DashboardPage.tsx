// Copyright (c) Khaled Shawki. All rights reserved.

import BlueBarChart from '../components/BlueBarChart';
import BlueCard from '../components/BlueCard';
import BlueKpiGrid from '../components/BlueKpiGrid';
import BlueTable, { type BlueColumn } from '../components/BlueTable';
import ErrorState from '../components/ErrorState';
import LoadingState from '../components/LoadingState';
import { useGetDashboardQuery } from '../analytics/analyticsApi';
import type { RecentBusinessPartner } from '../schema/types';

const recentColumns: BlueColumn<RecentBusinessPartner>[] = [
  { key: 'kind', header: 'Type', render: (row) => row.kind },
  { key: 'code', header: 'Code', render: (row) => row.code },
  { key: 'name', header: 'Name', render: (row) => row.name },
  { key: 'status', header: 'Status', render: (row) => row.status },
  { key: 'source', header: 'Marketing source', render: (row) => row.marketingSource },
];

export default function DashboardPage() {
  const { data, isLoading, error } = useGetDashboardQuery();

  if (isLoading) return <LoadingState />;
  if (error || !data) return <ErrorState message="Could not load dashboard." />;

  return (
    <div className="page-stack">
      <BlueCard eyebrow="Overview" title="Dashboard">
        <BlueKpiGrid metrics={data.kpis} />
      </BlueCard>

      <div className="chart-grid">
        <BlueBarChart title="CRM mix" description="Active records by CRM type." points={data.businessPartnersByKind} />
        <BlueBarChart title="Leads by marketing source" description="Active leads grouped by source." points={data.leadsByMarketingSource} />
        <BlueBarChart title="Status breakdown" description="Active records grouped by lifecycle status." points={data.businessPartnersByStatus} />
        <BlueBarChart title="New records by month" description="Created CRM records during the last twelve months." points={data.newBusinessPartnersByMonth} labelKey="month" />
        <BlueBarChart title="Contact persons by role" description="Top contact roles across active CRM records." points={data.contactPersonsByRole} />
        <BlueBarChart title="Contact coverage by type" description="Share of active records with at least one contact person." points={data.contactCoverageByKind} />
      </div>

      <BlueCard eyebrow="Recent" title="Latest CRM Records">
        <BlueTable columns={recentColumns} rows={data.recentBusinessPartners} rowKey={(row) => row.id} />
      </BlueCard>
    </div>
  );
}
