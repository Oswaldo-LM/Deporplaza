import {
  TipoDocumento
} from './enums';


export interface ClientePerfil {

  idCliente: number;

  nombreCompleto: string;

  tipoDocumento: TipoDocumento;

  numDocumento: string;

  email: string;

  telefono: string;

}