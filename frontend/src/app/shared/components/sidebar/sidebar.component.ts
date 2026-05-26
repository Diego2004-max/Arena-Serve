import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  template: `
    <aside>
      <h1>ArenaReserve</h1>
      <a routerLink="/dashboard" routerLinkActive="active">Dashboard</a>
      <a routerLink="/clientes" routerLinkActive="active">Clientes</a>
      <a routerLink="/canchas" routerLinkActive="active">Canchas</a>
      <a routerLink="/canchas/disponibilidad" routerLinkActive="active">Disponibilidad</a>
      <a routerLink="/reservas" routerLinkActive="active">Reservas</a>
      <a routerLink="/pagos" routerLinkActive="active">Pagos</a>
      <a *ngIf="auth.getRole()==='ADMIN'" routerLink="/usuarios" routerLinkActive="active">Usuarios</a>
    </aside>
  `,
  styles: [`aside{height:100%;background:#10233f;color:#fff;padding:24px 18px;display:flex;flex-direction:column;gap:8px}h1{font-size:22px;margin:0 0 20px}a{color:#dbeafe;text-decoration:none;padding:12px;border-radius:6px}.active,a:hover{background:#0f766e}@media(max-width:820px){aside{height:auto}}`]
})
export class SidebarComponent {
  constructor(public auth: AuthService) {}
}
