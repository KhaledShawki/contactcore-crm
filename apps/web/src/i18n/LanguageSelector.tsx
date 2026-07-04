// Copyright (c) Khaled Shawki. All rights reserved.

import { setCurrentUser } from '../auth/authSlice';
import { useNotifications } from '../notifications/useNotifications';
import { useUpdateProfileLocaleMutation } from '../profile/profileApi';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { supportedLocales, type SupportedLocale } from './localeRegistry';
import { useLocale } from './LocaleProvider';

const localeOptions = Object.values(supportedLocales);

export default function LanguageSelector() {
  const { locale, direction, setAnonymousLocale, t } = useLocale();
  const user = useAppSelector((state) => state.auth.user);
  const dispatch = useAppDispatch();
  const { notifySuccess, notifyError } = useNotifications();
  const [updateLocale, { isLoading }] = useUpdateProfileLocaleMutation();

  async function changeLocale(nextLocale: SupportedLocale) {
    if (nextLocale === locale) {
      return;
    }

    if (!user) {
      setAnonymousLocale(nextLocale);
      return;
    }
    try {
      await updateLocale(nextLocale).unwrap();
      dispatch(setCurrentUser({ ...user, locale: nextLocale, direction: supportedLocales[nextLocale].direction }));
      notifySuccess(t('locale.selector.saved'));
    } catch {
      notifyError(t('locale.selector.failed'));
    }
  }

  return (
    <label className="language-selector" dir={direction} data-direction={direction}>
      <span className="language-selector__accessible-label">{t('locale.selector.label')}</span>
      <select
        className="language-selector__select"
        aria-label={t('locale.selector.label')}
        title={t('locale.selector.label')}
        value={locale}
        disabled={isLoading}
        onChange={(event) => { void changeLocale(event.target.value as SupportedLocale); }}
      >
        {localeOptions.map((definition) => (
          <option key={definition.locale} value={definition.locale} dir={definition.direction}>
            {definition.nativeLabel}
          </option>
        ))}
      </select>
      <span className="language-selector__chevron" aria-hidden="true">⌄</span>
    </label>
  );
}
