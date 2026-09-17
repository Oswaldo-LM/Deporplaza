import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import {
  CanchaRequest,
  CanchaResponse
} from '../models/cancha.model';


@Injectable({
  providedIn: 'root'
})
export class CanchaService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    `${environment.apiUrl}/canchas`;


  listar(): Observable<CanchaResponse[]> {

    return this.http.get<CanchaResponse[]>(
      this.apiUrl
    );
  }


  obtenerPorId(
    id: number
  ): Observable<CanchaResponse> {

    return this.http.get<CanchaResponse>(
      `${this.apiUrl}/${id}`
    );
  }


  listarPorSede(
    idSede: number
  ): Observable<CanchaResponse[]> {

    return this.http.get<CanchaResponse[]>(
      `${this.apiUrl}/sede/${idSede}`
    );
  }


  crear(
    request: CanchaRequest
  ): Observable<CanchaResponse> {

    return this.http.post<CanchaResponse>(
      this.apiUrl,
      request
    );
  }


  actualizar(
    id: number,
    request: CanchaRequest
  ): Observable<CanchaResponse> {

    return this.http.put<CanchaResponse>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

}