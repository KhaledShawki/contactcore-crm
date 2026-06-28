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

const landingPages = ['/dashboard', '/customers', '/leads', '/suppliers', '/reports'] as const;

export default function SettingsPage() {
  const dispatch = useAppDispatch();
  const { data, isLoading, error } = useGetUiSettingsQuery();

  useEffect(() => {
    if (data) {
      dispatch(applyUiSettings(data));
    }
  }, [data, dispatch]);

  if (isLoading) return <LoadingState />;
  if (error || !data) return <ErrorState message="Could not load UI settings." />;

  return <SettingsEditor key={String(data.id ?? data.userId)} initialValue={data} />;
}

function SettingsEditor({ initialValue }: { initialValue: UiSettings }) {
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
      notifySuccess('UI settings saved.');
    } catch {
      notifyError('Could not save UI settings. Try again.');
    }
  });

  const saveBusy = saving || saveTask.running;

  return (
    <div className="page-stack">
      <BlueCard eyebrow="Preferences" title="UI Settings">
        <div className="settings-grid">
          <PreferenceSelect
            label="Theme"
            value={formValue.theme}
            options={blueThemeOptions}
            onChange={(value) => setField('theme', value as UiSettings['theme'])}
          />
          <PreferenceSelect
            label="Text size"
            value={formValue.textSize}
            options={textSizeOptions}
            onChange={(value) => setField('textSize', value as UiSettings['textSize'])}
          />
          <PreferenceSelect
            label="Density"
            value={formValue.density}
            options={densityOptions}
            onChange={(value) => setField('density', value as UiSettings['density'])}
          />
          <PreferenceSelect
            label="Sidebar"
            value={formValue.sidebarMode}
            options={sidebarOptions}
            onChange={(value) => setField('sidebarMode', value as UiSettings['sidebarMode'])}
          />
          <BlueSelect
            label="Default landing page"
            value={formValue.defaultLandingPage}
            options={[...landingPages]}
            onChange={(event) => setField('defaultLandingPage', event.target.value as UiSettings['defaultLandingPage'])}
          />
          <label className="toggle-card">
            <input type="checkbox" checked={formValue.highContrast} disabled={saveBusy} onChange={(event) => setField('highContrast', event.target.checked)} />
            <span>
              <strong>High contrast</strong>
              <small>Increase border and text contrast for long CRM sessions.</small>
            </span>
          </label>
          <label className="toggle-card">
            <input type="checkbox" checked={formValue.reduceMotion} disabled={saveBusy} onChange={(event) => setField('reduceMotion', event.target.checked)} />
            <span>
              <strong>Reduce motion</strong>
              <small>Disable non-essential transitions and animations.</small>
            </span>
          </label>
        </div>
        <div className="settings-actions">
          <BlueButton type="button" onClick={() => { void saveTask.run(); }} disabled={saveBusy || !formModified} title={!saveBusy && !formModified ? 'No changes to save.' : undefined}>
            {saveBusy ? 'Saving...' : 'Save settings'}
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

function PreferenceSelect({ label, value, options, onChange }: { label: string; value: string; options: PreferenceOption[]; onChange: (value: string) => void }) {
  return (
    <div className="preference-select">
      <BlueSelect label={label} value={value} options={options.map((option) => option.value)} onChange={(event) => onChange(event.target.value)} />
      <p className="hint">{options.find((option) => option.value === value)?.description}</p>
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
