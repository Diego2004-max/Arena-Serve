import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Cancha, EstadoCancha, TipoCancha } from '../../../core/models/cancha.model';
import { CanchaService } from '../../../core/services/cancha.service';

@Component({
  selector: 'app-cancha-form',
  template: `
    <form [formGroup]="form" (ngSubmit)="save()" class="panel">
      <input placeholder="Nombre" formControlName="nombre">
      <select formControlName="tipoCancha"><option>FUTBOL_5</option><option>FUTBOL_7</option><option>FUTBOL_11</option></select>
      <input type="number" placeholder="Capacidad" formControlName="capacidad">
      <input type="number" placeholder="Precio hora" formControlName="precioHora">
      <button>Guardar cancha</button>
    </form>
  `
})
export class CanchaFormComponent implements OnChanges {
  @Input() cancha?: Cancha;
  @Output() saved = new EventEmitter<void>();
  form = this.fb.group({ nombre: ['', Validators.required], tipoCancha: ['FUTBOL_5', Validators.required], capacidad: [10, Validators.required], precioHora: [80000, Validators.required], estadoCancha: ['ACTIVA'] });
  constructor(private fb: FormBuilder, private service: CanchaService) {}
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['cancha'] && this.cancha) {
      this.form.patchValue(this.cancha);
    }
  }
  save(): void {
    if (this.form.invalid) return;
    const v = this.form.getRawValue();
    const payload = {
      nombre: v.nombre ?? '',
      tipoCancha: (v.tipoCancha ?? 'FUTBOL_5') as TipoCancha,
      capacidad: Number(v.capacidad ?? 0),
      precioHora: Number(v.precioHora ?? 0),
      estadoCancha: (v.estadoCancha ?? 'ACTIVA') as EstadoCancha
    };
    const request = this.cancha?.id ? this.service.actualizar(this.cancha.id, payload) : this.service.crear(payload);
    request.subscribe(() => {
      this.form.reset({ tipoCancha: 'FUTBOL_5', capacidad: 10, precioHora: 80000, estadoCancha: 'ACTIVA' });
      this.saved.emit();
    });
  }
}
