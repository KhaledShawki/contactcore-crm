// Copyright (c) Khaled Shawki. All rights reserved.

import { useMemo, useReducer, useRef } from 'react';
import type { FetchBaseQueryError } from '@reduxjs/toolkit/query';
import BlueAlert from './BlueAlert';
import BlueButton from './BlueButton';
import BlueInput from './BlueInput';
import BlueTable, { type BlueColumn } from './BlueTable';
import type { ContactPerson } from '../schema/types';
import { isFormModified } from '../forms/formComparison';
import { useSingleFlightAction, useSingleFlightByKey } from '../hooks/useSingleFlightAction';
import { toWritablePayload, type SchemaRecord } from '../schema/schemaValues';
import { validateSchemaRecord, type SchemaValidationResult } from '../schema/schemaValidation';
import { useArchiveContactPersonMutation, useListContactPersonsQuery, useSaveContactPersonMutation } from '../crm/crmApi';
import { useGetScreenQuery } from '../schema/schemaApi';
import { useNotifications } from '../notifications/useNotifications';
import { CodeText } from '../i18n/components/CodeText';
import { DirectionalText } from '../i18n/components/DirectionalText';
import { EmailText } from '../i18n/components/EmailText';
import { useLocale } from '../i18n/LocaleProvider';

const emptyContact: ContactPerson = {
  firstName: '',
  lastName: '',
  roleTitle: '',
  email: '',
  phone: '',
  mobile: '',
  department: '',
  primaryContact: false,
  notes: '',
};

const emptyValidation: SchemaValidationResult = { valid: true, fieldErrors: {}, formError: null };

interface ContactEditorState {
  formValue: ContactPerson;
  savedPayload: SchemaRecord | null;
  validation: SchemaValidationResult;
  apiError: string | null;
}

type ContactEditorAction =
  | { type: 'replace'; formValue: ContactPerson; savedPayload: SchemaRecord | null }
  | { type: 'fieldChanged'; formValue: ContactPerson; validation?: SchemaValidationResult }
  | { type: 'validationChanged'; validation: SchemaValidationResult }
  | { type: 'apiErrorChanged'; apiError: string | null }
  | { type: 'resetError' };

const initialEditorState: ContactEditorState = {
  formValue: emptyContact,
  savedPayload: null,
  validation: emptyValidation,
  apiError: null,
};

function contactEditorReducer(state: ContactEditorState, action: ContactEditorAction): ContactEditorState {
  switch (action.type) {
    case 'replace':
      return {
        formValue: action.formValue,
        savedPayload: action.savedPayload,
        validation: emptyValidation,
        apiError: null,
      };
    case 'fieldChanged':
      return {
        ...state,
        formValue: action.formValue,
        validation: action.validation ?? state.validation,
        apiError: null,
      };
    case 'validationChanged':
      return { ...state, validation: action.validation, apiError: null };
    case 'apiErrorChanged':
      return { ...state, apiError: action.apiError };
    case 'resetError':
      return { ...state, apiError: null };
    default:
      return state;
  }
}

