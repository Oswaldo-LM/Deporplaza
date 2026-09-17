import {
  EstadoReserva,
  OrigenReserva
} from './enums';


export interface MiReserva {

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

  fechaExpiracion:
    string | null;

}