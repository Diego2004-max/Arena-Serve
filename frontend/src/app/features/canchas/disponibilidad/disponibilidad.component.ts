import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Cancha, Disponibilidad } from '../../../core/models/cancha.model';
import { CanchaService } from '../../../core/services/cancha.service';
import { DisponibilidadService } from '../../../core/services/disponibilidad.service';

@Component({
  selector: 'app-disponibilidad',
  template: `
    <h2>Disponibilidad</h2>
    <form [formGroup]="form" (ngSubmit)="buscar()" class="panel">
      <select formControlName="canchaId"><option *ngFor="let c of canchas" [value]="c.id">{{c.nombre}}</option></select>
      <input type="date" formControlName="fecha"><button>Consultar</button>
    </form>
    <div class="slots"><span *ngFor="let f of franjas" [class.ok]="f.disponible">{{f.horaInicio}} - {{f.horaFin}}</span></div>
  `,
  styles: [`.slots{display:flex;flex-wrap:wrap;gap:10px}span{padding:10px 12px;border-radius:6px;background:#fee2e2;color:#991b1b}.ok{background:#dcfce7;color:#166534}`]
})
export class DisponibilidadComponent implements OnInit {
  canchas: Cancha[] = [];
  franjas: Disponibilidad[] = [];
  form = this.fb.group({ canchaId: [0, Validators.required], fecha: [new Date().toISOString().slice(0, 10), Validators.required] });
  constructor(private fb: FormBuilder, private canchasService: CanchaService, private disponibilidad: DisponibilidadService) {}
  ngOnInit(): void { this.canchasService.listar().subscribe(c => { this.canchas = c; this.form.patchValue({ canchaId: c[0]?.id ?? 0 }); }); }
  buscar(): void { const v = this.form.getRawValue(); this.disponibilidad.consultar(Number(v.canchaId), v.fecha!).subscribe(data => this.franjas = data); }
}
