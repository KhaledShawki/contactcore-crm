// Copyright (c) Khaled Shawki. All rights reserved.

import { Link } from 'react-router-dom';
import { useCallback, useMemo, useState } from 'react';
import BlueButton from '../components/BlueButton';
import BlueCard from '../components/BlueCard';
import BlueInput from '../components/BlueInput';
import BlueSelect from '../components/BlueSelect';
import BluePagination from '../components/BluePagination';
import BlueStatusBadge from '../components/BlueStatusBadge';
import { CodeText } from '../i18n/components/CodeText';
import { DirectionalText } from '../i18n/components/DirectionalText';
import { EmailText } from '../i18n/components/EmailText';
import { useLocale } from '../i18n/LocaleProvider';
import BlueTable, { type BlueColumn } from '../components/BlueTable';
import ErrorState from '../components/ErrorState';
import LoadingState from '../components/LoadingState';
import { useArchiveBusinessPartnerMutation, useSearchBusinessPartnersQuery } from '../crm/crmApi';
import { useGetConnectorSessionQuery, useSearchConnectorBusinessPartnersQuery } from '../connectors/connectorApi';
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
  const { t } = useLocale();
  const [query, setQuery] = useState('');
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const [sort, setSort] = useState('updated_desc');
  const { notifySuccess, notifyError } = useNotifications();
  const { data: screen, isLoading: screenLoading, error: screenError } = useGetScreenQuery(screenKey);
  const { data: connectorSession } = useGetConnectorSessionQuery();
  const connectorMode = connectorSession?.connected === true;
  const localRows = useSearchBusinessPartnersQuery(
    { kind: screen?.entityKind ?? '', query, page, size, sort },
    { skip: !screen || connectorMode },
  );
  const connectorRows = useSearchConnectorBusinessPartnersQuery(
    { type: screen?.entityKind ?? '', query, page, size, sort },
    { skip: !screen || !connectorMode },
  );
  const data = connectorMode ? connectorRows.data : localRows.data;
  const isFetching = connectorMode ? connectorRows.isFetching : localRows.isFetching;
  const rowsError = connectorMode ? connectorRows.error : localRows.error;
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
    if (connectorMode || !row.id || !screen) return;
    const entityLabel = singularTitle(t(`navigation.${screen.key}`));
    const shouldMoveToPreviousPage = page > 0 && data?.items.length === 1;
    await runArchiveTask(row.id, async () => {
      try {
        await archivePartner(row.id as number).unwrap();
        if (shouldMoveToPreviousPage) {
          setPage((currentPage) => Math.max(0, currentPage - 1));
        }
        notifySuccess(t('crm.notifications.archived', { entity: entityLabel }));
      } catch {
        notifyError(t('crm.errors.archive', { entity: entityLabel.toLowerCase() }));
      }
    });
  }, [archivePartner, connectorMode, data?.items.length, notifyError, notifySuccess, page, runArchiveTask, screen, t]);

  const exportReport = useCallback(async () => {
    if (!screen || connectorMode) return;
    try {
      const report = await downloadReport({ kind: screen.entityKind, query, sort }).unwrap();
      saveDownloadedReport(report);
      notifySuccess(t('crm.notifications.exported'));
    } catch {
      notifyError(t('crm.errors.export', { title: t(`navigation.${screen.key}`).toLowerCase() }));
    }
  }, [connectorMode, downloadReport, notifyError, notifySuccess, query, screen, sort, t]);

  const columns = useMemo<BlueColumn<BusinessPartner>[]>(() => {
    if (!screen) return [];
    const schemaColumns: BlueColumn<BusinessPartner>[] = [];
    for (const field of screen.fields) {
      if (!field.listVisible) continue;
      schemaColumns.push({
        key: field.key,
        header: t(field.labelKey ?? `schema.field.${field.key}`),
        render: (row: BusinessPartner) => renderFieldValue(field.key, field.valueKind, (row as unknown as Record<string, unknown>)[field.key]),
      });
    }
    return [
      ...schemaColumns,
      {
        key: 'actions',
        header: t('common.actions.open'),
        align: 'right',
        render: (row) => (
          <div className="row-actions">
            <Link to={`/${screenKey}/${encodeURIComponent(String(row.externalId ?? row.id ?? row.code))}`}>{t('common.actions.open')}</Link>
            {!connectorMode && (
              <button type="button" className="link-button danger-link" disabled={row.id ? isArchiveRunning(row.id) : false} onClick={() => { void archiveRecord(row); }}>
                {row.id && isArchiveRunning(row.id) ? t('crm.actions.archiving') : t('crm.actions.archive')}
              </button>
            )}
          </div>
        ),
      },
    ];
  }, [archiveRecord, connectorMode, isArchiveRunning, screen, screenKey, t]);

  if (screenLoading) return <LoadingState />;
  if (screenError || rowsError || !screen) return <ErrorState message={t('crm.errors.loadRecords')} />;

  const pageData = data ?? { items: [], page: 0, size, totalElements: 0, totalPages: 0 };

  return (
    <BlueCard
      eyebrow={connectorMode ? t('connectors.mode.eyebrow') : t('crm.eyebrow')}
      title={connectorMode && connectorSession?.connectorDisplayName ? t('crm.form.fromConnector', { entity: t(`navigation.${screen.key}`), connector: connectorSession.connectorDisplayName }) : t(`navigation.${screen.key}`)}
      action={(
        <div className="card-actions">
          {!connectorMode && (
            <>
              <BlueButton type="button" variant="secondary" disabled={reportDownloading} onClick={() => { void exportReport(); }}>
                {reportDownloading ? t('crm.actions.exporting') : t('crm.actions.export')}
              </BlueButton>
              <Link className="blue-button blue-button--primary" to={`/${screenKey}/new`}>{t('crm.actions.new')}</Link>
            </>
          )}
        </div>
      )}
    >
      <div className="list-toolbar">
        <BlueInput label={t('crm.search.label')} placeholder={t('crm.search.placeholder')} value={query} onChange={(event) => updateQuery(event.target.value)} />
        <BlueSelect
          label={t('crm.sort.label')}
          value={sort}
          options={['updated_desc', 'created_desc', 'name_asc', 'code_asc', 'status_asc']}
          optionLabels={{
            updated_desc: t('crm.sort.updated_desc'),
            created_desc: t('crm.sort.created_desc'),
            name_asc: t('crm.sort.name_asc'),
            code_asc: t('crm.sort.code_asc'),
            status_asc: t('crm.sort.status_asc'),
          }}
          onChange={(event) => updateSort(event.target.value)}
        />
        {isFetching && <span className="refresh-pill">{t('common.status.refreshing')}</span>}
      </div>
      {connectorMode && <p className="hint">{t('connectors.mode.listHint')}</p>}
      <BlueTable columns={columns} rows={pageData.items} rowKey={(row) => row.externalId ?? row.id ?? row.code} />
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

function renderFieldValue(key: string, valueKind: string | null | undefined, value: unknown) {
  const text = String(value ?? '');
  if (key.toLowerCase().includes('status') && text) {
    return <BlueStatusBadge value={text} />;
  }
  if (valueKind === 'code' || valueKind === 'phone' || valueKind === 'url') {
    return <CodeText value={text} />;
  }
  if (valueKind === 'email') {
    return <EmailText value={text} />;
  }
  return <DirectionalText value={text} />;
}

function singularTitle(title: string) {
  if (title.endsWith('ies')) return `${title.slice(0, -3)}y`;
  if (title.endsWith('s')) return title.slice(0, -1);
  return title;
}
