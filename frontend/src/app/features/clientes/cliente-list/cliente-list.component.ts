import { Component, OnInit } from '@angular/core';
import { Cliente } from '../../../core/models/cliente.model';
import { ClienteService } from '../../../core/services/cliente.service';

@Component({
  selector: 'app-cliente-list',
  template: `
    <h2>Clientes</h2>
    <app-cliente-form [cliente]="seleccionado" (saved)="onSaved()"></app-cliente-form>
    <table><tr><th>Nombre</th><th>Documento</th><th>Email</th><th>Telefono</th><th>Estado</th><th></th></tr>
      <tr *ngFor="let c of clientes"><td>{{c.nombre}}</td><td>{{c.documento}}</td><td>{{c.email}}</td><td>{{c.telefono}}</td><td><span class="badge" [class.inactivo]="!c.activo">{{c.activo ? 'Activo' : 'Inactivo'}}</span></td><td><button (click)="seleccionado=c">Editar</button><button *ngIf="c.activo" (click)="eliminar(c.id)">Eliminar</button><button *ngIf="!c.activo" (click)="activar(c.id)">Activar</button></td></tr>
    </table>
  `,
  styles: [`.badge{display:inline-flex;border-radius:999px;padding:5px 10px;background:#dcfce7;color:#166534;font-weight:700;font-size:12px}.inactivo{background:#fee2e2;color:#991b1b}`]
})
export class ClienteListComponent implements OnInit {
  clientes: Cliente[] = [];
  seleccionado?: Cliente;
  constructor(private service: ClienteService) {}
  ngOnInit(): void { this.load(); }
  load(): void { this.service.listar().subscribe(data => this.clientes = data); }
  onSaved(): void { this.seleccionado = undefined; this.load(); }
  eliminar(id: number): void { this.service.eliminar(id).subscribe(() => this.load()); }
  activar(id: number): void { this.service.cambiarEstado(id, true).subscribe(() => this.load()); }
}
