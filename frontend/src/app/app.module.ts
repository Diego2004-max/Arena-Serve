import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule, provideHttpClient, withInterceptors } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './features/auth/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { ClienteListComponent } from './features/clientes/cliente-list/cliente-list.component';
import { ClienteFormComponent } from './features/clientes/cliente-form/cliente-form.component';
import { CanchaListComponent } from './features/canchas/cancha-list/cancha-list.component';
import { CanchaFormComponent } from './features/canchas/cancha-form/cancha-form.component';
import { DisponibilidadComponent } from './features/canchas/disponibilidad/disponibilidad.component';
import { ReservaListComponent } from './features/reservas/reserva-list/reserva-list.component';
import { ReservaFormComponent } from './features/reservas/reserva-form/reserva-form.component';
import { ReservaDetailComponent } from './features/reservas/reserva-detail/reserva-detail.component';
import { PagoFormComponent } from './features/pagos/pago-form/pago-form.component';
import { UsuarioListComponent } from './features/usuarios/usuario-list/usuario-list.component';
import { UsuarioFormComponent } from './features/usuarios/usuario-form/usuario-form.component';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { SidebarComponent } from './shared/components/sidebar/sidebar.component';
import { ConfirmDialogComponent } from './shared/components/confirm-dialog/confirm-dialog.component';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';

@NgModule({
  declarations: [
    AppComponent, LoginComponent, DashboardComponent, ClienteListComponent, ClienteFormComponent,
    CanchaListComponent, CanchaFormComponent, DisponibilidadComponent, ReservaListComponent, ReservaFormComponent,
    ReservaDetailComponent, PagoFormComponent, UsuarioListComponent, UsuarioFormComponent, NavbarComponent,
    SidebarComponent, ConfirmDialogComponent
  ],
  imports: [BrowserModule, ReactiveFormsModule, HttpClientModule, AppRoutingModule],
  providers: [provideHttpClient(withInterceptors([jwtInterceptor]))],
  bootstrap: [AppComponent]
})
export class AppModule {}
