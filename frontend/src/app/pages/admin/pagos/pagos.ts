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
  PagoAdminResponse
} from '../../../core/models/pago.model';

import {
  PagoService
} from '../../../core/services/pago';


@Component({
  selector: 'app-pagos',

  imports: [],

  templateUrl: './pagos.html',

  styleUrl: './pagos.scss'
})
export class Pagos
  implements OnInit {

  private readonly pagoService =
    inject(PagoService);

  private readonly cdr =
    inject(ChangeDetectorRef);


  pagos:
    PagoAdminResponse[] = [];


  cargando = false;

  procesandoId:
    number | null = null;


  errorMensaje = '';

  exitoMensaje = '';


  ngOnInit(): void {

    this.cargarPagos();
  }


  cargarPagos(): void {

    this.cargando = true;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    this.pagoService
      .listarPendientes()
      .subscribe({

        next: pagos => {

          this.pagos =
            pagos;

          this.cargando =
            false;

          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.cargando =
            false;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudieron cargar los pagos pendientes.';

          this.cdr.markForCheck();
        }

      });
  }


  verComprobante(
    pago: PagoAdminResponse
  ): void {

    this.errorMensaje = '';


    this.pagoService
      .obtenerComprobante(
        pago.idPago
      )
      .subscribe({

        next: blob => {

          const url =
            URL.createObjectURL(
              blob
            );


          const enlace =
            document.createElement(
              'a'
            );

          enlace.href =
            url;

          enlace.target =
            '_blank';

          enlace.rel =
            'noopener noreferrer';


          document.body.appendChild(
            enlace
          );

          enlace.click();

          enlace.remove();


          /*
           * Dejamos unos segundos antes de
           * liberar la URL temporal para que
           * el navegador pueda abrirla.
           */
          setTimeout(
            () => {

              URL.revokeObjectURL(
                url
              );

            },
            60000
          );

        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo abrir el comprobante.';

          this.cdr.markForCheck();
        }

      });
  }


  aprobar(
    pago: PagoAdminResponse
  ): void {

    const confirmar =
      window.confirm(
        `¿Deseas aprobar el pago de ${pago.nombreCliente} por S/ ${pago.monto}?`
      );


    if (!confirmar) {
      return;
    }


    this.procesandoId =
      pago.idPago;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    this.pagoService
      .aprobar(
        pago.idPago
      )
      .subscribe({

        next: respuesta => {

          this.procesandoId =
            null;


          /*
           * Ya no está PENDIENTE_VALIDACION,
           * por lo tanto lo retiramos de
           * esta tabla.
           */
          this.pagos =
            this.pagos.filter(
              item =>
                item.idPago
                !== pago.idPago
            );


          this.exitoMensaje =
            `Pago #${respuesta.idPago} aprobado correctamente.`;


          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.procesandoId =
            null;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo aprobar el pago.';

          this.cdr.markForCheck();
        }

      });
  }


  rechazar(
    pago: PagoAdminResponse
  ): void {

    const confirmar =
      window.confirm(
        `¿Deseas rechazar el pago de ${pago.nombreCliente}? La reserva será cancelada.`
      );


    if (!confirmar) {
      return;
    }


    this.procesandoId =
      pago.idPago;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    this.pagoService
      .rechazar(
        pago.idPago
      )
      .subscribe({

        next: respuesta => {

          this.procesandoId =
            null;


          this.pagos =
            this.pagos.filter(
              item =>
                item.idPago
                !== pago.idPago
            );


          this.exitoMensaje =
            `Pago #${respuesta.idPago} rechazado. La reserva fue cancelada.`;


          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.procesandoId =
            null;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo rechazar el pago.';

          this.cdr.markForCheck();
        }

      });
  }


  formatearFecha(
    fecha: string | null
  ): string {

    if (!fecha) {
      return '-';
    }


    const date =
      new Date(fecha);


    if (
      Number.isNaN(
        date.getTime()
      )
    ) {

      return fecha;
    }


    return date.toLocaleString(
      'es-PE'
    );
  }

}