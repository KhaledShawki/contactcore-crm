// Copyright (c) Khaled Shawki. All rights reserved.

export interface DownloadedReport {
  blob: Blob;
  filename: string;
}

const FALLBACK_FILENAME = 'contactcore-report.xlsx';

export function filenameFromContentDisposition(header: string | null | undefined, fallback = FALLBACK_FILENAME) {
  if (!header) return fallback;

  const utf8Match = /filename\*=UTF-8''([^;]+)/i.exec(header);
  if (utf8Match?.[1]) {
    return sanitizeFilename(decodeURIComponent(utf8Match[1]), fallback);
  }

  const quotedMatch = /filename="([^"]+)"/i.exec(header);
  if (quotedMatch?.[1]) {
    return sanitizeFilename(quotedMatch[1], fallback);
  }

  const plainMatch = /filename=([^;]+)/i.exec(header);
  if (plainMatch?.[1]) {
    return sanitizeFilename(plainMatch[1], fallback);
  }

  return fallback;
}

export function saveDownloadedReport(report: DownloadedReport) {
  const url = window.URL.createObjectURL(report.blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = report.filename;
  anchor.rel = 'noopener';
  document.body.appendChild(anchor);
  anchor.click();
  anchor.remove();
  window.URL.revokeObjectURL(url);
}

function sanitizeFilename(value: string, fallback: string) {
  const cleaned = Array.from(value)
    .map((character) => isForbiddenFilenameCharacter(character) ? '-' : character)
    .join('')
    .replace(/\s+/g, ' ')
    .trim();
  return cleaned.endsWith('.xlsx') && cleaned.length > '.xlsx'.length ? cleaned : fallback;
}


function isForbiddenFilenameCharacter(character: string) {
  return character.charCodeAt(0) < 32 || '/:*?"<>|'.includes(character);
}
