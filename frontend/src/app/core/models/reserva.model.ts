export type EstadoReserva = 'PENDIENTE' | 'CONFIRMADA' | 'CANCELADA' | 'FINALIZADA';

export interface Reserva {
  id: number;
  clienteId: number;
  clienteNombre: string;
  canchaId: number;
  canchaNombre: string;
  fecha: string;
  horaInicio: string;
  horaFin: string;
  estadoReserva: EstadoReserva;
  total: number;
  fechaCreacion: string;
  fechaCancelacion?: string;
  serviciosAdicionales: string[];
}
