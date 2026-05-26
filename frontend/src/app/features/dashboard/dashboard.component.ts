import { Component, OnInit } from '@angular/core';
import { Dashboard } from '../../core/models/dashboard.model';
import { DashboardService } from '../../core/services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  template: `
    <h2>Dashboard</h2>
    <section class="cards" *ngIf="data">
      <article><span>Reservas hoy</span><strong>{{data.reservasHoy}}</strong></article>
      <article><span>Pendientes</span><strong>{{data.reservasPendientes}}</strong></article>
      <article><span>Confirmadas</span><strong>{{data.reservasConfirmadas}}</strong></article>
      <article><span>Ingresos dia</span><strong>{{data.ingresosDelDia | currency:'COP':'symbol-narrow'}}</strong></article>
    </section>
    <h3>Proximas reservas</h3>
    <table><tr><th>Cliente</th><th>Cancha</th><th>Fecha</th><th>Hora</th><th>Estado</th></tr>
      <tr *ngFor="let r of data?.proximasReservas"><td>{{r.clienteNombre}}</td><td>{{r.canchaNombre}}</td><td>{{r.fecha}}</td><td>{{r.horaInicio}} - {{r.horaFin}}</td><td>{{r.estadoReserva}}</td></tr>
    </table>
  `,
  styles: [`.cards{display:grid;grid-template-columns:repeat(4,minmax(150px,1fr));gap:16px}article{background:#fff;border-radius:8px;padding:20px;border:1px solid #dce3ee}span{color:#64748b}strong{display:block;font-size:30px;color:#10233f;margin-top:8px}table{width:100%;background:#fff;border-collapse:collapse;border-radius:8px;overflow:hidden}th,td{padding:12px;border-bottom:1px solid #e2e8f0;text-align:left}@media(max-width:900px){.cards{grid-template-columns:1fr 1fr}}`]
})
export class DashboardComponent implements OnInit {
  data?: Dashboard;
  constructor(private service: DashboardService) {}
  ngOnInit(): void { this.service.obtener().subscribe(data => this.data = data); }
}
