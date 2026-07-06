// Copyright (c) Khaled Shawki. All rights reserved.

import type { UiCapabilityReference, UiResourceCapabilities } from './types';

export function hasCapability(capabilities: UiResourceCapabilities | null | undefined, capability: string): boolean {
  if (!capabilities || !capability) return false;
  return capabilities.capabilities[capability] === true;
}

export function routeIsVisible(route: { visible?: boolean | null }): boolean {
  return route.visible !== false;
}

export function findCapabilitySet(capabilities: UiResourceCapabilities[] | null | undefined, resourceKey: string): UiResourceCapabilities | undefined {
  return capabilities?.find((capabilitySet) => capabilitySet.resourceKey === resourceKey);
}

export function capabilityReferenceIsAllowed(
  capabilities: UiResourceCapabilities[] | null | undefined,
  reference: UiCapabilityReference | null | undefined,
): boolean {
  if (!reference) return true;
  return hasCapability(findCapabilitySet(capabilities, reference.resourceKey), reference.capability);
}
