// Copyright (c) Khaled Shawki. All rights reserved.

import { useRef, useState } from 'react';
import BlueAlert from './BlueAlert';
import BlueButton from './BlueButton';
import BlueInput from './BlueInput';
import BlueSelect from './BlueSelect';
import type { SchemaRecord } from '../schema/schemaValues';
import { validateSchemaRecord, type SchemaValidationResult } from '../schema/schemaValidation';
import type { UiScreen } from '../schema/types';

interface Props {
  screen: UiScreen;
  value: SchemaRecord;
  busy?: boolean;
  canSubmit?: boolean;
  submitDisabledReason?: string;
  submitLabel: string;
  onChange: (next: SchemaRecord) => void;
  onSubmit: () => void;
  readOnly?: boolean;
}

const emptyValidation: SchemaValidationResult = { valid: true, fieldErrors: {}, formError: null };

export default function SchemaForm({ screen, value, busy = false, canSubmit = true, submitDisabledReason, submitLabel, onChange, onSubmit, readOnly = false }: Props) {
  const [validation, setValidation] = useState<SchemaValidationResult>(emptyValidation);
  const submittedOnceRef = useRef(false);
  const visibleFields = screen.fields.filter((field) => field.formVisible && field.type !== 'hidden');

  function setField(key: string, nextValue: string | boolean) {
    const nextRecord = { ...value, [key]: nextValue };
    onChange(nextRecord);
    if (submittedOnceRef.current) {
      setValidation(validateSchemaRecord(screen, nextRecord));
    }
  }

  function submit() {
    if (busy || !canSubmit) {
      return;
    }

    const result = validateSchemaRecord(screen, value);
    submittedOnceRef.current = true;
    setValidation(result);
    if (!result.valid) {
      return;
    }
    onSubmit();
  }

  return (
    <form className="schema-form" noValidate onSubmit={(event) => { event.preventDefault(); submit(); }}>
      {validation.formError && <div className="span-two"><BlueAlert message={validation.formError} /></div>}
      {visibleFields.map((field) => {
        const fieldValue = String(value[field.key] ?? field.defaultValue ?? '');
        const disabled = readOnly || field.readOnly || busy;
        const error = validation.fieldErrors[field.key] ?? null;
        const helpText = field.validation?.helpText ?? undefined;
        const inputType = resolveInputType(field.type, field.validation?.inputType);

        if (field.type === 'select') {
          return (
            <BlueSelect
              key={field.key}
              name={field.key}
              label={field.label}
              required={field.required}
              disabled={disabled}
              value={fieldValue}
              options={field.options}
              error={error}
              helpText={helpText}
              onChange={(event) => setField(field.key, event.target.value)}
            />
          );
        }

        if (field.type === 'checkbox') {
          return (
            <label key={field.key} className="toggle-card compact-toggle">
              <input
                type="checkbox"
                checked={Boolean(value[field.key])}
                disabled={disabled}
                onChange={(event) => setField(field.key, event.target.checked)}
              />
              <span>
                <strong>{field.label}</strong>
                {helpText && <small>{helpText}</small>}
              </span>
            </label>
          );
        }

        if (field.type === 'textarea') {
          return (
            <label key={field.key} className={`blue-field span-two ${error ? 'blue-field--invalid' : ''}`.trim()}>
              <span>
                {field.label}
                {field.required && <small className="required-marker" aria-hidden="true"> *</small>}
              </span>
              <textarea
                name={field.key}
                required={field.required}
                disabled={disabled}
                aria-invalid={error ? true : undefined}
                value={fieldValue}
                onChange={(event) => setField(field.key, event.target.value)}
              />
              {(error || helpText) && <small className={error ? 'field-error' : 'field-help'}>{error ?? helpText}</small>}
            </label>
          );
        }

        return (
          <BlueInput
            key={field.key}
            name={field.key}
            label={field.label}
            type={inputType}
            inputMode={inputType === 'tel' ? 'tel' : inputType === 'email' ? 'email' : inputType === 'number' ? 'numeric' : undefined}
            required={field.required}
            disabled={disabled}
            value={fieldValue}
            error={error}
            helpText={helpText}
            onChange={(event) => setField(field.key, event.target.value)}
          />
        );
      })}
      {!readOnly && (
        <div className="schema-form__actions span-two">
          <BlueButton type="submit" disabled={busy || !canSubmit} title={!busy && !canSubmit ? submitDisabledReason : undefined}>
            {busy ? 'Saving...' : submitLabel}
          </BlueButton>
        </div>
      )}
    </form>
  );
}

function resolveInputType(fieldType: string, inputType: string | null | undefined) {
  if (fieldType === 'number' || inputType === 'number') return 'number';
  if (inputType === 'email') return 'email';
  if (inputType === 'tel') return 'tel';
  if (inputType === 'url') return 'url';
  return 'text';
}
