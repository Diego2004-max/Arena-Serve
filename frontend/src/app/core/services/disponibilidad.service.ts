import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Disponibilidad } from '../models/cancha.model';

@Injectable({ providedIn: 'root' })
export class DisponibilidadService {
  constructor(private http: HttpClient) {}
  consultar(canchaId: number, fecha: string) {
    return this.http.get<Disponibilidad[]>(`${environment.apiUrl}/disponibilidad?canchaId=${canchaId}&fecha=${fecha}`);
  }
}
