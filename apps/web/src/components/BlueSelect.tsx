// Copyright (c) Khaled Shawki. All rights reserved.

import type { SelectHTMLAttributes } from 'react';

interface Props extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  options: string[];
  error?: string | null;
  helpText?: string | null;
}

export default function BlueSelect({ label, id, options, className = '', error = null, helpText = null, required, ...props }: Props) {
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
          <option key={option} value={option}>{option || 'None'}</option>
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
