export type TipoCancha = 'FUTBOL_5' | 'FUTBOL_7' | 'FUTBOL_11';
export type EstadoCancha = 'ACTIVA' | 'INACTIVA' | 'MANTENIMIENTO';

export interface Cancha {
  id: number;
  nombre: string;
  tipoCancha: TipoCancha;
  capacidad: number;
  precioHora: number;
  estadoCancha: EstadoCancha;
}

export interface Disponibilidad {
  horaInicio: string;
  horaFin: string;
  disponible: boolean;
}
