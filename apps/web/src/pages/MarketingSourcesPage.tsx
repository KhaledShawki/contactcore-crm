// Copyright (c) Khaled Shawki. All rights reserved.

import { useCallback, useEffect, useMemo, useReducer, useRef } from 'react';
import BlueAlert from '../components/BlueAlert';
import BlueButton from '../components/BlueButton';
import BlueCard from '../components/BlueCard';
import BlueInput from '../components/BlueInput';
import BluePagination from '../components/BluePagination';
import BlueTable, { type BlueColumn } from '../components/BlueTable';
import ErrorState from '../components/ErrorState';
import LoadingState from '../components/LoadingState';
import SchemaForm from '../components/SchemaForm';
import { useArchiveMarketingSourceMutation, useSaveMarketingSourceMutation, useSearchMarketingSourcesQuery } from '../marketing/marketingApi';
import { useDownloadMarketingSourcesReportMutation } from '../reports/reportsApi';
import { saveDownloadedReport } from '../reports/reportDownload';
import { isFormModified } from '../forms/formComparison';
import { useSingleFlightAction, useSingleFlightByKey } from '../hooks/useSingleFlightAction';
import { createDefaultRecord, toWritablePayload, type SchemaRecord } from '../schema/schemaValues';
import { useGetScreenQuery } from '../schema/schemaApi';
import type { MarketingSource } from '../schema/types';
import { useNotifications } from '../notifications/useNotifications';

interface MarketingSourcesState {
  query: string;
  page: number;
  size: number;
  editingId: number | undefined;
  editorOpen: boolean;
  formValue: SchemaRecord;
  savedPayload: SchemaRecord;
  error: string | null;
}

type MarketingSourcesAction =
  | { type: 'searchChanged'; query: string }
  | { type: 'pageChanged'; page: number }
  | { type: 'pageSizeChanged'; size: number }
  | { type: 'editorOpened'; editingId: number | undefined; formValue: SchemaRecord; savedPayload: SchemaRecord }
  | { type: 'editorClosed'; formValue: SchemaRecord; savedPayload: SchemaRecord }
  | { type: 'formChanged'; formValue: SchemaRecord }
  | { type: 'sourceSaved'; editingId: number | undefined; formValue: SchemaRecord; savedPayload: SchemaRecord }
  | { type: 'errorChanged'; error: string | null };

const initialMarketingState: MarketingSourcesState = {
  query: '',
  page: 0,
  size: 20,
  editingId: undefined,
  editorOpen: false,
  formValue: {},
  savedPayload: {},
  error: null,
};

function marketingSourcesReducer(state: MarketingSourcesState, action: MarketingSourcesAction): MarketingSourcesState {
  switch (action.type) {
    case 'searchChanged':
      return { ...state, query: action.query, page: 0 };
    case 'pageChanged':
      return { ...state, page: action.page };
    case 'pageSizeChanged':
      return { ...state, size: action.size, page: 0 };
    case 'editorOpened':
      return {
        ...state,
        editingId: action.editingId,
        editorOpen: true,
        formValue: action.formValue,
        savedPayload: action.savedPayload,
        error: null,
      };
    case 'editorClosed':
      return {
        ...state,
        editingId: undefined,
        editorOpen: false,
        formValue: action.formValue,
        savedPayload: action.savedPayload,
        error: null,
      };
    case 'formChanged':
      return { ...state, formValue: action.formValue };
    case 'sourceSaved':
      return {
        ...state,
        editingId: action.editingId,
        formValue: action.formValue,
        savedPayload: action.savedPayload,
        error: null,
      };
    case 'errorChanged':
      return { ...state, error: action.error };
    default:
      return state;
  }
}

