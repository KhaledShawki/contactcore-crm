// Copyright (c) Khaled Shawki. All rights reserved.

import type { BusinessPartner, PageResponse } from '../schema/types';

export interface ConnectorInstance {
  id: number;
  type: 'SAP_B1' | string;
  displayName: string;
  environment: 'DEV' | 'TEST' | 'PROD' | string;
  capabilities: string[];
  canReadBusinessPartners: boolean;
}

export interface ConnectorLoginRequest {
  connectorInstanceId: number;
  username: string;
  password: string;
}

export interface ConnectorSessionStatus {
  connected: boolean;
  connectorInstanceId: number | null;
  connectorType: string | null;
  connectorDisplayName: string | null;
  environment: string | null;
  externalUsername: string | null;
  connectedAt: string | null;
}

export type ConnectorBusinessPartner = BusinessPartner;
export type ConnectorBusinessPartnerPage = PageResponse<ConnectorBusinessPartner>;
