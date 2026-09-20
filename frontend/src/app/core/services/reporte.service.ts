import {
  Injectable,
  inject
} from '@angular/core';

import {
  HttpClient,
  HttpParams,
  HttpResponse
} from '@angular/common/http';

import {
  Observable
} from 'rxjs';

import {
  environment
} from '../../../environments/environment';

import {
  ReporteReserva,
  ReporteReservaFiltros
} from '../models/reporte.model';


@Injectable({
  providedIn: 'root'
})
export class ReporteService {

  private readonly http =
    inject(HttpClient);


  private readonly apiUrl =
    `${environment.apiUrl}/admin/reportes/reservas`;


  obtenerReservas(
    filtros: ReporteReservaFiltros
  ): Observable<ReporteReserva[]> {

    const params =
      this.crearParametros(
        filtros
      );


    return this.http.get<ReporteReserva[]>(
      this.apiUrl,
      {
        params
      }
    );
  }


  exportarReservas(
    filtros: ReporteReservaFiltros
  ): Observable<HttpResponse<Blob>> {

    const params =
      this.crearParametros(
        filtros
      );


    return this.http.get(
      `${this.apiUrl}/exportar`,
      {
        params,
        observe: 'response',
        responseType: 'blob'
      }
    );
  }


  private crearParametros(
    filtros: ReporteReservaFiltros
  ): HttpParams {

    let params =
      new HttpParams();


    if (filtros.desde) {

      params =
        params.set(
          'desde',
          filtros.desde
        );
    }


    if (filtros.hasta) {

      params =
        params.set(
          'hasta',
          filtros.hasta
        );
    }


    if (
      filtros.idSede
      !== undefined
    ) {

      params =
        params.set(
          'idSede',
          filtros.idSede.toString()
        );
    }


    if (filtros.estado) {

      params =
        params.set(
          'estado',
          filtros.estado
        );
    }


    return params;
  }

}