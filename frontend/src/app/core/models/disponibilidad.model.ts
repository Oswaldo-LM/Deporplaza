import {
  DiaSemana,
  SuperficieCancha
} from './enums';


export interface BloqueDisponibilidad {

  horaInicio: string;

  disponible: boolean;

  maxExtrasDisponibles: number | null;

  duracionMaximaMinutos: number | null;

}


export interface CanchaDisponibilidad {

  idCancha: number;

  nombre: string;

  superficie: SuperficieCancha;

  precioHora: number;

  tieneHorario: boolean;

  horaApertura: string | null;

  horaCierre: string | null;

  bloques: BloqueDisponibilidad[];

}


export interface DisponibilidadResponse {

  idSede: number;

  nombreSede: string;

  fecha: string;

  diaSemana: DiaSemana;

  canchas: CanchaDisponibilidad[];

}