// Copyright (c) Khaled Shawki. All rights reserved.

import { useState, type FormEvent } from 'react';
import BlueButton from '../components/BlueButton';
import { useLocale } from '../i18n/LocaleProvider';

interface Props {
  disabled?: boolean;
  onSubmit: (message: string) => Promise<void> | void;
}

export default function AssistantMessageInput({ disabled = false, onSubmit }: Props) {
  const { t } = useLocale();
  const [message, setMessage] = useState('');
  const [error, setError] = useState<string | null>(null);

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const normalized = message.trim().replace(/\s+/g, ' ');
    if (!normalized) {
      setError(t('assistant.input.errors.empty'));
      return;
    }
    if (normalized.length > 2000) {
      setError(t('assistant.input.errors.maxLength'));
      return;
    }
    setError(null);
    await onSubmit(normalized);
    setMessage('');
  };

  return (
    <form className="assistant-input-block" onSubmit={submit}>
      <label className="assistant-input-label" htmlFor="assistant-message-input">
        {t('assistant.input.label')}
      </label>
      <textarea
        id="assistant-message-input"
        value={message}
        maxLength={2000}
        rows={3}
        placeholder={t('assistant.input.placeholder')}
        disabled={disabled}
        aria-invalid={error ? true : undefined}
        aria-describedby={error ? 'assistant-message-input-error' : 'assistant-message-input-count'}
        onChange={(event) => setMessage(event.target.value)}
      />
      <div className="assistant-input-footer">
        <div>
          {error && <small id="assistant-message-input-error" className="field-error">{error}</small>}
          <small id="assistant-message-input-count" className="assistant-input-count">{message.length}/2000</small>
        </div>
        <BlueButton type="submit" disabled={disabled}>{t('assistant.input.submit')}</BlueButton>
      </div>
    </form>
  );
}
