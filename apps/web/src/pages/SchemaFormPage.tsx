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
import { isFormModified } from '../forms/formComparison';
import { useSingleFlightAction, useSingleFlightByKey } from '../hooks/useSingleFlightAction';
import { createDefaultRecord, toWritablePayload, type SchemaRecord } from '../schema/schemaValues';
import { useGetScreenQuery } from '../schema/schemaApi';
import type { BusinessPartner, UiScreen } from '../schema/types';
import { useMemo, useState } from 'react';
import { useNotifications } from '../notifications/useNotifications';

interface Props {
  screenKey: string;
}

interface SchemaFormEditorProps {
  initialValue: SchemaRecord;
  numericId?: number;
  screen: UiScreen;
  screenKey: string;
}

function SchemaFormEditor({ initialValue, numericId, screen, screenKey }: SchemaFormEditorProps) {
  const navigate = useNavigate();
  const { notifySuccess, notifyError } = useNotifications();
  const [formValue, setFormValue] = useState<SchemaRecord>(initialValue);
  const [savedPayload, setSavedPayload] = useState<SchemaRecord>(() => toWritablePayload(screen, initialValue));
  const [error, setError] = useState<string | null>(null);
  const { data: documents = [] } = useListBusinessPartnerDocumentsQuery(numericId as number, { skip: !numericId });
  const [savePartner, { isLoading: saving }] = useSaveBusinessPartnerMutation();
  const [uploadDocument, { isLoading: uploadingDocument }] = useUploadBusinessPartnerDocumentMutation();
  const [archiveDocument] = useArchiveBusinessPartnerDocumentMutation();
  const archiveDocumentTask = useSingleFlightByKey<number>();
  const entityLabel = singularTitle(screen.title);
  const currentPayload = useMemo(() => toWritablePayload(screen, formValue), [formValue, screen]);
  const formModified = isFormModified(savedPayload, currentPayload);

  const saveTask = useSingleFlightAction(async () => {
    if (!formModified) {
      return;
    }

    try {
      setError(null);
      const saved = await savePartner({ id: numericId, body: currentPayload }).unwrap();
      const nextValue = saved as unknown as SchemaRecord;
      const nextPayload = toWritablePayload(screen, nextValue);
      setFormValue(nextValue);
      setSavedPayload(nextPayload);
      notifySuccess(`${entityLabel} saved.`);
      navigate(`/${screenKey}/${saved.id}`, { replace: true });
    } catch {
      const message = `Could not save the ${entityLabel.toLowerCase()}. Check required fields and duplicate codes.`;
      setError(message);
      notifyError(message);
    }
  });

  async function upload(file: File | null) {
    if (!file || !numericId || uploadingDocument) return;
    try {
      await uploadDocument({ businessPartnerId: numericId, file }).unwrap();
      notifySuccess('Document uploaded.');
    } catch {
      notifyError('Could not upload the document. Check the file and try again.');
    }
  }

  async function archiveBusinessPartnerDocument(documentId: number) {
    if (!numericId) return;
    await archiveDocumentTask.run(documentId, async () => {
      try {
        await archiveDocument({ businessPartnerId: numericId, documentId }).unwrap();
        notifySuccess('Document archived.');
      } catch {
        notifyError('Could not archive the document. Try again.');
      }
    });
  }

  const saveBusy = saving || saveTask.running;

  return (
    <BlueCard eyebrow="CRM" title={numericId ? `Edit ${entityLabel}` : `New ${entityLabel}`} action={<Link to={`/${screenKey}`}>Back</Link>}>
      {error && <BlueAlert message={error} />}
      <SchemaForm
        screen={screen}
        value={formValue}
        onChange={setFormValue}
        onSubmit={() => { void saveTask.run(); }}
        submitLabel="Save"
        busy={saveBusy}
        canSubmit={formModified}
        submitDisabledReason="No changes to save."
      />

      {numericId && <ContactPersonsPanel businessPartnerId={numericId} />}

      {numericId && (
        <section className="documents-panel">
          <header className="subheader">
            <h2>Documents</h2>
            <input type="file" aria-label="Upload document" disabled={uploadingDocument} onChange={(event) => upload(event.target.files?.[0] ?? null)} />
          </header>
          {documents.length === 0 ? <p className="hint">No documents uploaded.</p> : (
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
                      {archiveDocumentTask.isRunning(document.id) ? 'Archiving...' : 'Archive'}
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

function initialFormValue(screen: UiScreen, record: BusinessPartner | undefined, numericId: number | undefined): SchemaRecord {
  if (numericId && record) {
    return record as unknown as SchemaRecord;
  }

  return createDefaultRecord(screen);
}

export default function SchemaFormPage({ screenKey }: Props) {
  const { id } = useParams();
  const numericId = id && id !== 'new' ? Number(id) : undefined;
  const { data: screen, isLoading: screenLoading, error: screenError } = useGetScreenQuery(screenKey);
  const { data: record, isLoading: recordLoading, error: recordError } = useGetBusinessPartnerQuery(numericId as number, { skip: !numericId });

  if (screenLoading || recordLoading) return <LoadingState />;
  if (screenError || recordError || !screen) return <ErrorState message="Could not load form." />;

  const editorKey = `${screenKey}:${numericId ?? 'new'}:${record?.id ?? 'default'}`;

  return (
    <SchemaFormEditor
      key={editorKey}
      initialValue={initialFormValue(screen, record, numericId)}
      numericId={numericId}
      screen={screen}
      screenKey={screenKey}
    />
  );
}

function singularTitle(title: string) {
  if (title.endsWith('ies')) return `${title.slice(0, -3)}y`;
  if (title.endsWith('s')) return title.slice(0, -1);
  return title;
}
