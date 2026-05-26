import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Cancha } from '../../../core/models/cancha.model';
import { Cliente } from '../../../core/models/cliente.model';
import { CanchaService } from '../../../core/services/cancha.service';
import { ClienteService } from '../../../core/services/cliente.service';
import { extractHttpErrorMessage } from '../../../core/services/http-error.util';
import { ReservaService } from '../../../core/services/reserva.service';

@Component({
  selector: 'app-reserva-form',
  template: `
    <form [formGroup]="form" (ngSubmit)="save()" class="panel">
      <div class="message error" *ngIf="errorMessage">{{errorMessage}}</div>
      <div class="message success" *ngIf="successMessage">{{successMessage}}</div>
      <select formControlName="clienteId"><option *ngFor="let c of clientes" [value]="c.id">{{c.nombre}}</option></select>
      <select formControlName="canchaId"><option *ngFor="let c of canchas" [value]="c.id">{{c.nombre}} - {{c.estadoCancha}}</option></select>
      <input type="date" formControlName="fecha"><input type="time" formControlName="horaInicio"><input type="time" formControlName="horaFin">
      <div class="message warning" *ngIf="canchaSeleccionada && canchaSeleccionada.estadoCancha !== 'ACTIVA'">Esta cancha no está activa y no puede reservarse</div>
      <button [disabled]="form.invalid || !puedeReservar()">Crear reserva</button>
    </form>
  `,
  styles: [`.message{grid-column:1/-1;border-radius:6px;padding:10px 12px}.error{background:#fee2e2;color:#991b1b}.success{background:#dcfce7;color:#166534}.warning{background:#fef3c7;color:#92400e}`]
})
export class ReservaFormComponent implements OnInit {
  @Output() saved = new EventEmitter<void>();
  clientes: Cliente[] = [];
  canchas: Cancha[] = [];
  errorMessage = '';
  successMessage = '';
  form = this.fb.group({ clienteId: [0, Validators.required], canchaId: [0, Validators.required], fecha: [new Date().toISOString().slice(0, 10), Validators.required], horaInicio: ['08:00', Validators.required], horaFin: ['09:00', Validators.required], serviciosAdicionalesIds: [[] as number[]] });
  constructor(private fb: FormBuilder, private clientesService: ClienteService, private canchasService: CanchaService, private reservas: ReservaService) {}
  ngOnInit(): void {
    this.clientesService.listar().subscribe(c => { this.clientes = c; this.form.patchValue({ clienteId: c[0]?.id ?? 0 }); });
    this.canchasService.listar().subscribe(c => { this.canchas = c; this.form.patchValue({ canchaId: c[0]?.id ?? 0 }); });
  }
  get canchaSeleccionada(): Cancha | undefined {
    return this.canchas.find(cancha => cancha.id === Number(this.form.getRawValue().canchaId));
  }
  puedeReservar(): boolean {
    return this.canchaSeleccionada?.estadoCancha === 'ACTIVA';
  }
  save(): void {
    this.errorMessage = '';
    this.successMessage = '';
    if (this.form.invalid || !this.puedeReservar()) {
      if (!this.puedeReservar()) {
        this.errorMessage = 'Esta cancha no está activa y no puede reservarse';
      }
      return;
    }
    const v = this.form.getRawValue();
    this.reservas.crear({
      clienteId: Number(v.clienteId),
      canchaId: Number(v.canchaId),
      fecha: v.fecha!,
      horaInicio: v.horaInicio!,
      horaFin: v.horaFin!,
      serviciosAdicionalesIds: []
    }).subscribe({
      next: () => {
        this.successMessage = 'Reserva creada correctamente';
        this.form.patchValue({ fecha: new Date().toISOString().slice(0, 10), horaInicio: '08:00', horaFin: '09:00' });
        this.saved.emit();
      },
      error: error => this.errorMessage = extractHttpErrorMessage(error)
    });
  }
}
