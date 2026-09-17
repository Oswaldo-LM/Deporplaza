import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import {
  HorarioRequest,
  HorarioResponse
} from '../models/horario.model';


@Injectable({
  providedIn: 'root'
})
export class HorarioService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    `${environment.apiUrl}/horarios`;


  listar(): Observable<HorarioResponse[]> {

    return this.http.get<HorarioResponse[]>(
      this.apiUrl
    );
  }


  obtenerPorId(
    id: number
  ): Observable<HorarioResponse> {

    return this.http.get<HorarioResponse>(
      `${this.apiUrl}/${id}`
    );
  }


  listarPorCancha(
    idCancha: number
  ): Observable<HorarioResponse[]> {

    return this.http.get<HorarioResponse[]>(
      `${this.apiUrl}/cancha/${idCancha}`
    );
  }


  crear(
    request: HorarioRequest
  ): Observable<HorarioResponse> {

    return this.http.post<HorarioResponse>(
      this.apiUrl,
      request
    );
  }


  actualizar(
    id: number,
    request: HorarioRequest
  ): Observable<HorarioResponse> {

    return this.http.put<HorarioResponse>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

}