import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Cliente } from '../../../core/models/cliente.model';
import { ClienteService } from '../../../core/services/cliente.service';

@Component({
  selector: 'app-cliente-form',
  template: `
    <form [formGroup]="form" (ngSubmit)="save()" class="panel">
      <input placeholder="Nombre" formControlName="nombre"><input placeholder="Documento" formControlName="documento">
      <input placeholder="Email" formControlName="email"><input placeholder="Telefono" formControlName="telefono">
      <input placeholder="Direccion" formControlName="direccion"><button>Guardar cliente</button>
    </form>
  `
})
export class ClienteFormComponent implements OnChanges {
  @Input() cliente?: Cliente;
  @Output() saved = new EventEmitter<void>();
  form = this.fb.group({ nombre: ['', Validators.required], documento: ['', Validators.required], email: ['', [Validators.required, Validators.email]], telefono: [''], direccion: [''] });
  constructor(private fb: FormBuilder, private service: ClienteService) {}
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['cliente'] && this.cliente) {
      this.form.patchValue(this.cliente);
    }
  }
  save(): void {
    if (this.form.invalid) return;
    const v = this.form.getRawValue();
    const payload = {
      nombre: v.nombre ?? '',
      documento: v.documento ?? '',
      email: v.email ?? '',
      telefono: v.telefono ?? '',
      direccion: v.direccion ?? ''
    };
    const request = this.cliente?.id ? this.service.actualizar(this.cliente.id, payload) : this.service.crear(payload);
    request.subscribe(() => { this.form.reset(); this.saved.emit(); });
  }
}
