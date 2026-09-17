import { RolUsuario } from './enums';


export interface LoginRequest {

  email: string;

  password: string;

}


export interface LoginResponse {

  accessToken: string;

  tokenType: string;

  idUsuario: number;

  nombre: string;

  email: string;

  rol: RolUsuario;

}


export interface ClienteRegistroRequest {

  nombreCompleto: string;

  tipoDocumento:
    'DNI'
    | 'CE'
    | 'PASAPORTE';

  numDocumento: string;

  email: string;

  telefono: string;

  password: string;

}