// Copyright (c) Khaled Shawki. All rights reserved.

export type FieldType = 'hidden' | 'text' | 'textarea' | 'select' | 'number' | 'checkbox';

export interface UiRoute {
  path: string;
  label: string;
  screenKey: string;
}

export interface UiManifest {
  appName: string;
  routes: UiRoute[];
}

export interface UiValidation {
  minLength?: number | null;
  maxLength?: number | null;
  pattern?: string | null;
  patternMessage?: string | null;
  inputType?: 'text' | 'email' | 'tel' | 'url' | 'number' | 'textarea' | 'select' | null;
  minNumber?: number | null;
  maxNumber?: number | null;
  helpText?: string | null;
}

export interface UiFormRule {
  type: 'atLeastOne' | string;
  fields: string[];
  message: string;
}

export interface UiField {
  key: string;
  label: string;
  type: FieldType;
  required: boolean;
  listVisible: boolean;
  formVisible: boolean;
  readOnly: boolean;
  defaultValue: string | null;
  options: string[];
  validation?: UiValidation | null;
}

export interface UiScreen {
  key: string;
  title: string;
  entityKind: string;
  listEndpoint: string;
  detailEndpoint: string;
  createEndpoint: string;
  updateEndpoint: string;
  archiveEndpoint: string;
  documentEndpoint: string;
  fields: UiField[];
  validationRules?: UiFormRule[];
}

export interface ContactPerson {
  id?: number;
  businessPartnerId?: number;
  version?: number;
  createdAt?: string;
  updatedAt?: string;
  firstName: string;
  lastName: string;
  displayName?: string;
  roleTitle?: string | null;
  email?: string | null;
  phone?: string | null;
  mobile?: string | null;
  department?: string | null;
  primaryContact: boolean;
  notes?: string | null;
}

export interface BusinessPartner {
  id?: number | null;
  externalId?: string | null;
  sourceSystem?: string | null;
  connectorInstanceId?: number | null;
  connectorDisplayName?: string | null;
  readOnly?: boolean;
  currency?: string | null;
  balance?: number | string | null;
  version?: number;
  createdAt?: string;
  updatedAt?: string;
  kind: string;
  statusCode: string;
  statusName?: string;
  sourceCode?: string | null;
  code: string;
  name: string;
  primaryEmail?: string | null;
  primaryPhone?: string | null;
  website?: string | null;
  addressLine1?: string | null;
  addressLine2?: string | null;
  city?: string | null;
  postalCode?: string | null;
  countryCode?: string | null;
  notes?: string | null;
  contactPersons?: ContactPerson[];
}

export interface MarketingSource {
  id?: number;
  version?: number;
  createdAt?: string;
  updatedAt?: string;
  code: string;
  name: string;
  sortOrder?: number | string | null;
}

export interface UserProfile {
  id?: number;
  userId?: number;
  username: string;
  email: string;
  displayName: string;
  phone?: string | null;
  jobTitle?: string | null;
  bio?: string | null;
  locale: string;
  timezone: string;
  profileImageUrl?: string | null;
}

export interface PageResponse<T> {
  items: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface DocumentAttachment {
  id: number;
  createdAt: string;
  documentTypeCode: string;
  documentTypeName: string;
  originalFilename: string;
  contentType: string | null;
  sizeBytes: number;
  downloadUrl: string;
}

export interface KpiMetric {
  key: string;
  label: string;
  value: number;
  unit: string;
  helpText: string;
}

export interface ChartPoint {
  label: string;
  value: number;
}

export interface MonthlyCountPoint {
  month: string;
  value: number;
}

export interface RecentBusinessPartner {
  id: number;
  kind: string;
  code: string;
  name: string;
  status: string;
  marketingSource: string;
  createdAt: string;
}

export interface DashboardResponse {
  kpis: KpiMetric[];
  businessPartnersByKind: ChartPoint[];
  leadsByMarketingSource: ChartPoint[];
  businessPartnersByStatus: ChartPoint[];
  newBusinessPartnersByMonth: MonthlyCountPoint[];
  contactPersonsByRole: ChartPoint[];
  contactCoverageByKind: ChartPoint[];
  recentBusinessPartners: RecentBusinessPartner[];
}

export interface MarketingSourceReportRow {
  marketingSource: string;
  leads: number;
  qualifiedLeads: number;
  customers: number;
  leadQualificationRate: number;
}

export interface UiSettings {
  id?: number | null;
  userId: number;
  theme: 'light' | 'dark' | 'ocean' | 'graphite';
  textSize: 'compact' | 'comfortable' | 'large';
  density: 'compact' | 'comfortable' | 'spacious';
  sidebarMode: 'expanded' | 'compact';
  reduceMotion: boolean;
  highContrast: boolean;
  defaultLandingPage: '/dashboard' | '/customers' | '/leads' | '/suppliers' | '/reports';
}

export interface CrmReportResponse {
  kpis: KpiMetric[];
  kindBreakdown: ChartPoint[];
  statusBreakdown: ChartPoint[];
  marketingSourceBreakdown: ChartPoint[];
  contactPersonsByRole: ChartPoint[];
  contactCoverageByKind: ChartPoint[];
  marketingSourcePerformance: MarketingSourceReportRow[];
  recentBusinessPartners: RecentBusinessPartner[];
}
