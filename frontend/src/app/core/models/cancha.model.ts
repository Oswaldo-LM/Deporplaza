import {
  EstadoCancha,
  SuperficieCancha
} from './enums';


export interface CanchaRequest {

  idSede: number;

  nombre: string;

  superficie: SuperficieCancha;

  precioHora: number;

  estado: EstadoCancha;

}


export interface CanchaResponse {

  idCancha: number;

  idSede: number;

  nombreSede: string;

  nombre: string;

  superficie: SuperficieCancha;

  precioHora: number;

  estado: EstadoCancha;

}