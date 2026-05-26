export interface Cliente {
  id: number;
  nombre: string;
  telefono?: string;
  email: string;
  documento: string;
  direccion?: string;
  fechaRegistro: string;
  activo: boolean;
}
