import {
  EstadoPago,
  EstadoReserva,
  MetodoPago,
  OrigenReserva,
  SuperficieCancha,
  TipoDocumento
} from './enums';


export interface ReservaAdminResponse {

  idReserva: number;

  estado: EstadoReserva;

  origen: OrigenReserva;

  fechaRegistro: string;

  fechaExpiracion: string | null;

  total: number;


  idCliente: number;

  nombreCliente: string;

  tipoDocumento: TipoDocumento;

  numDocumento: string;

  emailCliente: string;

  telefonoCliente: string;


  idSede: number;

  nombreSede: string;


  idCancha: number;

  nombreCancha: string;

  superficie: SuperficieCancha;


  fechaTurno: string;

  horaInicio: string;

  horaFin: string;

  cantidadExtras: number;

  precioHora: number;

  subtotal: number;


  idPago: number | null;

  metodoPago: MetodoPago | null;

  estadoPago: EstadoPago | null;

  fechaPago: string | null;

}

export interface ReservaAdminFiltros {

  estado?: EstadoReserva;

  origen?: OrigenReserva;

  fecha?: string;

  sedeId?: number;

  canchaId?: number;

}