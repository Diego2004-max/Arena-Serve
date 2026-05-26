import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Pago } from '../models/pago.model';

@Injectable({ providedIn: 'root' })
export class PagoService {
  private api = `${environment.apiUrl}/pagos`;
  constructor(private http: HttpClient) {}
  registrar(data: { reservaId: number; valor: number; tipoPago: string; referencia?: string }) {
    return this.http.post<Pago>(this.api, data);
  }
  porReserva(reservaId: number) { return this.http.get<Pago[]>(`${this.api}/reserva/${reservaId}`); }
}
