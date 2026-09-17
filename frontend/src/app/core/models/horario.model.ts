import {
  DiaSemana,
  EstadoHorario
} from './enums';


export interface HorarioRequest {

  idCancha: number;

  diaSemana: DiaSemana;

  horaApertura: string;

  horaCierre: string;

  estado: EstadoHorario;

}


export interface HorarioResponse {

  idHorarioCancha: number;

  idCancha: number;

  nombreCancha: string;

  diaSemana: DiaSemana;

  horaApertura: string;

  horaCierre: string;

  estado: EstadoHorario;

}