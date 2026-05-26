import { Reserva } from './reserva.model';

export interface Dashboard {
  reservasHoy: number;
  reservasPendientes: number;
  reservasConfirmadas: number;
  ingresosDelDia: number;
  proximasReservas: Reserva[];
}
