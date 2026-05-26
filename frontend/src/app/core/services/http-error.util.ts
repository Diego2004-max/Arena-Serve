import { HttpErrorResponse } from '@angular/common/http';

export function extractHttpErrorMessage(error: unknown, fallback = 'No se pudo completar la operacion'): string {
  if (error instanceof HttpErrorResponse) {
    const payload = error.error as { message?: unknown } | string | null;
    if (payload && typeof payload === 'object' && typeof payload.message === 'string' && payload.message.trim()) {
      return payload.message;
    }
    if (typeof payload === 'string' && payload.trim()) {
      return payload;
    }
    if (error.message) {
      return error.message;
    }
  }
  if (error instanceof Error && error.message) {
    return error.message;
  }
  return fallback;
}