export default function MarketingSourcesPage() {
  const [state, dispatch] = useReducer(marketingSourcesReducer, initialMarketingState);
  const { query, page, size, editingId, editorOpen, formValue, savedPayload, error } = state;
  const editorRef = useRef<HTMLDivElement | null>(null);
  const { notifySuccess, notifyError } = useNotifications();
  const { data: screen, isLoading: screenLoading, error: screenError } = useGetScreenQuery('marketingSources');
  const { data, isFetching, error: rowsError } = useSearchMarketingSourcesQuery({ query, page, size });
  const [saveSource, { isLoading: saving }] = useSaveMarketingSourceMutation();
  const [archiveSource] = useArchiveMarketingSourceMutation();
  const [downloadReport, { isLoading: reportDownloading }] = useDownloadMarketingSourcesReportMutation();
  const { run: runArchiveTask, isRunning: isArchiveRunning } = useSingleFlightByKey<number>();
  const currentPayload = useMemo(() => screen ? toWritablePayload(screen, formValue) : {}, [formValue, screen]);
  const formModified = isFormModified(savedPayload, currentPayload);

  useEffect(() => {
    if (!data) return;
    const lastPage = Math.max(0, data.totalPages - 1);
    if (page > lastPage) {
      dispatch({ type: 'pageChanged', page: lastPage });
    }
  }, [data, page]);

  const scrollEditorIntoView = useCallback(() => {
    window.requestAnimationFrame(() => {
      editorRef.current?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    });
  }, []);

  const saveTask = useSingleFlightAction(async () => {
    if (!screen || !formModified) return;
    try {
      dispatch({ type: 'errorChanged', error: null });
      const saved = await saveSource({ id: editingId, body: currentPayload as Partial<MarketingSource> }).unwrap();
      const nextValue = saved as unknown as SchemaRecord;
      const nextPayload = toWritablePayload(screen, nextValue);
      dispatch({ type: 'sourceSaved', editingId: saved.id, formValue: nextValue, savedPayload: nextPayload });
      notifySuccess('Marketing source saved.');
    } catch {
      const message = 'Could not save the marketing source. Check required fields and duplicate codes.';
      dispatch({ type: 'errorChanged', error: message });
      notifyError(message);
    }
  });

  const closeEditor = useCallback(() => {
    const nextValue = screen ? createDefaultRecord(screen) : {};
    dispatch({
      type: 'editorClosed',
      formValue: nextValue,
      savedPayload: screen ? toWritablePayload(screen, nextValue) : {},
    });
  }, [screen]);

  const startEdit = useCallback((row: MarketingSource) => {
    if (!screen) return;
    const nextValue = row as unknown as SchemaRecord;
    dispatch({
      type: 'editorOpened',
      editingId: row.id,
      formValue: nextValue,
      savedPayload: toWritablePayload(screen, nextValue),
    });
    scrollEditorIntoView();
  }, [screen, scrollEditorIntoView]);

  const startCreate = useCallback(() => {
    if (!screen) return;
    const nextValue = createDefaultRecord(screen);
    dispatch({
      type: 'editorOpened',
      editingId: undefined,
      formValue: nextValue,
      savedPayload: toWritablePayload(screen, nextValue),
    });
    scrollEditorIntoView();
  }, [screen, scrollEditorIntoView]);

  const archiveMarketingSource = useCallback(async (row: MarketingSource) => {
    if (!row.id) return;
    await runArchiveTask(row.id, async () => {
      try {
        await archiveSource(row.id as number).unwrap();
        notifySuccess('Marketing source archived.');
        if (editingId === row.id) {
          closeEditor();
        }
      } catch {
        notifyError('Could not archive the marketing source. Try again.');
      }
    });
  }, [archiveSource, closeEditor, editingId, notifyError, notifySuccess, runArchiveTask]);

  const exportReport = useCallback(async () => {
    try {
      const report = await downloadReport({ query }).unwrap();
      saveDownloadedReport(report);
      notifySuccess('Marketing sources report downloaded.');
    } catch {
      notifyError('Could not generate the marketing sources report. Try again.');
    }
  }, [downloadReport, notifyError, notifySuccess, query]);

  const columns = useMemo<BlueColumn<MarketingSource>[]>(() => {
    if (!screen) return [];

    const schemaColumns: BlueColumn<MarketingSource>[] = [];
    for (const field of screen.fields) {
      if (!field.listVisible) continue;
      schemaColumns.push({
        key: field.key,
        header: field.label,
        render: (row: MarketingSource) => String((row as unknown as Record<string, unknown>)[field.key] ?? ''),
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
            <button type="button" className="link-button" onClick={() => startEdit(row)}>Edit</button>
            <button
              type="button"
              className="link-button danger-link"
              disabled={row.id ? isArchiveRunning(row.id) : false}
              onClick={() => { void archiveMarketingSource(row); }}
            >
              {row.id && isArchiveRunning(row.id) ? 'Archiving...' : 'Archive'}
            </button>
          </div>
        ),
      },
    ];
  }, [archiveMarketingSource, isArchiveRunning, screen, startEdit]);

  if (screenLoading) return <LoadingState />;
  if (screenError || rowsError || !screen) return <ErrorState message="Could not load marketing sources." />;

  const pageData = data ?? { items: [], page: 0, size, totalElements: 0, totalPages: 0 };
  const saveBusy = saving || saveTask.running;

  return (
    <div className={`split-page ${editorOpen ? 'split-page--with-editor' : 'split-page--list-only'}`.trim()}>
      <BlueCard
        eyebrow="Marketing"
        title={screen.title}
        action={(
          <div className="card-actions">
            <BlueButton type="button" variant="secondary" disabled={reportDownloading} onClick={() => { void exportReport(); }}>
              {reportDownloading ? 'Exporting...' : 'Export XLSX'}
            </BlueButton>
            <BlueButton onClick={startCreate}>New</BlueButton>
          </div>
        )}
      >
        <div className="list-toolbar">
          <BlueInput
            label="Search"
            placeholder="Search by code or name"
            value={query}
            onChange={(event) => dispatch({ type: 'searchChanged', query: event.target.value })}
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
          onPageChange={(nextPage) => dispatch({ type: 'pageChanged', page: nextPage })}
          onSizeChange={(nextSize) => dispatch({ type: 'pageSizeChanged', size: nextSize })}
        />
      </BlueCard>

      {editorOpen && (
        <div ref={editorRef}>
          <BlueCard
            eyebrow="Editor"
            title={editingId ? 'Edit Marketing Source' : 'New Marketing Source'}
            action={<BlueButton type="button" variant="secondary" onClick={closeEditor}>Cancel</BlueButton>}
          >
            {error && <BlueAlert message={error} />}
            <SchemaForm
              key={editingId ?? 'new-marketing-source'}
              screen={screen}
              value={formValue}
              onChange={(nextValue) => dispatch({ type: 'formChanged', formValue: nextValue })}
              onSubmit={() => { void saveTask.run(); }}
              submitLabel="Save"
              busy={saveBusy}
              canSubmit={formModified}
              submitDisabledReason="No changes to save."
            />
          </BlueCard>
        </div>
      )}
    </div>
  );
}
