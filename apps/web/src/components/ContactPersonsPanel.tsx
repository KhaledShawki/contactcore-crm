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
    { key: 'displayName', header: 'Name', render: (row) => row.displayName ?? `${row.firstName} ${row.lastName}` },
    { key: 'roleTitle', header: 'Role', render: (row) => row.roleTitle ?? '—' },
    { key: 'email', header: 'Email', render: (row) => row.email ?? '—' },
    { key: 'phone', header: 'Phone', render: (row) => row.phone ?? row.mobile ?? '—' },
    { key: 'primary', header: 'Primary', render: (row) => row.primaryContact ? 'Yes' : 'No' },
    {
      key: 'actions',
      header: 'Actions',
      align: 'right',
      render: (row) => (
        <div className="row-actions">
          <button type="button" className="link-button" onClick={() => startEdit(row)} disabled={saving}>Edit</button>
          {row.id && (
            <button
              type="button"
              className="link-button danger-link"
              disabled={archiveTask.isRunning(row.id)}
              onClick={() => archiveContactPerson(row)}
            >
              {archiveTask.isRunning(row.id) ? 'Archiving...' : 'Archive'}
            </button>
          )}
        </div>
      ),
    },
  ];

  const submitTask = useSingleFlightAction(async () => {
    if (!contactSchema || !formModified) {
      if (!contactSchema) {
        dispatchEditor({ type: 'apiErrorChanged', apiError: 'Could not load contact-person validation schema.' });
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
      notifySuccess(editing ? 'Contact person updated.' : 'Contact person added.');
      resetForm();
    } catch (error) {
      const message = resolveApiErrorMessage(error);
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
        notifySuccess('Contact person archived.');
        if (formValue.id === row.id) {
          resetForm();
        }
      } catch {
        notifyError('Could not archive contact person. Try again.');
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
          <h2>Contact persons</h2>
          <p className="hint">People connected to this customer, lead, or supplier.</p>
        </div>
        {isFetching && <span className="refresh-pill">Refreshing</span>}
      </header>

      <form className="contact-person-editor" onSubmit={(event) => { event.preventDefault(); void submitTask.run(); }} noValidate>
        <div className="contact-person-editor__header">
          <div>
            <strong>{editing ? 'Edit contact person' : 'Add contact person'}</strong>
            <span>Validation is driven by the backend UI schema.</span>
          </div>
          {editing && <span className="settings-pill">Editing</span>}
        </div>

        {formError && <BlueAlert message={formError} />}
        <div className="contact-person-form">
          <BlueInput
            name="firstName"
            label="First name"
            required
            disabled={saveBusy}
            value={formValue.firstName}
            error={fieldErrors.firstName}
            onChange={(event) => setField('firstName', event.target.value)}
          />
          <BlueInput
            name="lastName"
            label="Last name"
            required
            disabled={saveBusy}
            value={formValue.lastName}
            error={fieldErrors.lastName}
            onChange={(event) => setField('lastName', event.target.value)}
          />
          <BlueInput
            name="roleTitle"
            label="Role"
            disabled={saveBusy}
            value={formValue.roleTitle ?? ''}
            error={fieldErrors.roleTitle}
            onChange={(event) => setField('roleTitle', event.target.value)}
          />
          <BlueInput
            name="email"
            label="Email"
            type="email"
            inputMode="email"
            disabled={saveBusy}
            value={formValue.email ?? ''}
            error={fieldErrors.email}
            onChange={(event) => setField('email', event.target.value)}
          />
          <BlueInput
            name="phone"
            label="Phone"
            type="tel"
            inputMode="tel"
            disabled={saveBusy}
            value={formValue.phone ?? ''}
            error={fieldErrors.phone}
            onChange={(event) => setField('phone', event.target.value)}
          />
          <BlueInput
            name="mobile"
            label="Mobile"
            type="tel"
            inputMode="tel"
            disabled={saveBusy}
            value={formValue.mobile ?? ''}
            error={fieldErrors.mobile}
            onChange={(event) => setField('mobile', event.target.value)}
          />
          <BlueInput
            name="department"
            label="Department"
            disabled={saveBusy}
            value={formValue.department ?? ''}
            error={fieldErrors.department}
            onChange={(event) => setField('department', event.target.value)}
          />
          <label className="toggle-card compact-toggle">
            <input type="checkbox" checked={formValue.primaryContact} disabled={saveBusy} onChange={(event) => setField('primaryContact', event.target.checked)} />
            <span>
              <strong>Primary contact</strong>
              <small>Only one active contact can be primary.</small>
            </span>
          </label>
          <label className={`blue-field span-two ${fieldErrors.notes ? 'blue-field--invalid' : ''}`.trim()}>
            <span>Notes</span>
            <textarea value={formValue.notes ?? ''} disabled={saveBusy} onChange={(event) => setField('notes', event.target.value)} aria-invalid={fieldErrors.notes ? true : undefined} />
            {fieldErrors.notes && <small className="field-error">{fieldErrors.notes}</small>}
          </label>
        </div>

        <footer className="contact-person-form__actions">
          <span className="contact-person-form__validation-hint">
            {!formModified ? 'No changes to save.' : currentValidation.valid ? 'Ready to save.' : 'Complete the required contact details before saving.'}
          </span>
          <div className="contact-person-form__buttons">
            <BlueButton type="submit" disabled={saveBusy || !formModified} title={!saveBusy && !formModified ? 'No changes to save.' : undefined}>
              {saveBusy ? 'Saving...' : editing ? 'Update contact' : 'Add contact'}
            </BlueButton>
            <BlueButton type="button" variant="secondary" disabled={saveBusy} onClick={resetForm}>{editing ? 'Cancel edit' : 'Clear'}</BlueButton>
          </div>
        </footer>
      </form>

      <div className="contact-person-table-section">
        {contacts.length === 0 ? (
          <p className="empty-state">No contact persons added yet.</p>
        ) : (
          <BlueTable columns={columns} rows={contacts} rowKey={(row) => row.id ?? `${row.firstName}-${row.lastName}`} />
        )}
      </div>
    </section>
  );
}

function resolveApiErrorMessage(error: unknown): string {
  const queryError = error as FetchBaseQueryError;
  if (queryError && typeof queryError === 'object' && 'data' in queryError) {
    const data = queryError.data as { message?: unknown } | undefined;
    if (typeof data?.message === 'string' && data.message.trim().length > 0) {
      return data.message;
    }
  }
  return 'Could not save contact person. Check the required fields and try again.';
}
