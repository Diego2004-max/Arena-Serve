import { Component, OnInit } from '@angular/core';
import { UsuarioSistema } from '../../../core/models/user.model';
import { UsuarioService } from '../../../core/services/usuario.service';

@Component({
  selector: 'app-usuario-list',
  template: `
    <h2>Usuarios</h2>
    <app-usuario-form (saved)="load()"></app-usuario-form>
    <table><tr><th>Email</th><th>Rol</th><th>Estado</th><th></th></tr>
      <tr *ngFor="let u of usuarios"><td>{{u.email}}</td><td>{{u.rol}}</td><td>{{u.activo ? 'Activo' : 'Inactivo'}}</td><td><button (click)="estado(u.id)">Activar/Desactivar</button></td></tr>
    </table>
  `
})
export class UsuarioListComponent implements OnInit {
  usuarios: UsuarioSistema[] = [];
  constructor(private service: UsuarioService) {}
  ngOnInit(): void { this.load(); }
  load(): void { this.service.listar().subscribe(data => this.usuarios = data); }
  estado(id: number): void { this.service.cambiarEstado(id).subscribe(() => this.load()); }
}