export default function ContactPersonsPanel({ businessPartnerId }: { businessPartnerId: number }) {
  const { t } = useLocale();
  const { data: contacts = [], isFetching } = useListContactPersonsQuery(businessPartnerId);
  const { data: contactSchema } = useGetScreenQuery('contactPersons');
  const [saveContact, { isLoading: saving }] = useSaveContactPersonMutation();
  const [archiveContact] = useArchiveContactPersonMutation();
  const [editorState, dispatchEditor] = useReducer(contactEditorReducer, initialEditorState);
  const submittedOnceRef = useRef(false);
  const { formValue, savedPayload, validation, apiError } = editorState;
  const { notifySuccess, notifyError } = useNotifications();
  const archiveTask = useSingleFlightByKey<number>();

  const currentValidation = useMemo(() => {
    if (!contactSchema) return emptyValidation;
    return validateSchemaRecord(contactSchema, formValue as unknown as SchemaRecord);
  }, [contactSchema, formValue]);
  const currentPayload = useMemo(() => {
    if (!contactSchema) return {};
    return toWritablePayload(contactSchema, formValue as unknown as SchemaRecord);
  }, [contactSchema, formValue]);
  const effectiveSavedPayload = savedPayload ?? (contactSchema ? baselineFor(emptyContact) : {});
  const formModified = contactSchema ? isFormModified(effectiveSavedPayload, currentPayload) : false;
  const editing = Boolean(formValue.id);

  const columns: BlueColumn<ContactPerson>[] = [
    { key: 'displayName', header: t('schema.field.name'), render: (row) => <DirectionalText value={row.displayName ?? `${row.firstName} ${row.lastName}`} /> },
    { key: 'roleTitle', header: t('schema.field.roleTitle'), render: (row) => <DirectionalText value={row.roleTitle ?? '—'} /> },
    { key: 'email', header: t('schema.field.email'), render: (row) => row.email ? <EmailText value={row.email} /> : '—' },
    { key: 'phone', header: t('schema.field.phone'), render: (row) => row.phone || row.mobile ? <CodeText value={row.phone ?? row.mobile} /> : '—' },
    { key: 'primary', header: t('contactPersons.primary'), render: (row) => row.primaryContact ? t('common.boolean.yes') : t('common.boolean.no') },
    {
      key: 'actions',
      header: t('common.actions.open'),
      align: 'right',
      render: (row) => (
        <div className="row-actions">
          <button type="button" className="link-button" onClick={() => startEdit(row)} disabled={saving}>{t('common.actions.edit')}</button>
          {row.id && (
            <button
              type="button"
              className="link-button danger-link"
              disabled={archiveTask.isRunning(row.id)}
              onClick={() => archiveContactPerson(row)}
            >
              {archiveTask.isRunning(row.id) ? t('crm.actions.archiving') : t('crm.actions.archive')}
            </button>
          )}
        </div>
      ),
    },
  ];

  const submitTask = useSingleFlightAction(async () => {
    if (!contactSchema || !formModified) {
      if (!contactSchema) {
        dispatchEditor({ type: 'apiErrorChanged', apiError: t('contactPersons.errors.validationSchema') });
      }
      return;
    }

    const nextValidation = validateSchemaRecord(contactSchema, formValue as unknown as SchemaRecord);
    submittedOnceRef.current = true;
    dispatchEditor({ type: 'validationChanged', validation: nextValidation });

    if (!nextValidation.valid) {
      return;
    }

    try {
      const payload = currentPayload as unknown as ContactPerson;
      await saveContact({ businessPartnerId, id: formValue.id, body: payload }).unwrap();
      notifySuccess(editing ? t('contactPersons.notifications.updated') : t('contactPersons.notifications.added'));
      resetForm();
    } catch (error) {
      const message = resolveApiErrorMessage(error, t);
      dispatchEditor({ type: 'apiErrorChanged', apiError: message });
      notifyError(message);
    }
  });

  function baselineFor(contact: ContactPerson) {
    return contactSchema ? toWritablePayload(contactSchema, contact as unknown as SchemaRecord) : {};
  }

  function startEdit(contact: ContactPerson) {
    const nextValue = { ...emptyContact, ...contact };
    submittedOnceRef.current = false;
    dispatchEditor({ type: 'replace', formValue: nextValue, savedPayload: baselineFor(nextValue) });
  }

  function resetForm() {
    submittedOnceRef.current = false;
    dispatchEditor({ type: 'replace', formValue: emptyContact, savedPayload: baselineFor(emptyContact) });
  }

  function setField<TKey extends keyof ContactPerson>(key: TKey, value: ContactPerson[TKey]) {
    const next = { ...formValue, [key]: value };
    dispatchEditor({
      type: 'fieldChanged',
      formValue: next,
      validation: submittedOnceRef.current && contactSchema
        ? validateSchemaRecord(contactSchema, next as unknown as SchemaRecord)
        : undefined,
    });
  }

  async function archiveContactPerson(row: ContactPerson) {
    if (!row.id) return;
    await archiveTask.run(row.id, async () => {
      try {
        await archiveContact({ businessPartnerId, id: row.id as number }).unwrap();
        notifySuccess(t('contactPersons.notifications.archived'));
        if (formValue.id === row.id) {
          resetForm();
        }
      } catch {
        notifyError(t('contactPersons.errors.archive'));
      }
    });
  }

  const fieldErrors = validation.fieldErrors;
  const formError = validation.formError ?? apiError;
  const saveBusy = saving || submitTask.running;

  return (
    <section className="contact-persons-panel">
      <header className="subheader">
        <div>
          <h2>{t('contactPersons.title')}</h2>
          <p className="hint">{t('contactPersons.description')}</p>
        </div>
        {isFetching && <span className="refresh-pill">{t('common.status.refreshing')}</span>}
      </header>

      <form className="contact-person-editor" onSubmit={(event) => { event.preventDefault(); void submitTask.run(); }} noValidate>
        <div className="contact-person-editor__header">
          <div>
            <strong>{editing ? t('contactPersons.editor.editTitle') : t('contactPersons.editor.addTitle')}</strong>
            <span>{t('contactPersons.editor.schemaHint')}</span>
          </div>
          {editing && <span className="settings-pill">{t('contactPersons.editor.editing')}</span>}
        </div>

        {formError && <BlueAlert message={formError} />}
        <div className="contact-person-form">
          <BlueInput
            name="firstName"
            label={t('schema.field.firstName')}
            required
            disabled={saveBusy}
            value={formValue.firstName}
            error={fieldErrors.firstName}
            onChange={(event) => setField('firstName', event.target.value)}
          />
          <BlueInput
            name="lastName"
            label={t('schema.field.lastName')}
            required
            disabled={saveBusy}
            value={formValue.lastName}
            error={fieldErrors.lastName}
            onChange={(event) => setField('lastName', event.target.value)}
          />
          <BlueInput
            name="roleTitle"
            label={t('schema.field.roleTitle')}
            disabled={saveBusy}
            value={formValue.roleTitle ?? ''}
            error={fieldErrors.roleTitle}
            onChange={(event) => setField('roleTitle', event.target.value)}
          />
          <BlueInput
            name="email"
            label={t('schema.field.email')}
            type="email"
            inputMode="email"
            disabled={saveBusy}
            value={formValue.email ?? ''}
            error={fieldErrors.email}
            onChange={(event) => setField('email', event.target.value)}
          />
          <BlueInput
            name="phone"
            label={t('schema.field.phone')}
            type="tel"
            inputMode="tel"
            disabled={saveBusy}
            value={formValue.phone ?? ''}
            error={fieldErrors.phone}
            onChange={(event) => setField('phone', event.target.value)}
          />
          <BlueInput
            name="mobile"
            label={t('schema.field.mobile')}
            type="tel"
            inputMode="tel"
            disabled={saveBusy}
            value={formValue.mobile ?? ''}
            error={fieldErrors.mobile}
            onChange={(event) => setField('mobile', event.target.value)}
          />
          <BlueInput
            name="department"
            label={t('schema.field.department')}
            disabled={saveBusy}
            value={formValue.department ?? ''}
            error={fieldErrors.department}
            onChange={(event) => setField('department', event.target.value)}
          />
          <label className="toggle-card compact-toggle">
            <input type="checkbox" checked={formValue.primaryContact} disabled={saveBusy} onChange={(event) => setField('primaryContact', event.target.checked)} />
            <span>
              <strong>{t('contactPersons.primary')}</strong>
              <small>{t('contactPersons.primaryHelp')}</small>
            </span>
          </label>
          <label className={`blue-field span-two ${fieldErrors.notes ? 'blue-field--invalid' : ''}`.trim()}>
            <span>{t('schema.field.notes')}</span>
            <textarea value={formValue.notes ?? ''} disabled={saveBusy} onChange={(event) => setField('notes', event.target.value)} aria-invalid={fieldErrors.notes ? true : undefined} />
            {fieldErrors.notes && <small className="field-error">{fieldErrors.notes}</small>}
          </label>
        </div>

        <footer className="contact-person-form__actions">
          <span className="contact-person-form__validation-hint">
            {!formModified ? t('schema.form.noChanges') : currentValidation.valid ? t('contactPersons.validation.ready') : t('contactPersons.validation.completeRequired')}
          </span>
          <div className="contact-person-form__buttons">
            <BlueButton type="submit" disabled={saveBusy || !formModified} title={!saveBusy && !formModified ? t('schema.form.noChanges') : undefined}>
              {saveBusy ? t('schema.form.saving') : editing ? t('contactPersons.actions.update') : t('contactPersons.actions.add')}
            </BlueButton>
            <BlueButton type="button" variant="secondary" disabled={saveBusy} onClick={resetForm}>{editing ? t('contactPersons.actions.cancelEdit') : t('contactPersons.actions.clear')}</BlueButton>
          </div>
        </footer>
      </form>

      <div className="contact-person-table-section">
        {contacts.length === 0 ? (
          <p className="empty-state">{t('contactPersons.empty')}</p>
        ) : (
          <BlueTable columns={columns} rows={contacts} rowKey={(row) => row.id ?? `${row.firstName}-${row.lastName}`} />
        )}
      </div>
    </section>
  );
}

function resolveApiErrorMessage(error: unknown, t: (key: string) => string): string {
  const queryError = error as FetchBaseQueryError;
  if (queryError && typeof queryError === 'object' && 'data' in queryError) {
    const data = queryError.data as { message?: unknown } | undefined;
    if (typeof data?.message === 'string' && data.message.trim().length > 0) {
      return data.message;
    }
  }
  return t('contactPersons.errors.save');
}
