import { Component, Input } from '@angular/core';
import { Reserva } from '../../../core/models/reserva.model';

@Component({
  selector: 'app-reserva-detail',
  template: `<article *ngIf="reserva"><strong>{{reserva.clienteNombre}}</strong><span>{{reserva.canchaNombre}} - {{reserva.estadoReserva}}</span></article>`
})
export class ReservaDetailComponent {
  @Input() reserva?: Reserva;
}
