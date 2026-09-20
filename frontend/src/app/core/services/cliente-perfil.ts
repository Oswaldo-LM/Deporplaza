import {
  Injectable,
  inject
} from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  Observable
} from 'rxjs';

import {
  environment
} from '../../../environments/environment';

import {
  ClientePerfil
} from '../models/cliente-perfil.model';


@Injectable({
  providedIn: 'root'
})
export class ClientePerfilService {

  private readonly http =
    inject(HttpClient);


  private readonly apiUrl =
    `${environment.apiUrl}/cliente`;


  obtenerPerfil():
    Observable<ClientePerfil> {

    return this.http
      .get<ClientePerfil>(
        `${this.apiUrl}/perfil`
      );
  }

}