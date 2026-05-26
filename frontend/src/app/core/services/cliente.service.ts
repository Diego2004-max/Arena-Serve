import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Cliente } from '../models/cliente.model';

@Injectable({ providedIn: 'root' })
export class ClienteService {
  private api = `${environment.apiUrl}/clientes`;
  constructor(private http: HttpClient) {}
  listar() { return this.http.get<Cliente[]>(this.api); }
  obtener(id: number) { return this.http.get<Cliente>(`${this.api}/${id}`); }
  crear(data: Partial<Cliente>) { return this.http.post<Cliente>(this.api, data); }
  actualizar(id: number, data: Partial<Cliente>) { return this.http.put<Cliente>(`${this.api}/${id}`, data); }
  eliminar(id: number) { return this.http.delete<void>(`${this.api}/${id}`); }
  cambiarEstado(id: number, activo: boolean) { return this.http.patch<Cliente>(`${this.api}/${id}/estado?activo=${activo}`, {}); }
}
