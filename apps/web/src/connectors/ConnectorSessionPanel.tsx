// Copyright (c) Khaled Shawki. All rights reserved.

import { useMemo, useRef, useState } from 'react';
import BlueButton from '../components/BlueButton';
import BlueInput from '../components/BlueInput';
import BlueSelect from '../components/BlueSelect';
import { useNotifications } from '../notifications/useNotifications';
import {
  useDisconnectConnectorSessionMutation,
  useGetConnectorSessionQuery,
  useListConnectorInstancesQuery,
  useLoginConnectorSessionMutation,
} from './connectorApi';

export default function ConnectorSessionPanel() {
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
      notifySuccess(`Connected to ${selectedInstance.displayName}.`);
    } catch {
      notifyError('Could not connect to the selected CRM connector. Check the company, username, and password.');
    }
  }

  async function disconnect() {
    try {
      await disconnectConnector().unwrap();
      notifySuccess('CRM connector disconnected.');
    } catch {
      notifyError('Could not disconnect the CRM connector. Try again.');
    }
  }

  if (instances.length === 0) {
    return <span className="connector-pill connector-pill--muted">No CRM connector</span>;
  }

  return (
    <div className="connector-session">
      {session?.connected ? (
        <>
          <span className="connector-pill" title={session.externalUsername ?? undefined}>
            {session.connectorDisplayName}
          </span>
          <BlueButton variant="secondary" disabled={disconnecting} onClick={() => { void disconnect(); }}>
            {disconnecting ? 'Disconnecting...' : 'Disconnect'}
          </BlueButton>
        </>
      ) : (
        <>
          <span className="connector-pill connector-pill--muted">CRM connector inactive</span>
          <BlueButton variant="secondary" onClick={openDialog}>Connect CRM</BlueButton>
        </>
      )}

      <dialog
        ref={dialogRef}
        className="connector-modal"
        aria-labelledby="connector-login-title"
      >
        <header>
          <div>
            <p className="eyebrow">CRM connector</p>
            <h2 id="connector-login-title">Connect to SAP Business One</h2>
          </div>
          <button type="button" className="link-button" onClick={closeDialog}>Close</button>
        </header>
        <BlueSelect
          label="Company"
          value={String(selectedInstance?.id ?? '')}
          options={instances.map((instance) => String(instance.id))}
          onChange={(event) => setSelectedId(Number(event.target.value))}
        />
        {selectedInstance && <p className="hint">Selected: {selectedInstance.displayName} ({selectedInstance.environment})</p>}
        <BlueInput label="SAP username" value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" />
        <BlueInput label="SAP password" type="password" value={password} onChange={(event) => setPassword(event.target.value)} autoComplete="current-password" />
        <div className="modal-actions">
          <BlueButton variant="secondary" onClick={closeDialog}>Cancel</BlueButton>
          <BlueButton disabled={!selectedInstance || !username.trim() || !password || connecting} onClick={() => { void connect(); }}>
            {connecting ? 'Connecting...' : 'Connect'}
          </BlueButton>
        </div>
      </dialog>
    </div>
  );
}
