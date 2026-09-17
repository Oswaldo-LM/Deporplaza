import {
  Injectable
} from '@angular/core';

import {
  ReservaResponse
} from '../models/reserva.model';


@Injectable({
  providedIn: 'root'
})
export class ReservaPendienteService {

  private readonly KEY =
    'deporplaza_reserva_pendiente';


  guardar(
    reserva: ReservaResponse
  ): void {

    sessionStorage.setItem(
      this.KEY,
      JSON.stringify(reserva)
    );
  }


  obtener():
    ReservaResponse | null {

    const raw =
      sessionStorage.getItem(
        this.KEY
      );


    if (!raw) {
      return null;
    }


    try {

      return JSON.parse(
        raw
      ) as ReservaResponse;

    } catch {

      this.eliminar();

      return null;
    }
  }


  eliminar(): void {

    sessionStorage.removeItem(
      this.KEY
    );
  }

}