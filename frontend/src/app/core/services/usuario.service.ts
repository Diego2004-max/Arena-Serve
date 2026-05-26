import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { UsuarioSistema, Rol } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private api = `${environment.apiUrl}/usuarios`;
  constructor(private http: HttpClient) {}
  listar() { return this.http.get<UsuarioSistema[]>(this.api); }
  crear(data: { email: string; password: string; rol: Rol }) { return this.http.post<UsuarioSistema>(this.api, data); }
  actualizar(id: number, data: { email: string; password: string; rol: Rol }) { return this.http.put<UsuarioSistema>(`${this.api}/${id}`, data); }
  cambiarEstado(id: number) { return this.http.patch<UsuarioSistema>(`${this.api}/${id}/estado`, {}); }
}
