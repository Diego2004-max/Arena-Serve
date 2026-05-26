import { Component, OnInit } from '@angular/core';
import { Cancha, EstadoCancha } from '../../../core/models/cancha.model';
import { CanchaService } from '../../../core/services/cancha.service';

@Component({
  selector: 'app-cancha-list',
  template: `
    <h2>Canchas</h2>
    <app-cancha-form [cancha]="seleccionada" (saved)="onSaved()"></app-cancha-form>
    <table><tr><th>Nombre</th><th>Tipo</th><th>Capacidad</th><th>Precio</th><th>Estado</th><th></th></tr>
      <tr *ngFor="let c of canchas"><td>{{c.nombre}}</td><td>{{c.tipoCancha}}</td><td>{{c.capacidad}}</td><td>{{c.precioHora | currency:'COP':'symbol-narrow'}}</td><td><span class="badge" [ngClass]="c.estadoCancha.toLowerCase()">{{c.estadoCancha}}</span></td>
      <td><button (click)="seleccionada=c">Editar</button><select #estado [value]="c.estadoCancha"><option>ACTIVA</option><option>INACTIVA</option><option>MANTENIMIENTO</option></select><button (click)="estadoCancha(c.id, estado.value)">Cambiar</button></td></tr>
    </table>
  `,
  styles: [`.badge{display:inline-flex;align-items:center;border-radius:999px;padding:5px 10px;font-weight:700;font-size:12px}.activa{background:#dcfce7;color:#166534}.inactiva{background:#fee2e2;color:#991b1b}.mantenimiento{background:#fef3c7;color:#92400e}`]
})
export class CanchaListComponent implements OnInit {
  canchas: Cancha[] = [];
  seleccionada?: Cancha;
  constructor(private service: CanchaService) {}
  ngOnInit(): void { this.load(); }
  load(): void { this.service.listar().subscribe(data => this.canchas = data); }
  onSaved(): void { this.seleccionada = undefined; this.load(); }
  estadoCancha(id: number, estado: string): void { this.service.cambiarEstado(id, estado as EstadoCancha).subscribe(() => this.load()); }
}
