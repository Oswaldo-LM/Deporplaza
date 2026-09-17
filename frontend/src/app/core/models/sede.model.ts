import { EstadoSede } from './enums';


export interface SedeRequest {

  nombre: string;

  direccion: string;

  telefono: string | null;

  estado: EstadoSede;

}


export interface SedeResponse {

  idSede: number;

  nombre: string;

  direccion: string;

  telefono: string | null;

  estado: EstadoSede;

}