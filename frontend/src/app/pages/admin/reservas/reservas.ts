import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule
} from '@angular/forms';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  ReservaAdminResponse
} from '../../../core/models/reserva-admin.model';

import {
  CanchaResponse
} from '../../../core/models/cancha.model';

import {
  SedeResponse
} from '../../../core/models/sede.model';

import {
  EstadoReserva,
  OrigenReserva
} from '../../../core/models/enums';

import {
  ReservaService
} from '../../../core/services/reserva';

import {
  SedeService
} from '../../../core/services/sede';

import {
  CanchaService
} from '../../../core/services/cancha';


@Component({
  selector: 'app-reservas',

  imports: [
    ReactiveFormsModule
  ],

  templateUrl: './reservas.html',

  styleUrl: './reservas.scss'
})
export class Reservas
  implements OnInit {

  private readonly fb =
    inject(FormBuilder);

  private readonly reservaService =
    inject(ReservaService);

  private readonly sedeService =
    inject(SedeService);

  private readonly canchaService =
    inject(CanchaService);

  private readonly cdr =
    inject(ChangeDetectorRef);


  reservas:
    ReservaAdminResponse[] = [];


  sedes:
    SedeResponse[] = [];


  canchas:
    CanchaResponse[] = [];


  reservaSeleccionada:
    ReservaAdminResponse | null = null;


  cargando = false;

  cargandoDetalle = false;

  procesandoId:
    number | null = null;


  errorMensaje = '';

  exitoMensaje = '';


  readonly estados:
    EstadoReserva[] = [

      'PENDIENTE_PAGO',
      'PENDIENTE_CONFIRMACION',
      'CONFIRMADA',
      'CANCELADA',
      'EXPIRADA',
      'COMPLETADA'

    ];


  readonly origenes:
    OrigenReserva[] = [
      'WEB',
      'PRESENCIAL'
    ];


  form =
    this.fb.nonNullable.group({

      estado: [
        ''
      ],

      origen: [
        ''
      ],

      fecha: [
        ''
      ],

      sedeId: [
        0
      ],

      canchaId: [
        0
      ]

    });


  ngOnInit(): void {

    this.cargarSedes();

    this.cargarReservas();


    this.form.controls
      .sedeId
      .valueChanges
      .subscribe(
        idSede => {

          this.form.controls
            .canchaId
            .setValue(
              0,
              {
                emitEvent: false
              }
            );


          if (
            idSede > 0
          ) {

            this.cargarCanchas(
              idSede
            );

          } else {

            this.canchas = [];

          }

        }
      );
  }


  cargarSedes(): void {

    this.sedeService
      .listar()
      .subscribe({

        next: sedes => {

          this.sedes =
            sedes;

          this.cdr.markForCheck();
        },


        error: () => {

          this.errorMensaje =
            'No se pudieron cargar las sedes.';

          this.cdr.markForCheck();
        }

      });
  }


  cargarCanchas(
    idSede: number
  ): void {

    this.canchaService
      .listarPorSede(
        idSede
      )
      .subscribe({

        next: canchas => {

          this.canchas =
            canchas;

          this.cdr.markForCheck();
        },


        error: () => {

          this.canchas = [];

          this.errorMensaje =
            'No se pudieron cargar las canchas.';

          this.cdr.markForCheck();
        }

      });
  }


  cargarReservas(): void {

    this.cargando = true;

    this.errorMensaje = '';

    this.exitoMensaje = '';

    this.reservaSeleccionada =
      null;


    const filtros =
      this.form.getRawValue();


    this.reservaService
      .listarAdmin({

        estado:
          filtros.estado
            ? filtros.estado as EstadoReserva
            : undefined,

        origen:
          filtros.origen
            ? filtros.origen as OrigenReserva
            : undefined,

        fecha:
          filtros.fecha
            || undefined,

        sedeId:
          filtros.sedeId > 0
            ? filtros.sedeId
            : undefined,

        canchaId:
          filtros.canchaId > 0
            ? filtros.canchaId
            : undefined

      })
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

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudieron cargar las reservas.';

          this.cdr.markForCheck();
        }

      });
  }


  limpiarFiltros(): void {

    this.form.reset({

      estado: '',
      origen: '',
      fecha: '',
      sedeId: 0,
      canchaId: 0

    });


    this.canchas = [];


    this.cargarReservas();
  }


  verDetalle(
    reserva: ReservaAdminResponse
  ): void {

    this.cargandoDetalle =
      true;

    this.reservaSeleccionada =
      null;

    this.errorMensaje = '';


    this.reservaService
      .obtenerAdminPorId(
        reserva.idReserva
      )
      .subscribe({

        next: detalle => {

          this.reservaSeleccionada =
            detalle;

          this.cargandoDetalle =
            false;

          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.cargandoDetalle =
            false;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo cargar el detalle de la reserva.';

          this.cdr.markForCheck();
        }

      });
  }


  cerrarDetalle(): void {

    this.reservaSeleccionada =
      null;
  }


  puedeCancelar(
    reserva: ReservaAdminResponse
  ): boolean {

    return [

      'PENDIENTE_PAGO',
      'PENDIENTE_CONFIRMACION',
      'CONFIRMADA'

    ].includes(
      reserva.estado
    );
  }


  cancelar(
    reserva: ReservaAdminResponse
  ): void {

    if (
      !this.puedeCancelar(
        reserva
      )
    ) {

      return;
    }


    const confirmar =
      window.confirm(
        `¿Deseas cancelar la reserva #${reserva.idReserva} de ${reserva.nombreCliente}?`
      );


    if (!confirmar) {
      return;
    }


    this.procesandoId =
      reserva.idReserva;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    this.reservaService
      .cancelarAdmin(
        reserva.idReserva
      )
      .subscribe({

        next: actualizada => {

          this.procesandoId =
            null;


          this.reservas =
            this.reservas.map(
              item => {

                if (
                  item.idReserva
                    === actualizada.idReserva
                ) {

                  return actualizada;
                }


                return item;
              }
            );


          if (
            this.reservaSeleccionada
              ?.idReserva
              === actualizada.idReserva
          ) {

            this.reservaSeleccionada =
              actualizada;
          }


          this.exitoMensaje =
            `Reserva #${actualizada.idReserva} cancelada correctamente.`;


          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.procesandoId =
            null;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo cancelar la reserva.';

          this.cdr.markForCheck();
        }

      });
  }


  claseEstado(
    estado: EstadoReserva
  ): string {

    switch (estado) {

      case 'CONFIRMADA':
        return 'text-bg-success';

      case 'PENDIENTE_PAGO':
        return 'text-bg-warning';

      case 'PENDIENTE_CONFIRMACION':
        return 'text-bg-info';

      case 'CANCELADA':
        return 'text-bg-danger';

      case 'EXPIRADA':
        return 'text-bg-secondary';

      case 'COMPLETADA':
        return 'text-bg-primary';

      default:
        return 'text-bg-light';
    }
  }


  formatearFechaHora(
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


  formatearHora(
    hora: string
  ): string {

    return hora.substring(
      0,
      5
    );
  }

}