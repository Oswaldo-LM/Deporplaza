import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import {
  PagoAdminResponse,
  PagoComprobanteRequest,
  PagoResponse
} from '../models/pago.model';

import {
  ReservaResponse
} from '../models/reserva.model';

@Injectable({
  providedIn: 'root'
})
export class PagoService {

  private readonly http = inject(HttpClient);


  registrarComprobante(
    idReserva: number,
    request: PagoComprobanteRequest
  ): Observable<PagoResponse> {

    const formData =
      new FormData();


    formData.append(
      'metodoPago',
      request.metodoPago
    );


    if (request.numOperacion) {

      formData.append(
        'numOperacion',
        request.numOperacion
      );
    }


    formData.append(
      'comprobante',
      request.comprobante
    );


    return this.http.post<PagoResponse>(
      `${environment.apiUrl}/reservas/${idReserva}/pago`,
      formData
    );
  }

  obtenerReservaCliente(
  idReserva: number
): Observable<ReservaResponse> {

  return this.http.get<ReservaResponse>(
    `${environment.apiUrl}/cliente/mis-reservas/${idReserva}`
  );
}


  listarPendientes():
    Observable<PagoAdminResponse[]> {

    return this.http.get<PagoAdminResponse[]>(
      `${environment.apiUrl}/admin/pagos/pendientes`
    );
  }


  obtenerComprobante(
    idPago: number
  ): Observable<Blob> {

    return this.http.get(
      `${environment.apiUrl}/admin/pagos/${idPago}/comprobante`,
      {
        responseType: 'blob'
      }
    );
  }


  aprobar(
    idPago: number
  ): Observable<PagoAdminResponse> {

    return this.http.patch<PagoAdminResponse>(
      `${environment.apiUrl}/admin/pagos/${idPago}/aprobar`,
      null
    );
  }


  rechazar(
    idPago: number
  ): Observable<PagoAdminResponse> {

    return this.http.patch<PagoAdminResponse>(
      `${environment.apiUrl}/admin/pagos/${idPago}/rechazar`,
      null
    );
  }

}