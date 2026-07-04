// Copyright (c) Khaled Shawki. All rights reserved.

import type { SelectHTMLAttributes } from 'react';
import { useLocale } from '../i18n/LocaleProvider';

const EMPTY_OPTION_LABELS: Record<string, string> = {};

interface Props extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  options: string[];
  optionLabels?: Record<string, string>;
  error?: string | null;
  helpText?: string | null;
}

export default function BlueSelect({ label, id, options, optionLabels = EMPTY_OPTION_LABELS, className = '', error = null, helpText = null, required, ...props }: Props) {
  const { t } = useLocale();
  const selectId = id ?? props.name;
  const feedbackId = selectId ? `${selectId}-feedback` : undefined;
  const describedBy = [props['aria-describedby'], feedbackId].filter(Boolean).join(' ') || undefined;

  return (
    <label className={`blue-field ${error ? 'blue-field--invalid' : ''} ${className}`.trim()}>
      {label && (
        <span>
          {label}
          {required && <small className="required-marker" aria-hidden="true"> *</small>}
        </span>
      )}
      <select id={selectId} aria-invalid={error ? true : undefined} aria-describedby={describedBy} required={required} {...props}>
        {options.map((option) => (
          <option key={option} value={option}>{optionLabels[option] ?? (option || t('common.empty.none'))}</option>
        ))}
      </select>
      {(error || helpText) && (
        <small id={feedbackId} className={error ? 'field-error' : 'field-help'}>
          {error ?? helpText}
        </small>
      )}
    </label>
  );
}
