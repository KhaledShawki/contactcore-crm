// Copyright (c) Khaled Shawki. All rights reserved.

import { Link, useNavigate, useParams } from 'react-router-dom';
import BlueAlert from '../components/BlueAlert';
import BlueButton from '../components/BlueButton';
import BlueCard from '../components/BlueCard';
import ErrorState from '../components/ErrorState';
import LoadingState from '../components/LoadingState';
import SchemaForm from '../components/SchemaForm';
import ContactPersonsPanel from '../components/ContactPersonsPanel';
import {
  useArchiveBusinessPartnerDocumentMutation,
  useGetBusinessPartnerQuery,
  useListBusinessPartnerDocumentsQuery,
  useSaveBusinessPartnerMutation,
  useUploadBusinessPartnerDocumentMutation,
} from '../crm/crmApi';
import { useGetConnectorBusinessPartnerQuery, useGetConnectorSessionQuery } from '../connectors/connectorApi';
import { isFormModified } from '../forms/formComparison';
import { useSingleFlightAction, useSingleFlightByKey } from '../hooks/useSingleFlightAction';
import { createDefaultRecord, toWritablePayload, type SchemaRecord } from '../schema/schemaValues';
import { useGetScreenQuery } from '../schema/schemaApi';
import { hasCapability } from '../schema/capabilities';
import type { BusinessPartner, UiScreen } from '../schema/types';
import { useMemo, useState } from 'react';
import { useNotifications } from '../notifications/useNotifications';
import { useLocale } from '../i18n/LocaleProvider';

interface Props {
  screenKey: string;
}

interface SchemaFormEditorProps {
  initialValue: SchemaRecord;
  numericId?: number;
  screen: UiScreen;
  screenKey: string;
  connectorMode?: boolean;
  connectorName?: string | null;
}

