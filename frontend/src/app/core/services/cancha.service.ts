import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Cancha, EstadoCancha } from '../models/cancha.model';

@Injectable({ providedIn: 'root' })
export class CanchaService {
  private api = `${environment.apiUrl}/canchas`;
  constructor(private http: HttpClient) {}
  listar() { return this.http.get<Cancha[]>(this.api); }
  crear(data: Partial<Cancha>) { return this.http.post<Cancha>(this.api, data); }
  actualizar(id: number, data: Partial<Cancha>) { return this.http.put<Cancha>(`${this.api}/${id}`, data); }
  cambiarEstado(id: number, estado: EstadoCancha) { return this.http.patch<Cancha>(`${this.api}/${id}/estado?estado=${estado}`, {}); }
}
