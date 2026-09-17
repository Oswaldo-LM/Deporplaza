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
  MiReserva
} from '../models/mis-reservas.model';


@Injectable({
  providedIn: 'root'
})
export class ClienteReservaService {

  private readonly http =
    inject(HttpClient);


  private readonly apiUrl =
    `${environment.apiUrl}/cliente`;


  obtenerMisReservas():
    Observable<MiReserva[]> {

    return this.http
      .get<MiReserva[]>(
        `${this.apiUrl}/mis-reservas`
      );
  }

}