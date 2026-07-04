// Copyright (c) Khaled Shawki. All rights reserved.

import { useLocale } from '../i18n/LocaleProvider';

export default function LoadingState() {
  const { t } = useLocale();
  return <div className="state-card">{t('common.loading.default')}</div>;
}
