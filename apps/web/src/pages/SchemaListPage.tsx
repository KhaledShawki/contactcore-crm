// Copyright (c) Khaled Shawki. All rights reserved.

import { Link } from 'react-router-dom';
import { useCallback, useMemo, useState } from 'react';
import BlueButton from '../components/BlueButton';
import BlueCard from '../components/BlueCard';
import BlueInput from '../components/BlueInput';
import BlueSelect from '../components/BlueSelect';
import BluePagination from '../components/BluePagination';
import BlueStatusBadge from '../components/BlueStatusBadge';
import BlueTable, { type BlueColumn } from '../components/BlueTable';
import ErrorState from '../components/ErrorState';
import LoadingState from '../components/LoadingState';
import { useArchiveBusinessPartnerMutation, useSearchBusinessPartnersQuery } from '../crm/crmApi';
import { useDownloadBusinessPartnerReportMutation } from '../reports/reportsApi';
import { saveDownloadedReport } from '../reports/reportDownload';
import { useGetScreenQuery } from '../schema/schemaApi';
import type { BusinessPartner } from '../schema/types';
import { useNotifications } from '../notifications/useNotifications';
import { useSingleFlightByKey } from '../hooks/useSingleFlightAction';

interface Props {
  screenKey: string;
}

export default function SchemaListPage({ screenKey }: Props) {
  const [query, setQuery] = useState('');
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const [sort, setSort] = useState('updated_desc');
  const { notifySuccess, notifyError } = useNotifications();
  const { data: screen, isLoading: screenLoading, error: screenError } = useGetScreenQuery(screenKey);
  const { data, isFetching, error: rowsError } = useSearchBusinessPartnersQuery(
    { kind: screen?.entityKind ?? '', query, page, size, sort },
    { skip: !screen },
  );
  const [archivePartner] = useArchiveBusinessPartnerMutation();
  const [downloadReport, { isLoading: reportDownloading }] = useDownloadBusinessPartnerReportMutation();
  const { run: runArchiveTask, isRunning: isArchiveRunning } = useSingleFlightByKey<number>();

  function updateQuery(value: string) {
    setQuery(value);
    setPage(0);
  }

  function updateSort(value: string) {
    setSort(value);
    setPage(0);
  }

  function updatePageSize(value: number) {
    setSize(value);
    setPage(0);
  }

  const archiveRecord = useCallback(async (row: BusinessPartner) => {
    if (!row.id || !screen) return;
    const entityLabel = singularTitle(screen.title);
    const shouldMoveToPreviousPage = page > 0 && data?.items.length === 1;
    await runArchiveTask(row.id, async () => {
      try {
        await archivePartner(row.id as number).unwrap();
        if (shouldMoveToPreviousPage) {
          setPage((currentPage) => Math.max(0, currentPage - 1));
        }
        notifySuccess(`${entityLabel} archived.`);
      } catch {
        notifyError(`Could not archive the ${entityLabel.toLowerCase()}. Try again.`);
      }
    });
  }, [archivePartner, data?.items.length, notifyError, notifySuccess, page, runArchiveTask, screen]);

  const exportReport = useCallback(async () => {
    if (!screen) return;
    try {
      const report = await downloadReport({ kind: screen.entityKind, query, sort }).unwrap();
      saveDownloadedReport(report);
      notifySuccess(`${screen.title} report downloaded.`);
    } catch {
      notifyError(`Could not generate the ${screen.title.toLowerCase()} report. Try again.`);
    }
  }, [downloadReport, notifyError, notifySuccess, query, screen, sort]);

  const columns = useMemo<BlueColumn<BusinessPartner>[]>(() => {
    if (!screen) return [];
    const schemaColumns: BlueColumn<BusinessPartner>[] = [];
    for (const field of screen.fields) {
      if (!field.listVisible) continue;
      schemaColumns.push({
        key: field.key,
        header: field.label,
        render: (row: BusinessPartner) => renderFieldValue(field.key, (row as unknown as Record<string, unknown>)[field.key]),
      });
    }
    return [
      ...schemaColumns,
      {
        key: 'actions',
        header: 'Actions',
        align: 'right',
        render: (row) => (
          <div className="row-actions">
            <Link to={`/${screenKey}/${row.id}`}>Open</Link>
            <button type="button" className="link-button danger-link" disabled={row.id ? isArchiveRunning(row.id) : false} onClick={() => { void archiveRecord(row); }}>
              {row.id && isArchiveRunning(row.id) ? 'Archiving...' : 'Archive'}
            </button>
          </div>
        ),
      },
    ];
  }, [archiveRecord, isArchiveRunning, screen, screenKey]);

  if (screenLoading) return <LoadingState />;
  if (screenError || rowsError || !screen) return <ErrorState message="Could not load records." />;

  const pageData = data ?? { items: [], page: 0, size, totalElements: 0, totalPages: 0 };

  return (
    <BlueCard
      eyebrow="CRM"
      title={screen.title}
      action={(
        <div className="card-actions">
          <BlueButton type="button" variant="secondary" disabled={reportDownloading} onClick={() => { void exportReport(); }}>
            {reportDownloading ? 'Exporting...' : 'Export XLSX'}
          </BlueButton>
          <Link className="blue-button blue-button--primary" to={`/${screenKey}/new`}>New</Link>
        </div>
      )}
    >
      <div className="list-toolbar">
        <BlueInput label="Search" placeholder="Search by code, name, email, phone, or contact person" value={query} onChange={(event) => updateQuery(event.target.value)} />
        <BlueSelect
          label="Sort"
          value={sort}
          options={['updated_desc', 'created_desc', 'name_asc', 'code_asc', 'status_asc']}
          onChange={(event) => updateSort(event.target.value)}
        />
        {isFetching && <span className="refresh-pill">Refreshing</span>}
      </div>
      <BlueTable columns={columns} rows={pageData.items} rowKey={(row) => row.id ?? row.code} />
      <BluePagination
        page={pageData.page}
        size={pageData.size}
        totalElements={pageData.totalElements}
        totalPages={pageData.totalPages}
        disabled={isFetching}
        onPageChange={setPage}
        onSizeChange={updatePageSize}
      />
    </BlueCard>
  );
}

function renderFieldValue(key: string, value: unknown) {
  const text = String(value ?? '');
  if (key.toLowerCase().includes('status') && text) {
    return <BlueStatusBadge value={text} />;
  }
  return text;
}

function singularTitle(title: string) {
  if (title.endsWith('ies')) return `${title.slice(0, -3)}y`;
  if (title.endsWith('s')) return title.slice(0, -1);
  return title;
}
