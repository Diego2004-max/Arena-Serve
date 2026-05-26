import { AbstractControl, ValidationErrors } from '@angular/forms';

export function futureDateValidator(control: AbstractControl): ValidationErrors | null {
  if (!control.value) return null;
  const today = new Date().toISOString().slice(0, 10);
  return control.value >= today ? null : { pastDate: true };
}
