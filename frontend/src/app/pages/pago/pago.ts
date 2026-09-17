import {
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
  inject
} from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  ActivatedRoute,
  Router,
  RouterLink
} from '@angular/router';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  PagoComprobanteRequest,
  PagoResponse
} from '../../core/models/pago.model';

import {
  ReservaResponse
} from '../../core/models/reserva.model';

import {
  MetodoPago
} from '../../core/models/enums';

import {
  PagoService
} from '../../core/services/pago';

import {
  ReservaPendienteService
} from '../../core/services/reserva-pendiente.service';


@Component({
  selector: 'app-pago',

  imports: [
    ReactiveFormsModule,
    RouterLink
  ],

  templateUrl: './pago.html',

  styleUrl: './pago.scss'
})
export class Pago
  implements OnInit, OnDestroy {

  private readonly fb =
    inject(FormBuilder);

  private readonly route =
    inject(ActivatedRoute);

  private readonly router =
    inject(Router);

  private readonly pagoService =
    inject(PagoService);

  private readonly reservaPendienteService =
    inject(ReservaPendienteService);

  private readonly cdr =
    inject(ChangeDetectorRef);


  reserva:
    ReservaResponse | null = null;


  comprobante:
    File | null = null;


  pagoRegistrado:
    PagoResponse | null = null;


  enviando = false;

  expirado = false;

  errorMensaje = '';


  segundosRestantes = 0;


  private intervalo:
    ReturnType<typeof setInterval> | null = null;


  readonly metodosPago:
    MetodoPago[] = [
      'YAPE',
      'PLIN',
      'TRANSFERENCIA'
    ];


  form =
    this.fb.nonNullable.group({

      metodoPago: [
        'YAPE' as MetodoPago,
        [
          Validators.required
        ]
      ],

      numOperacion: [
        '',
        [
          Validators.maxLength(50)
        ]
      ]

    });


  ngOnInit(): void {

    this.cargarReserva();
  }


  ngOnDestroy(): void {

    this.detenerContador();
  }


  private cargarReserva(): void {

    const idReserva =
      Number(
        this.route.snapshot
          .paramMap
          .get('idReserva')
      );


    const reservaGuardada =
      this.reservaPendienteService
        .obtener();


    if (
      !idReserva
      || !reservaGuardada
      || reservaGuardada.idReserva !== idReserva
    ) {

      this.router.navigate([
        '/disponibilidad'
      ]);

      return;
    }


    this.reserva =
      reservaGuardada;


    this.actualizarContador();

    this.iniciarContador();
  }


  private iniciarContador(): void {

    this.detenerContador();


    this.intervalo =
      setInterval(
        () => {

          this.actualizarContador();

          this.cdr.markForCheck();

        },
        1000
      );
  }


  private actualizarContador(): void {

    if (
      !this.reserva?.fechaExpiracion
    ) {

      this.segundosRestantes =
        0;

      this.expirado =
        true;

      this.detenerContador();

      return;
    }


    const expiracion =
      new Date(
        this.reserva.fechaExpiracion
      ).getTime();


    const ahora =
      Date.now();


    const diferencia =
      Math.floor(
        (expiracion - ahora) / 1000
      );


    if (
      diferencia <= 0
    ) {

      this.segundosRestantes =
        0;

      this.expirado =
        true;

      this.detenerContador();

      return;
    }


    this.segundosRestantes =
      diferencia;
  }


  private detenerContador(): void {

    if (this.intervalo) {

      clearInterval(
        this.intervalo
      );

      this.intervalo =
        null;
    }
  }


  obtenerMinutos(): string {

    const minutos =
      Math.floor(
        this.segundosRestantes / 60
      );


    return String(
      minutos
    ).padStart(
      2,
      '0'
    );
  }


  obtenerSegundos(): string {

    const segundos =
      this.segundosRestantes % 60;


    return String(
      segundos
    ).padStart(
      2,
      '0'
    );
  }


  seleccionarArchivo(
    event: Event
  ): void {

    const input =
      event.target as HTMLInputElement;


    const archivo =
      input.files?.[0]
      ?? null;


    this.comprobante =
      archivo;

    this.errorMensaje =
      '';
  }


  enviarComprobante(): void {

    if (
      this.expirado
      || !this.reserva
    ) {

      this.errorMensaje =
        'El tiempo para registrar el pago ha expirado.';

      return;
    }


    if (
      this.form.invalid
    ) {

      this.form.markAllAsTouched();

      return;
    }


    if (
      !this.comprobante
    ) {

      this.errorMensaje =
        'Selecciona un comprobante de pago.';

      return;
    }


    const datos =
      this.form.getRawValue();


    const request:
      PagoComprobanteRequest = {

      metodoPago:
        datos.metodoPago as
          'YAPE'
          | 'PLIN'
          | 'TRANSFERENCIA',

      numOperacion:
        datos.numOperacion.trim()
          || null,

      comprobante:
        this.comprobante

    };


    this.enviando =
      true;

    this.errorMensaje =
      '';


    this.pagoService
      .registrarComprobante(
        this.reserva.idReserva,
        request
      )
      .subscribe({

        next: pago => {

          this.enviando =
            false;

          this.pagoRegistrado =
            pago;

          this.detenerContador();


          this.reservaPendienteService
            .eliminar();


          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.enviando =
            false;


          if (
            error.error?.message
          ) {

            this.errorMensaje =
              error.error.message;

          } else {

            this.errorMensaje =
              'No se pudo registrar el comprobante.';
          }


          /*
           * Si Spring indica conflicto porque
           * venció la reserva, dejamos de permitir
           * el envío.
           */
          if (
            error.status === 409
            || error.status === 400
          ) {

            this.actualizarContador();
          }


          this.cdr.markForCheck();
        }

      });

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