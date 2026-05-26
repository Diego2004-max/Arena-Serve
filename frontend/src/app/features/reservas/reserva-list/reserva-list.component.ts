import { Component, OnInit } from '@angular/core';
import { Reserva } from '../../../core/models/reserva.model';
import { ReservaService } from '../../../core/services/reserva.service';

@Component({
  selector: 'app-reserva-list',
  template: `
    <h2>Reservas</h2>
    <app-reserva-form (saved)="load()"></app-reserva-form>
    <table><tr><th>Cliente</th><th>Cancha</th><th>Fecha</th><th>Hora</th><th>Total</th><th>Estado</th><th></th></tr>
      <tr *ngFor="let r of reservas"><td>{{r.clienteNombre}}</td><td>{{r.canchaNombre}}</td><td>{{r.fecha}}</td><td>{{r.horaInicio}} - {{r.horaFin}}</td><td>{{r.total | currency:'COP':'symbol-narrow'}}</td><td>{{r.estadoReserva}}</td>
      <td><button (click)="cancelar(r.id)">Cancelar</button><button (click)="finalizar(r.id)">Finalizar</button></td></tr>
    </table>
  `
})
export class ReservaListComponent implements OnInit {
  reservas: Reserva[] = [];
  constructor(private service: ReservaService) {}
  ngOnInit(): void { this.load(); }
  load(): void { this.service.listar().subscribe(data => this.reservas = data); }
  cancelar(id: number): void { this.service.cancelar(id).subscribe(() => this.load()); }
  finalizar(id: number): void { this.service.finalizar(id).subscribe(() => this.load()); }
}
