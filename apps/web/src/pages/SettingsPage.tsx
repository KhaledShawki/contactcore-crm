// Copyright (c) Khaled Shawki. All rights reserved.

import { useEffect, useMemo, useState } from 'react';
import BlueButton from '../components/BlueButton';
import BlueCard from '../components/BlueCard';
import BlueSelect from '../components/BlueSelect';
import ErrorState from '../components/ErrorState';
import LoadingState from '../components/LoadingState';
import { useGetUiSettingsQuery, useUpdateUiSettingsMutation } from '../settings/settingsApi';
import { applyUiSettings } from '../theme/themeSlice';
import { blueThemeOptions, densityOptions, sidebarOptions, textSizeOptions } from '../theme/themeTypes';
import { useAppDispatch } from '../store/hooks';
import type { UiSettings } from '../schema/types';
import { isFormModified, type ComparableFormValue } from '../forms/formComparison';
import { useSingleFlightAction } from '../hooks/useSingleFlightAction';
import { useNotifications } from '../notifications/useNotifications';
import { useLocale } from '../i18n/LocaleProvider';

const landingPages = ['/dashboard', '/customers', '/leads', '/suppliers', '/reports'] as const;

export default function SettingsPage() {
  const { t } = useLocale();
  const dispatch = useAppDispatch();
  const { data, isLoading, error } = useGetUiSettingsQuery();

  useEffect(() => {
    if (data) {
      dispatch(applyUiSettings(data));
    }
  }, [data, dispatch]);

  if (isLoading) return <LoadingState />;
  if (error || !data) return <ErrorState message={t('settings.errors.load')} />;

  return <SettingsEditor key={String(data.id ?? data.userId)} initialValue={data} />;
}

function SettingsEditor({ initialValue }: { initialValue: UiSettings }) {
  const { t } = useLocale();
  const dispatch = useAppDispatch();
  const [updateSettings, { isLoading: saving }] = useUpdateUiSettingsMutation();
  const [formValue, setFormValue] = useState<UiSettings>(initialValue);
  const [savedPayload, setSavedPayload] = useState<ComparableFormValue>(() => settingsComparablePayload(initialValue));
  const { notifySuccess, notifyError } = useNotifications();
  const currentPayload = useMemo(() => settingsComparablePayload(formValue), [formValue]);
  const formModified = isFormModified(savedPayload, currentPayload);

  function setField<TKey extends keyof UiSettings>(key: TKey, value: UiSettings[TKey]) {
    setFormValue((current) => ({ ...current, [key]: value }));
  }

  const saveTask = useSingleFlightAction(async () => {
    if (!formModified) return;
    try {
      const saved = await updateSettings(formValue).unwrap();
      dispatch(applyUiSettings(saved));
      setFormValue(saved);
      setSavedPayload(settingsComparablePayload(saved));
      notifySuccess(t('settings.notifications.saved'));
    } catch {
      notifyError(t('settings.errors.save'));
    }
  });

  const saveBusy = saving || saveTask.running;

  return (
    <div className="page-stack">
      <BlueCard eyebrow={t('settings.eyebrow')} title={t('settings.title')}>
        <div className="settings-grid">
          <PreferenceSelect
            label={t('settings.fields.theme')}
            value={formValue.theme}
            options={blueThemeOptions}
            optionKeyPrefix="settings.theme"
            onChange={(value) => setField('theme', value as UiSettings['theme'])}
          />
          <PreferenceSelect
            label={t('settings.fields.textSize')}
            value={formValue.textSize}
            options={textSizeOptions}
            optionKeyPrefix="settings.textSize"
            onChange={(value) => setField('textSize', value as UiSettings['textSize'])}
          />
          <PreferenceSelect
            label={t('settings.fields.density')}
            value={formValue.density}
            options={densityOptions}
            optionKeyPrefix="settings.density"
            onChange={(value) => setField('density', value as UiSettings['density'])}
          />
          <PreferenceSelect
            label={t('settings.fields.sidebar')}
            value={formValue.sidebarMode}
            options={sidebarOptions}
            optionKeyPrefix="settings.sidebar"
            onChange={(value) => setField('sidebarMode', value as UiSettings['sidebarMode'])}
          />
          <BlueSelect
            label={t('settings.fields.defaultLandingPage')}
            value={formValue.defaultLandingPage}
            options={[...landingPages]}
            optionLabels={Object.fromEntries(landingPages.map((path) => [path, t(`settings.landing.${path}`)]))}
            onChange={(event) => setField('defaultLandingPage', event.target.value as UiSettings['defaultLandingPage'])}
          />
          <label className="toggle-card">
            <input type="checkbox" checked={formValue.highContrast} disabled={saveBusy} onChange={(event) => setField('highContrast', event.target.checked)} />
            <span>
              <strong>{t('settings.fields.highContrast')}</strong>
              <small>{t('settings.help.highContrast')}</small>
            </span>
          </label>
          <label className="toggle-card">
            <input type="checkbox" checked={formValue.reduceMotion} disabled={saveBusy} onChange={(event) => setField('reduceMotion', event.target.checked)} />
            <span>
              <strong>{t('settings.fields.reduceMotion')}</strong>
              <small>{t('settings.help.reduceMotion')}</small>
            </span>
          </label>
        </div>
        <div className="settings-actions">
          <BlueButton type="button" onClick={() => { void saveTask.run(); }} disabled={saveBusy || !formModified} title={!saveBusy && !formModified ? t('schema.form.noChanges') : undefined}>
            {saveBusy ? t('schema.form.saving') : t('settings.actions.save')}
          </BlueButton>
        </div>
      </BlueCard>
    </div>
  );
}

interface PreferenceOption {
  value: string;
  label: string;
  description: string;
}

function PreferenceSelect({ label, value, options, optionKeyPrefix, onChange }: { label: string; value: string; options: PreferenceOption[]; optionKeyPrefix: string; onChange: (value: string) => void }) {
  const { t } = useLocale();
  const optionLabels = Object.fromEntries(options.map((option) => [option.value, t(`${optionKeyPrefix}.${option.value}.label`)]));
  const selectedOption = options.find((option) => option.value === value);
  const selectedDescriptionKey = selectedOption ? `${optionKeyPrefix}.${selectedOption.value}.description` : null;

  return (
    <div className="preference-select">
      <BlueSelect label={label} value={value} options={options.map((option) => option.value)} optionLabels={optionLabels} onChange={(event) => onChange(event.target.value)} />
      <p className="hint">{selectedDescriptionKey ? t(selectedDescriptionKey) : ''}</p>
    </div>
  );
}

function settingsComparablePayload(settings: UiSettings): ComparableFormValue {
  return {
    defaultLandingPage: settings.defaultLandingPage,
    density: settings.density,
    highContrast: settings.highContrast,
    reduceMotion: settings.reduceMotion,
    sidebarMode: settings.sidebarMode,
    textSize: settings.textSize,
    theme: settings.theme,
  };
}
