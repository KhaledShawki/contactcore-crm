// Copyright (c) Khaled Shawki. All rights reserved.

import type { InputHTMLAttributes } from 'react';

interface Props extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string | null;
  helpText?: string;
}

export default function BlueInput({ label, id, className = '', error = null, helpText, required, ...props }: Props) {
  const inputId = id ?? props.name;
  const feedbackId = inputId ? `${inputId}-feedback` : undefined;
  const describedBy = [props['aria-describedby'], feedbackId].filter(Boolean).join(' ') || undefined;

  return (
    <label className={`blue-field ${error ? 'blue-field--invalid' : ''} ${className}`.trim()}>
      {label && (
        <span>
          {label}
          {required && <small className="required-marker" aria-hidden="true"> *</small>}
        </span>
      )}
      <input
        id={inputId}
        aria-invalid={error ? true : undefined}
        aria-describedby={describedBy}
        required={required}
        {...props}
      />
      {(error || helpText) && (
        <small id={feedbackId} className={error ? 'field-error' : 'field-help'}>
          {error ?? helpText}
        </small>
      )}
    </label>
  );
}
