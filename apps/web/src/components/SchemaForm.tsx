// Copyright (c) Khaled Shawki. All rights reserved.

import { useRef, useState } from 'react';
import BlueAlert from './BlueAlert';
import BlueButton from './BlueButton';
import BlueInput from './BlueInput';
import BlueSelect from './BlueSelect';
import { CodeText } from '../i18n/components/CodeText';
import { DirectionalText } from '../i18n/components/DirectionalText';
import { EmailText } from '../i18n/components/EmailText';
import { useLocale } from '../i18n/LocaleProvider';
import type { SchemaRecord } from '../schema/schemaValues';
import { validateSchemaRecord, type SchemaValidationResult } from '../schema/schemaValidation';
import type { UiField, UiScreen } from '../schema/types';

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
  const { t } = useLocale();
  const [validation, setValidation] = useState<SchemaValidationResult>(emptyValidation);
  const submittedOnceRef = useRef(false);
  const visibleFields = screen.fields.filter((field) => field.formVisible && field.type !== 'hidden');

  function setField(key: string, nextValue: string | boolean) {
    const nextRecord = { ...value, [key]: nextValue };
    onChange(nextRecord);
    if (submittedOnceRef.current) {
      setValidation(validateSchemaRecord(screen, nextRecord, t));
    }
  }

  function submit() {
    if (busy || !canSubmit) {
      return;
    }

    const result = validateSchemaRecord(screen, value, t);
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
        const label = t(field.labelKey ?? `schema.field.${field.key}`);

        if (readOnly || field.readOnly) {
          return <ReadOnlyField key={field.key} field={field} label={label} value={fieldValue} helpText={helpText} />;
        }

        if (field.type === 'select') {
          return (
            <BlueSelect
              key={field.key}
              name={field.key}
              label={label}
              required={field.required}
              disabled={disabled}
              value={fieldValue}
              options={field.options}
              optionLabels={Object.fromEntries(field.options.map((option) => [option, t(`schema.option.${option}`)]))}
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
                <strong>{label}</strong>
                {helpText && <small>{helpText}</small>}
              </span>
            </label>
          );
        }

        if (field.type === 'textarea') {
          return (
            <label key={field.key} className={`blue-field span-two ${error ? 'blue-field--invalid' : ''}`.trim()}>
              <span>
                {label}
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
            label={label}
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
            {busy ? t('schema.form.saving') : submitLabel}
          </BlueButton>
        </div>
      )}
    </form>
  );
}

function ReadOnlyField({ field, label, value, helpText }: { field: UiField; label: string; value: string; helpText?: string }) {
  return (
    <div className={`blue-field readonly-field ${field.type === 'textarea' ? 'span-two' : ''}`.trim()}>
      <span>{label}</span>
      <div className="readonly-value"><ReadOnlyValue valueKind={field.valueKind} value={value} /></div>
      {helpText && <small className="field-help">{helpText}</small>}
    </div>
  );
}

function ReadOnlyValue({ valueKind, value }: { valueKind: string | null | undefined; value: string }) {
  if (!value) return '—';
  if (valueKind === 'code' || valueKind === 'phone' || valueKind === 'url') return <CodeText value={value} />;
  if (valueKind === 'email') return <EmailText value={value} />;
  return <DirectionalText value={value} />;
}

function resolveInputType(fieldType: string, inputType: string | null | undefined) {
  if (fieldType === 'number' || inputType === 'number') return 'number';
  if (inputType === 'email') return 'email';
  if (inputType === 'tel') return 'tel';
  if (inputType === 'url') return 'url';
  return 'text';
}
