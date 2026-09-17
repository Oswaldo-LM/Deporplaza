import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import {
  ReservaPresencialRequest,
  ReservaResponse,
  ReservaWebRequest
} from '../models/reserva.model';

import {
  ReservaAdminFiltros,
  ReservaAdminResponse
} from '../models/reserva-admin.model';



@Injectable({
  providedIn: 'root'
})
export class ReservaService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    `${environment.apiUrl}/reservas`;


  crearWeb(
    request: ReservaWebRequest
  ): Observable<ReservaResponse> {

    return this.http.post<ReservaResponse>(
      `${this.apiUrl}/web`,
      request
    );
  }


  crearPresencial(
    request: ReservaPresencialRequest
  ): Observable<ReservaResponse> {

    return this.http.post<ReservaResponse>(
      `${environment.apiUrl}/admin/reservas/presencial`,
      request
    );
  }

  listarAdmin(
  filtros: ReservaAdminFiltros = {}
): Observable<ReservaAdminResponse[]> {

  let params =
    new HttpParams();


  if (filtros.estado) {

    params =
      params.set(
        'estado',
        filtros.estado
      );
  }


  if (filtros.origen) {

    params =
      params.set(
        'origen',
        filtros.origen
      );
  }


  if (filtros.fecha) {

    params =
      params.set(
        'fecha',
        filtros.fecha
      );
  }


  if (filtros.sedeId) {

    params =
      params.set(
        'sedeId',
        filtros.sedeId.toString()
      );
  }


  if (filtros.canchaId) {

    params =
      params.set(
        'canchaId',
        filtros.canchaId.toString()
      );
  }


  return this.http.get<ReservaAdminResponse[]>(
    `${environment.apiUrl}/admin/reservas`,
    {
      params
    }
  );
}


obtenerAdminPorId(
  idReserva: number
): Observable<ReservaAdminResponse> {

  return this.http.get<ReservaAdminResponse>(
    `${environment.apiUrl}/admin/reservas/${idReserva}`
  );
}


cancelarAdmin(
  idReserva: number
): Observable<ReservaAdminResponse> {

  return this.http.patch<ReservaAdminResponse>(
    `${environment.apiUrl}/admin/reservas/${idReserva}/cancelar`,
    null
  );
}

}