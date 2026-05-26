import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { PagoService } from '../../../core/services/pago.service';

@Component({
  selector: 'app-pago-form',
  template: `
    <h2>Pagos</h2>
    <form [formGroup]="form" (ngSubmit)="save()" class="panel narrow">
      <input type="number" placeholder="Reserva ID" formControlName="reservaId">
      <input type="number" placeholder="Valor" formControlName="valor">
      <select formControlName="tipoPago"><option>EFECTIVO</option><option>TRANSFERENCIA</option><option>TARJETA</option></select>
      <input placeholder="Referencia" formControlName="referencia">
      <p class="ok" *ngIf="mensaje">{{mensaje}}</p><button>Registrar pago</button>
    </form>
  `
})
export class PagoFormComponent {
  mensaje = '';
  form = this.fb.group({ reservaId: [0, Validators.required], valor: [0, Validators.required], tipoPago: ['EFECTIVO', Validators.required], referencia: [''] });
  constructor(private fb: FormBuilder, private service: PagoService) {}
  save(): void { if (this.form.invalid) return; const v = this.form.getRawValue(); this.service.registrar({ reservaId: Number(v.reservaId), valor: Number(v.valor), tipoPago: v.tipoPago!, referencia: v.referencia ?? '' }).subscribe(() => { this.mensaje = 'Pago registrado'; this.form.reset({ tipoPago: 'EFECTIVO' }); }); }
}
