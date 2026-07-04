// Copyright (c) Khaled Shawki. All rights reserved.

import { useMemo, useRef, useState } from 'react';
import BlueButton from '../components/BlueButton';
import BlueInput from '../components/BlueInput';
import BlueSelect from '../components/BlueSelect';
import { useLocale } from '../i18n/LocaleProvider';
import { DirectionalText } from '../i18n/components/DirectionalText';
import { useNotifications } from '../notifications/useNotifications';
import {
  useDisconnectConnectorSessionMutation,
  useGetConnectorSessionQuery,
  useListConnectorInstancesQuery,
  useLoginConnectorSessionMutation,
} from './connectorApi';

export default function ConnectorSessionPanel() {
  const { t } = useLocale();
  const { data: instances = [] } = useListConnectorInstancesQuery();
  const { data: session } = useGetConnectorSessionQuery();
  const [loginConnector, { isLoading: connecting }] = useLoginConnectorSessionMutation();
  const [disconnectConnector, { isLoading: disconnecting }] = useDisconnectConnectorSessionMutation();
  const { notifySuccess, notifyError } = useNotifications();
  const dialogRef = useRef<HTMLDialogElement>(null);
  const [selectedId, setSelectedId] = useState<number | ''>('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const selectedInstance = useMemo(
    () => instances.find((instance) => instance.id === selectedId) ?? instances[0],
    [instances, selectedId],
  );

  function openDialog() {
    const dialog = dialogRef.current;
    if (dialog && !dialog.open) {
      dialog.showModal();
    }
  }

  function closeDialog() {
    const dialog = dialogRef.current;
    if (dialog?.open) {
      dialog.close();
    }
  }

  async function connect() {
    if (!selectedInstance || !username.trim() || !password) return;
    try {
      await loginConnector({ connectorInstanceId: selectedInstance.id, username, password }).unwrap();
      setPassword('');
      closeDialog();
      notifySuccess(t('connectors.panel.connected', { name: selectedInstance.displayName }));
    } catch {
      notifyError(t('connectors.panel.connectFailed'));
    }
  }

  async function disconnect() {
    try {
      await disconnectConnector().unwrap();
      notifySuccess(t('connectors.panel.disconnected'));
    } catch {
      notifyError(t('connectors.panel.disconnectFailed'));
    }
  }

  if (instances.length === 0) {
    return <span className="connector-pill connector-pill--muted">{t('connectors.none')}</span>;
  }

  return (
    <div className="connector-session">
      {session?.connected ? (
        <>
          <span className="connector-pill" title={session.externalUsername ? t('connectors.activeTitle', { username: session.externalUsername }) : undefined}>
            <DirectionalText value={session.connectorDisplayName} />
          </span>
          <BlueButton variant="secondary" disabled={disconnecting} onClick={() => { void disconnect(); }}>
            {disconnecting ? t('connectors.panel.disconnecting') : t('common.actions.disconnect')}
          </BlueButton>
        </>
      ) : (
        <>
          <span className="connector-pill connector-pill--muted">{t('connectors.inactive')}</span>
          <BlueButton variant="secondary" onClick={openDialog}>{t('connectors.panel.connectCta')}</BlueButton>
        </>
      )}

      <dialog
        ref={dialogRef}
        className="connector-modal"
        aria-labelledby="connector-login-title"
      >
        <header>
          <div>
            <p className="eyebrow">{t('connectors.panel.eyebrow')}</p>
            <h2 id="connector-login-title">{t('connectors.modal.title')}</h2>
          </div>
          <button type="button" className="link-button" onClick={closeDialog}>{t('common.actions.close')}</button>
        </header>
        <BlueSelect
          label={t('connectors.modal.company')}
          value={String(selectedInstance?.id ?? '')}
          options={instances.map((instance) => String(instance.id))}
          optionLabels={Object.fromEntries(instances.map((instance) => [String(instance.id), `${instance.displayName} (${instance.environment})`]))}
          onChange={(event) => setSelectedId(Number(event.target.value))}
        />
        {selectedInstance && (
          <p className="hint">
            {t('connectors.modal.selected', { name: selectedInstance.displayName, environment: selectedInstance.environment })}
          </p>
        )}
        <BlueInput label={t('connectors.modal.username')} value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" />
        <BlueInput label={t('connectors.modal.password')} type="password" value={password} onChange={(event) => setPassword(event.target.value)} autoComplete="current-password" />
        <div className="modal-actions">
          <BlueButton variant="secondary" onClick={closeDialog}>{t('common.actions.cancel')}</BlueButton>
          <BlueButton disabled={!selectedInstance || !username.trim() || !password || connecting} onClick={() => { void connect(); }}>
            {connecting ? t('connectors.modal.connecting') : t('common.actions.connect')}
          </BlueButton>
        </div>
      </dialog>
    </div>
  );
}
