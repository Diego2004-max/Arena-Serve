import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  template: `<header class="navbar"><strong>ArenaReserve</strong><button (click)="auth.logout()">Salir</button></header>`,
  styles: [`.navbar{height:64px;background:#fff;border-bottom:1px solid #dce3ee;display:flex;align-items:center;justify-content:space-between;padding:0 24px}button{background:#0f766e;color:white;border:0;border-radius:6px;padding:10px 14px;cursor:pointer}`]
})
export class NavbarComponent {
  constructor(public auth: AuthService) {}
}
