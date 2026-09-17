import {
  Injectable,
  inject
} from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  Observable,
  shareReplay,
  tap
} from 'rxjs';

import {
  environment
} from '../../../environments/environment';

import {
  SedeRequest,
  SedeResponse
} from '../models/sede.model';


@Injectable({
  providedIn: 'root'
})
export class SedeService {

  private readonly http =
    inject(HttpClient);

  private readonly apiUrl =
    `${environment.apiUrl}/sedes`;


  private sedesActivasCache$:
    Observable<SedeResponse[]> | null = null;


  listar():
    Observable<SedeResponse[]> {

    return this.http.get<SedeResponse[]>(
      this.apiUrl
    );
  }


  listarActivas():
    Observable<SedeResponse[]> {

    if (!this.sedesActivasCache$) {

      this.sedesActivasCache$ =
        this.http
          .get<SedeResponse[]>(
            `${this.apiUrl}/activas`
          )
          .pipe(

            shareReplay({
              bufferSize: 1,
              refCount: false
            })

          );
    }


    return this.sedesActivasCache$;
  }


  obtenerPorId(
    id: number
  ): Observable<SedeResponse> {

    return this.http.get<SedeResponse>(
      `${this.apiUrl}/${id}`
    );
  }


  crear(
    request: SedeRequest
  ): Observable<SedeResponse> {

    return this.http
      .post<SedeResponse>(
        this.apiUrl,
        request
      )
      .pipe(

        tap(() =>
          this.invalidarCache()
        )

      );
  }


  actualizar(
    id: number,
    request: SedeRequest
  ): Observable<SedeResponse> {

    return this.http
      .put<SedeResponse>(
        `${this.apiUrl}/${id}`,
        request
      )
      .pipe(

        tap(() =>
          this.invalidarCache()
        )

      );
  }


  private invalidarCache(): void {

    this.sedesActivasCache$ =
      null;
  }

}