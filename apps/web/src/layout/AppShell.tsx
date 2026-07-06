// Copyright (c) Khaled Shawki. All rights reserved.

import { Navigate, NavLink, Route, Routes, useLocation } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { useGetManifestQuery } from '../schema/schemaApi';
import { logout } from '../auth/authSlice';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import LoadingState from '../components/LoadingState';
import ErrorState from '../components/ErrorState';
import SchemaListPage from '../pages/SchemaListPage';
import SchemaFormPage from '../pages/SchemaFormPage';
import ProfilePage from '../pages/ProfilePage';
import SchemaDashboardPage from '../schema-renderer/SchemaDashboardPage';
import MarketingSourcesPage from '../pages/MarketingSourcesPage';
import ReportPage from '../pages/ReportPage';
import BlueButton from '../components/BlueButton';
import SettingsPage from '../pages/SettingsPage';
import AssistantPage from '../assistant/AssistantPage';
import { useGetUiSettingsQuery } from '../settings/settingsApi';
import { applyUiSettings } from '../theme/themeSlice';
import ConnectorSessionPanel from '../connectors/ConnectorSessionPanel';
import LanguageSelector from '../i18n/LanguageSelector';
import { useLocale } from '../i18n/LocaleProvider';
import { routeIsVisible } from '../schema/capabilities';

export default function AppShell() {
  const { t } = useLocale();
  const { data: manifest, isLoading, error } = useGetManifestQuery();
  const { data: uiSettings } = useGetUiSettingsQuery();
  const [mobileNavOpen, setMobileNavOpen] = useState(false);
  const user = useAppSelector((state) => state.auth.user);
  const dispatch = useAppDispatch();
  const { pathname } = useLocation();

  useEffect(() => {
    if (uiSettings) {
      dispatch(applyUiSettings(uiSettings));
    }
  }, [dispatch, uiSettings]);

  const visibleRoutes = manifest?.routes.filter(routeIsVisible) ?? [];
  const activeRoute = visibleRoutes.find((route) => pathname === route.path || pathname.startsWith(`${route.path}/`));
  const activeRouteLabel = activeRoute ? t(activeRoute.labelKey ?? `navigation.${activeRoute.screenKey}`, undefined) : t('layout.topbar.workspace');

  if (isLoading) return <LoadingState />;
  if (error || !manifest) return <ErrorState message={t('layout.errors.schema')} />;

  const nav = (
    <nav className="sidebar-nav" aria-label={t('layout.navigation.main')}>
      {visibleRoutes.map((route) => (
        <NavLink key={route.path} to={route.path} onClick={() => setMobileNavOpen(false)}>
          <span className="nav-dot" aria-hidden="true" />
          <span>{t(route.labelKey ?? `navigation.${route.screenKey}`)}</span>
        </NavLink>
      ))}
    </nav>
  );

  return (
    <div className="app-shell">
      <aside className={`sidebar ${mobileNavOpen ? 'sidebar--open' : ''}`}>
        <div className="sidebar-brand">
          <div className="brand-mark" aria-hidden="true">CC</div>
          <div>
            <div className="brand-text">{manifest.appName}</div>
            <span className="brand-subtitle">{t('layout.sidebar.subtitle')}</span>
          </div>
        </div>
        {nav}
        <div className="sidebar-footer">
          <span className="signed-in-label">{t('layout.sidebar.signedInAs')}</span>
          <strong>{user?.displayName ?? t('common.user')}</strong>
          <BlueButton variant="secondary" onClick={() => dispatch(logout())}>{t('common.actions.logout')}</BlueButton>
        </div>
      </aside>

      {mobileNavOpen && <button type="button" className="mobile-backdrop" aria-label={t('layout.navigation.close')} onClick={() => setMobileNavOpen(false)} />}

      <main className="main-shell">
        <header className="topbar">
          <div className="topbar-left">
            <button type="button" className="nav-toggle" aria-label={t('layout.navigation.open')} onClick={() => setMobileNavOpen(true)}>☰</button>
            <div>
              <p className="eyebrow">ContactCore</p>
              <h1>{activeRouteLabel}</h1>
            </div>
          </div>
          <div className="topbar-actions">
            <ConnectorSessionPanel />
            <LanguageSelector />
            <NavLink className="settings-pill" to="/settings">{t('layout.topbar.uiSettings')}</NavLink>
            <span className="user-pill">{user?.displayName ?? user?.username ?? t('common.user')}</span>
          </div>
        </header>

        <section className="content">
          <Routes>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/dashboard" element={<SchemaDashboardPage screenKey="dashboard" />} />
            <Route path="/customers" element={<SchemaListPage screenKey="customers" />} />
            <Route path="/customers/:id" element={<SchemaFormPage screenKey="customers" />} />
            <Route path="/leads" element={<SchemaListPage screenKey="leads" />} />
            <Route path="/leads/:id" element={<SchemaFormPage screenKey="leads" />} />
            <Route path="/suppliers" element={<SchemaListPage screenKey="suppliers" />} />
            <Route path="/suppliers/:id" element={<SchemaFormPage screenKey="suppliers" />} />
            <Route path="/marketing-sources" element={<MarketingSourcesPage />} />
            <Route path="/reports" element={<ReportPage />} />
            <Route path="/assistant" element={<AssistantPage />} />
            <Route path="/settings" element={<SettingsPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </section>
      </main>
    </div>
  );
}
