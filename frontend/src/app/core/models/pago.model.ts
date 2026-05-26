export type TipoPago = 'EFECTIVO' | 'TRANSFERENCIA' | 'TARJETA';

export interface Pago {
  id: number;
  reservaId: number;
  valor: number;
  tipoPago: TipoPago;
  fechaPago: string;
  referencia?: string;
}
