// Copyright (c) Khaled Shawki. All rights reserved.

import BlueAlert from '../components/BlueAlert';
import { useLocale } from '../i18n/LocaleProvider';
import type { SchemaDataSourceStatus } from './useSchemaDataSources';

interface Props {
  status?: SchemaDataSourceStatus;
}

export default function SchemaWidgetState({ status = 'idle' }: Props) {
  return (
    <div className={`schema-widget-state schema-widget-state--${status}`}>
      <SchemaWidgetStateContent status={status} />
    </div>
  );
}

function SchemaWidgetStateContent({ status }: Required<Props>) {
  const { t } = useLocale();
  if (status === 'idle' || status === 'loading') {
    return t('common.loading.default');
  }
  if (status === 'empty') {
    return <div className="empty-state">{t('dashboard.states.empty')}</div>;
  }
  if (status === 'forbidden') {
    return <BlueAlert tone="warning" message={t('dashboard.states.forbidden')} />;
  }
  if (status === 'notConnected') {
    return <BlueAlert tone="info" message={t('dashboard.states.notConnected')} />;
  }
  return <BlueAlert message={t('dashboard.states.error')} />;
}
