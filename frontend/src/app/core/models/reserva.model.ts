import {
  EstadoReserva,
  MetodoPago,
  OrigenReserva,
  TipoDocumento
} from './enums';


export interface ReservaWebRequest {

  nombreCompleto: string;

  tipoDocumento: TipoDocumento;

  numDocumento: string;

  email: string;

  telefono: string;

  idCancha: number;

  fechaTurno: string;

  horaInicio: string;

  cantidadExtras: number;

}


export interface ReservaPresencialRequest {

  nombreCompleto: string;

  tipoDocumento: TipoDocumento;

  numDocumento: string;

  email: string;

  telefono: string;

  idCancha: number;

  fechaTurno: string;

  horaInicio: string;

  cantidadExtras: number;

  metodoPago: MetodoPago;

  numOperacion: string | null;

}


export interface ReservaResponse {

  idReserva: number;

  idCliente: number;

  nombreCliente: string;

  idSede: number;

  nombreSede: string;

  idCancha: number;

  nombreCancha: string;

  fechaTurno: string;

  horaInicio: string;

  horaFin: string;

  cantidadExtras: number;

  duracionMinutos: number;

  precioHora: number;

  total: number;

  estado: EstadoReserva;

  origen: OrigenReserva;

  fechaRegistro: string;

  fechaExpiracion: string | null;

}