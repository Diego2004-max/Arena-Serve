import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Reserva } from '../models/reserva.model';

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private api = `${environment.apiUrl}/reservas`;
  constructor(private http: HttpClient) {}
  listar() { return this.http.get<Reserva[]>(this.api); }
  crear(data: { clienteId: number; canchaId: number; fecha: string; horaInicio: string; horaFin: string; serviciosAdicionalesIds: number[] }) {
    return this.http.post<Reserva>(this.api, data);
  }
  cancelar(id: number) { return this.http.patch<Reserva>(`${this.api}/${id}/cancelar`, {}); }
  finalizar(id: number) { return this.http.patch<Reserva>(`${this.api}/${id}/finalizar`, {}); }
}
