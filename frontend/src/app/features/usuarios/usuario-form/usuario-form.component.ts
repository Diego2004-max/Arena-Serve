import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { UsuarioService } from '../../../core/services/usuario.service';
import { Rol } from '../../../core/models/user.model';

@Component({
  selector: 'app-usuario-form',
  template: `
    <form [formGroup]="form" (ngSubmit)="save()" class="panel">
      <input placeholder="Email" formControlName="email"><input placeholder="Password" type="password" formControlName="password">
      <select formControlName="rol"><option>ADMIN</option><option>EMPLEADO</option></select><button>Crear usuario</button>
    </form>
  `
})
export class UsuarioFormComponent {
  @Output() saved = new EventEmitter<void>();
  form = this.fb.group({ email: ['', [Validators.required, Validators.email]], password: ['', [Validators.required, Validators.minLength(8)]], rol: ['EMPLEADO' as Rol, Validators.required] });
  constructor(private fb: FormBuilder, private service: UsuarioService) {}
  save(): void { if (this.form.invalid) return; const v = this.form.getRawValue(); this.service.crear({ email: v.email!, password: v.password!, rol: v.rol! }).subscribe(() => { this.form.reset({ rol: 'EMPLEADO' }); this.saved.emit(); }); }
}
