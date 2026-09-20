import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  RouterLink
} from '@angular/router';

import {
  ClienteReservaService
} from '../../core/services/cliente-reserva.service';

import {
  MiReserva
} from '../../core/models/mis-reservas.model';


@Component({
  selector: 'app-cliente-mis-reservas',

  imports: [
    RouterLink
  ],

  templateUrl: './cliente-mis-reservas.html',

  styleUrl: './cliente-mis-reservas.scss'
})
export class ClienteMisReservas
  implements OnInit {

  private readonly clienteReservaService =
    inject(ClienteReservaService);

  private readonly cdr =
    inject(ChangeDetectorRef);


  reservas: MiReserva[] = [];

  cargando =
    true;

  errorMensaje =
    '';


  ngOnInit(): void {

    this.cargarReservas();
  }


  // =========================================================
  // CARGAR RESERVAS
  // =========================================================

  cargarReservas(): void {

    this.cargando =
      true;

    this.errorMensaje =
      '';


    this.clienteReservaService
      .obtenerMisReservas()
      .subscribe({

        next: reservas => {

          this.reservas =
            reservas;

          this.cargando =
            false;

          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.cargando =
            false;


          if (
            error.status === 403
          ) {

            this.errorMensaje =
              'No tienes permisos para consultar estas reservas.';

          } else {

            this.errorMensaje =
              error.error?.message
              ?? 'No se pudieron cargar tus reservas.';
          }


          this.cdr.markForCheck();
        }

      });
  }


  // =========================================================
  // TEXTO DEL ESTADO
  // =========================================================

  estadoTexto(
    estado: string
  ): string {

    switch (
      estado
    ) {

      case 'PENDIENTE_PAGO':
        return 'Pendiente de pago';

      case 'PENDIENTE_CONFIRMACION':
        return 'Pago en validación';

      case 'CONFIRMADA':
        return 'Confirmada';

      case 'COMPLETADA':
        return 'Completada';

      case 'CANCELADA':
        return 'Cancelada';

      case 'EXPIRADA':
        return 'Expirada';

      default:
        return estado;
    }
  }


  // =========================================================
  // CLASE DEL BADGE
  // =========================================================

  estadoClase(
    estado: string
  ): string {

    switch (
      estado
    ) {

      case 'CONFIRMADA':
        return 'text-bg-success';

      case 'COMPLETADA':
        return 'text-bg-primary';

      case 'PENDIENTE_PAGO':
        return 'text-bg-warning';

      case 'PENDIENTE_CONFIRMACION':
        return 'text-bg-info';

      case 'CANCELADA':
        return 'text-bg-danger';

      case 'EXPIRADA':
        return 'text-bg-secondary';

      default:
        return 'text-bg-light';
    }
  }


  // =========================================================
  // DURACIÓN
  // =========================================================

  formatearDuracion(
    minutos: number
  ): string {

    const horas =
      Math.floor(
        minutos / 60
      );


    const minutosRestantes =
      minutos % 60;


    if (
      minutosRestantes === 0
    ) {

      return horas === 1
        ? '1 hora'
        : `${horas} horas`;
    }


    if (
      horas === 0
    ) {

      return `${minutosRestantes} min`;
    }


    return `${horas} h ${minutosRestantes} min`;
  }


  // =========================================================
  // HORA
  // =========================================================

  formatearHora(
    hora: string
  ): string {

    if (
      !hora
    ) {

      return '';
    }


    return hora.substring(
      0,
      5
    );
  }


  // =========================================================
  // FECHA
  // =========================================================

  formatearFecha(
    fecha: string
  ): string {

    if (
      !fecha
    ) {

      return '';
    }


    const partes =
      fecha.split('-');


    if (
      partes.length !== 3
    ) {

      return fecha;
    }


    return `${partes[2]}/${partes[1]}/${partes[0]}`;
  }


  // =========================================================
  // MONTO
  // =========================================================

  formatearMonto(
    monto: number
  ): string {

    return new Intl.NumberFormat(
      'es-PE',
      {
        style: 'currency',
        currency: 'PEN'
      }
    ).format(
      monto
    );
  }

}