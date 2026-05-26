import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';
import { LoginComponent } from './features/auth/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { ClienteListComponent } from './features/clientes/cliente-list/cliente-list.component';
import { CanchaListComponent } from './features/canchas/cancha-list/cancha-list.component';
import { DisponibilidadComponent } from './features/canchas/disponibilidad/disponibilidad.component';
import { ReservaListComponent } from './features/reservas/reserva-list/reserva-list.component';
import { PagoFormComponent } from './features/pagos/pago-form/pago-form.component';
import { UsuarioListComponent } from './features/usuarios/usuario-list/usuario-list.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'clientes', component: ClienteListComponent, canActivate: [AuthGuard] },
  { path: 'canchas', component: CanchaListComponent, canActivate: [AuthGuard] },
  { path: 'canchas/disponibilidad', component: DisponibilidadComponent, canActivate: [AuthGuard] },
  { path: 'reservas', component: ReservaListComponent, canActivate: [AuthGuard] },
  { path: 'pagos', component: PagoFormComponent, canActivate: [AuthGuard] },
  { path: 'usuarios', component: UsuarioListComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] } },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