function SchemaFormEditor({ initialValue, numericId, screen, screenKey, connectorMode = false, connectorName = null }: SchemaFormEditorProps) {
  const { t } = useLocale();
  const navigate = useNavigate();
  const { notifySuccess, notifyError } = useNotifications();
  const [formValue, setFormValue] = useState<SchemaRecord>(initialValue);
  const [savedPayload, setSavedPayload] = useState<SchemaRecord>(() => toWritablePayload(screen, initialValue));
  const [error, setError] = useState<string | null>(null);
  const { data: documents = [] } = useListBusinessPartnerDocumentsQuery(numericId as number, { skip: !numericId || connectorMode });
  const [savePartner, { isLoading: saving }] = useSaveBusinessPartnerMutation();
  const [uploadDocument, { isLoading: uploadingDocument }] = useUploadBusinessPartnerDocumentMutation();
  const [archiveDocument] = useArchiveBusinessPartnerDocumentMutation();
  const archiveDocumentTask = useSingleFlightByKey<number>();
  const entityLabel = singularTitle(t(`navigation.${screen.key}`));
  const currentPayload = useMemo(() => toWritablePayload(screen, formValue), [formValue, screen]);
  const formModified = isFormModified(savedPayload, currentPayload);
  const canCreate = hasCapability(screen.capabilities, 'create');
  const canUpdate = hasCapability(screen.capabilities, 'update');
  const canSave = numericId ? canUpdate : canCreate;

  const saveTask = useSingleFlightAction(async () => {
    if (connectorMode || !canSave || !formModified) {
      return;
    }

    try {
      setError(null);
      const saved = await savePartner({ id: numericId, body: currentPayload }).unwrap();
      const nextValue = saved as unknown as SchemaRecord;
      const nextPayload = toWritablePayload(screen, nextValue);
      setFormValue(nextValue);
      setSavedPayload(nextPayload);
      notifySuccess(t('crm.notifications.saved', { entity: entityLabel }));
      navigate(`/${screenKey}/${saved.id}`, { replace: true });
    } catch {
      const message = t('profile.saveFailed');
      setError(message);
      notifyError(message);
    }
  });

  async function upload(file: File | null) {
    if (!file || !numericId || uploadingDocument) return;
    try {
      await uploadDocument({ businessPartnerId: numericId, file }).unwrap();
      notifySuccess(t('documents.notifications.uploaded'));
    } catch {
      notifyError(t('documents.errors.upload'));
    }
  }

  async function archiveBusinessPartnerDocument(documentId: number) {
    if (!numericId) return;
    await archiveDocumentTask.run(documentId, async () => {
      try {
        await archiveDocument({ businessPartnerId: numericId, documentId }).unwrap();
        notifySuccess(t('documents.notifications.archived'));
      } catch {
        notifyError(t('documents.errors.archive'));
      }
    });
  }

  const saveBusy = saving || saveTask.running;

  return (
    <BlueCard
      eyebrow={connectorMode ? t('connectors.mode.eyebrow') : t('crm.eyebrow')}
      title={connectorMode ? t('crm.form.fromConnector', { entity: entityLabel, connector: connectorName ?? 'connector' }) : numericId ? t('crm.form.edit', { entity: entityLabel }) : t('crm.form.new', { entity: entityLabel })}
      action={<Link to={`/${screenKey}`}>{t('crm.actions.back')}</Link>}
    >
      {error && <BlueAlert message={error} />}
      {connectorMode && <BlueAlert message={t('connectors.mode.recordHint')} />}
      <SchemaForm
        screen={screen}
        value={formValue}
        onChange={setFormValue}
        onSubmit={() => { void saveTask.run(); }}
        submitLabel={t('common.actions.save')}
        busy={saveBusy}
        canSubmit={canSave && formModified}
        submitDisabledReason={t('schema.form.noChanges')}
        readOnly={connectorMode || !canSave}
      />

      {numericId && !connectorMode && canUpdate && <ContactPersonsPanel businessPartnerId={numericId} />}

      {numericId && !connectorMode && canUpdate && (
        <section className="documents-panel">
          <header className="subheader">
            <h2>{t('documents.title')}</h2>
            <input type="file" aria-label={t('documents.actions.upload')} disabled={uploadingDocument} onChange={(event) => upload(event.target.files?.[0] ?? null)} />
          </header>
          {documents.length === 0 ? <p className="hint">{t('documents.empty')}</p> : (
            <ul>
              {documents.map((document) => (
                <li key={document.id}>
                  <a href={document.downloadUrl} target="_blank" rel="noreferrer">{document.originalFilename}</a>
                  <div className="row-actions">
                    <span>{Math.ceil(document.sizeBytes / 1024)} KB</span>
                    <BlueButton
                      type="button"
                      variant="danger"
                      disabled={archiveDocumentTask.isRunning(document.id)}
                      onClick={() => archiveBusinessPartnerDocument(document.id)}
                    >
                      {archiveDocumentTask.isRunning(document.id) ? t('crm.actions.archiving') : t('crm.actions.archive')}
                    </BlueButton>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </section>
      )}
    </BlueCard>
  );
}

function initialFormValue(screen: UiScreen, record: BusinessPartner | undefined): SchemaRecord {
  if (record) {
    return record as unknown as SchemaRecord;
  }

  return createDefaultRecord(screen);
}

export default function SchemaFormPage({ screenKey }: Props) {
  const { id } = useParams();
  const isNew = id === 'new';
  const recordKey = id && !isNew ? decodeURIComponent(id) : undefined;
  const numericId = recordKey && /^\d+$/.test(recordKey) ? Number(recordKey) : undefined;
  const { data: connectorSession } = useGetConnectorSessionQuery();
  const connectorMode = connectorSession?.connected === true;
  const { data: screen, isLoading: screenLoading, error: screenError } = useGetScreenQuery(screenKey);
  const localRecord = useGetBusinessPartnerQuery(numericId as number, { skip: connectorMode || !numericId });
  const connectorRecord = useGetConnectorBusinessPartnerQuery(recordKey ?? '', { skip: !connectorMode || !recordKey || isNew });
  const record = connectorMode ? connectorRecord.data : localRecord.data;
  const recordLoading = connectorMode ? connectorRecord.isLoading : localRecord.isLoading;
  const recordError = connectorMode ? connectorRecord.error : localRecord.error;

  const { t } = useLocale();

  if (screenLoading || recordLoading) return <LoadingState />;
  if (connectorMode && isNew) return <ErrorState message={t('connectors.mode.recordHint')} />;
  if (screenError || recordError || !screen) return <ErrorState message={t('crm.errors.loadRecord')} />;
  if (isNew && !hasCapability(screen.capabilities, 'create')) return <ErrorState message={t('common.errors.forbidden')} />;

  const editorKey = `${screenKey}:${connectorMode ? recordKey : numericId ?? 'new'}:${record?.externalId ?? record?.id ?? 'default'}`;

  return (
    <SchemaFormEditor
      key={editorKey}
      initialValue={initialFormValue(screen, record)}
      numericId={connectorMode ? undefined : numericId}
      screen={screen}
      screenKey={screenKey}
      connectorMode={connectorMode}
      connectorName={connectorSession?.connectorDisplayName}
    />
  );
}

function singularTitle(title: string) {
  if (title.endsWith('ies')) return `${title.slice(0, -3)}y`;
  if (title.endsWith('s')) return title.slice(0, -1);
  return title;
}
