import {
  EstadoPago,
  EstadoReserva,
  MetodoPago
} from './enums';


export interface PagoComprobanteRequest {

  metodoPago:
    | 'YAPE'
    | 'PLIN'
    | 'TRANSFERENCIA';

  numOperacion: string | null;

  comprobante: File;

}


export interface PagoResponse {

  idPago: number;

  idReserva: number;

  metodoPago: MetodoPago;

  monto: number;

  numOperacion: string | null;

  estadoPago: EstadoPago;

  estadoReserva: EstadoReserva;

  fechaPago: string;

}


export interface PagoAdminResponse {

  idPago: number;

  idReserva: number;

  idCliente: number;

  nombreCliente: string;

  metodoPago: MetodoPago;

  monto: number;

  numOperacion: string | null;

  estadoPago: EstadoPago;

  estadoReserva: EstadoReserva;

  fechaPago: string;

  fechaValidacion: string | null;

  idUsuarioValidador: number | null;

  nombreUsuarioValidador: string | null;

  urlComprobante: string;

}