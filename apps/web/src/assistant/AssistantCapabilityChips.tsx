// Copyright (c) Khaled Shawki. All rights reserved.

import BlueButton from '../components/BlueButton';
import { useLocale } from '../i18n/LocaleProvider';

interface Props {
  promptKeys: string[];
  disabled?: boolean;
  onSelectPrompt: (prompt: string) => void;
}

export default function AssistantCapabilityChips({ promptKeys, disabled = false, onSelectPrompt }: Props) {
  const { t } = useLocale();

  return (
    <section className="assistant-capabilities" aria-label={t('assistant.suggestions.aria')}>
      <div className="assistant-capabilities__intro">
        <p className="eyebrow">{t('assistant.capabilities.eyebrow')}</p>
        <h2>{t('assistant.capabilities.title')}</h2>
        <p>{t('assistant.capabilities.description')}</p>
      </div>
      <div className="assistant-capability-chips">
        {promptKeys.map((promptKey) => {
          const prompt = t(promptKey);
          return (
            <BlueButton key={promptKey} type="button" variant="secondary" disabled={disabled} onClick={() => onSelectPrompt(prompt)}>
              {prompt}
            </BlueButton>
          );
        })}
      </div>
    </section>
  );
}
