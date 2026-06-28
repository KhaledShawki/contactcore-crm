// Copyright (c) Khaled Shawki. All rights reserved.

import { useState } from 'react';
import BlueButton from '../components/BlueButton';

interface Props {
  disabled?: boolean;
  onSubmit: (message: string) => Promise<void> | void;
}

export default function AssistantMessageInput({ disabled = false, onSubmit }: Props) {
  const [message, setMessage] = useState('');
  const [error, setError] = useState<string | null>(null);

  const submit = async () => {
    const normalized = message.trim().replace(/\s+/g, ' ');
    if (!normalized) {
      setError('Enter a CRM question.');
      return;
    }
    if (normalized.length > 2000) {
      setError('Questions must be at most 2000 characters.');
      return;
    }
    setError(null);
    await onSubmit(normalized);
    setMessage('');
  };

  return (
    <div className="assistant-input-block">
      <label className="blue-field assistant-input-label">
        <span>Ask ContactCore Assistant</span>
        <textarea
          value={message}
          maxLength={2000}
          rows={4}
          placeholder="Example: Which leads need follow-up?"
          disabled={disabled}
          aria-invalid={error ? true : undefined}
          onChange={(event) => setMessage(event.target.value)}
        />
        {error && <small className="field-error">{error}</small>}
      </label>
      <div className="assistant-input-actions">
        <small>{message.length}/2000</small>
        <BlueButton disabled={disabled} onClick={submit}>Ask Assistant</BlueButton>
      </div>
    </div>
  );
}
