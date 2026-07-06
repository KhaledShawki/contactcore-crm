// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { collectRequiredDataSources, commercialSummaryKpis } from './useSchemaDataSources';
import type { UiScreen } from '../schema/types';

describe('useSchemaDataSources helpers', () => {
  it('collects visible widget data source keys only', () => {
    const screen: UiScreen = {
      key: 'commercialDashboard',
      title: 'Commercial Dashboard',
      entityKind: 'DASHBOARD',
      listEndpoint: '',
      detailEndpoint: '',
      createEndpoint: '',
      updateEndpoint: '',
      archiveEndpoint: '',
      documentEndpoint: '',
      fields: [],
      layout: {
        type: 'dashboard',
        sections: [
          {
            key: 'visible',
            columns: 2,
            widgets: [
              { key: 'summary', type: 'kpiGrid', title: 'Summary', dataSource: { key: 'commercialDashboard.summary', endpoint: '/dashboard/commercial/summary' } },
              { key: 'hiddenWidget', type: 'barChart', title: 'Hidden', visible: false, dataSource: { key: 'commercialDashboard.topCustomers', endpoint: '/dashboard/commercial/top-customers' } },
            ],
          },
          {
            key: 'hiddenSection',
            visible: false,
            columns: 1,
            widgets: [
              { key: 'hiddenSectionWidget', type: 'barChart', title: 'Hidden section', dataSource: { key: 'commercialDashboard.invoiceAging', endpoint: '/dashboard/commercial/invoice-aging' } },
            ],
          },
        ],
      },
    };

    expect([...collectRequiredDataSources(screen)]).toEqual(['commercialDashboard.summary']);
  });

  it('maps commercial summary response into KPI metrics', () => {
    const kpis = commercialSummaryKpis({
      totalSales: 12000,
      openInvoiceAmount: 3000,
      overdueAmount: 1000,
      unpaidInvoiceCount: 4,
      activeCustomerCount: 7,
      soldItemCount: 13,
      currency: 'CHF',
    });

    expect(kpis).toHaveLength(6);
    expect(kpis[0]).toMatchObject({ key: 'commercialTotalSales', value: 12000, unit: 'CHF' });
    expect(kpis[3]).toMatchObject({ key: 'commercialUnpaidInvoiceCount', value: 4, unit: '' });
  });
});
