import {
  ChangeDetectorRef,
  Component,
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
  CanchaResponse
} from '../../core/models/cancha.model';

import {
  ReservaResponse,
  ReservaWebRequest
} from '../../core/models/reserva.model';

import {
  TipoDocumento
} from '../../core/models/enums';

import {
  CanchaService
} from '../../core/services/cancha';

import {
  ReservaService
} from '../../core/services/reserva';


@Component({
  selector: 'app-reserva',

  imports: [
    ReactiveFormsModule,
    RouterLink
  ],

  templateUrl: './reserva.html',

  styleUrl: './reserva.scss'
})
export class Reserva
  implements OnInit {

  private readonly fb =
    inject(FormBuilder);

  private readonly route =
    inject(ActivatedRoute);

  private readonly router =
    inject(Router);

  private readonly canchaService =
    inject(CanchaService);

  private readonly reservaService =
    inject(ReservaService);

  private readonly cdRef =
    inject(ChangeDetectorRef);


  cancha:
    CanchaResponse | null = null;


  fecha = '';

  horaInicio = '';

  maxExtras = 0;


  cargando = true;

  guardando = false;

  errorMensaje = '';


  reservaCreada:
    ReservaResponse | null = null;


  readonly tiposDocumento:
    TipoDocumento[] = [
      'DNI',
      'CE',
      'PASAPORTE'
    ];


  form =
    this.fb.nonNullable.group({

      nombreCompleto: [
        '',
        [
          Validators.required,
          Validators.maxLength(150)
        ]
      ],

      tipoDocumento: [
        'DNI' as TipoDocumento,
        [
          Validators.required
        ]
      ],

      numDocumento: [
        '',
        [
          Validators.required,
          Validators.maxLength(20)
        ]
      ],

      email: [
        '',
        [
          Validators.required,
          Validators.email,
          Validators.maxLength(150)
        ]
      ],

      telefono: [
        '',
        [
          Validators.required,
          Validators.maxLength(20)
        ]
      ],

      cantidadExtras: [
        0,
        [
          Validators.required,
          Validators.min(0),
          Validators.max(4)
        ]
      ]

    });


  ngOnInit(): void {

    this.cargarSeleccion();
  }


  private cargarSeleccion(): void {

    const idCancha =
      Number(
        this.route.snapshot
          .queryParamMap
          .get('idCancha')
      );


    this.fecha =
      this.route.snapshot
        .queryParamMap
        .get('fecha')
      ?? '';


    this.horaInicio =
      this.route.snapshot
        .queryParamMap
        .get('horaInicio')
      ?? '';


    this.maxExtras =
      Number(
        this.route.snapshot
          .queryParamMap
          .get('maxExtras')
        ?? 0
      );


    /*
     * Protegemos también la UI de parámetros
     * manipulados manualmente.
     *
     * Spring igualmente validará todo otra vez.
     */
    this.maxExtras =
      Math.min(
        Math.max(
          this.maxExtras,
          0
        ),
        4
      );


    if (
      !idCancha
      || !this.fecha
      || !this.horaInicio
    ) {

      this.router.navigate([
        '/disponibilidad'
      ]);

      return;
    }


    this.canchaService
      .obtenerPorId(idCancha)
      .subscribe({

        next: cancha => {

          this.cancha =
            cancha;

          this.cargando =
            false;

          this.cdRef.markForCheck();

        },


        error: () => {

          this.cargando =
            false;

          this.errorMensaje =
            'No se pudo cargar la cancha seleccionada.';

          this.cdRef.markForCheck();

        }

      });
  }


  obtenerOpcionesExtras():
    number[] {

    return Array.from(
      {
        length:
          this.maxExtras + 1
      },

      (_, indice) =>
        indice
    );
  }


  obtenerDuracionMinutos(
    extras: number
  ): number {

    return 60
      + extras * 30;
  }


  obtenerDuracionTexto(
    extras: number
  ): string {

    const minutos =
      this.obtenerDuracionMinutos(
        extras
      );


    const horas =
      Math.floor(
        minutos / 60
      );


    const minutosRestantes =
      minutos % 60;


    if (
      minutosRestantes === 0
    ) {

      return `${horas} h`;
    }


    return `${horas} h ${minutosRestantes} min`;
  }


  calcularTotal(): number {

    if (!this.cancha) {
      return 0;
    }


    const extras =
      this.form.controls
        .cantidadExtras.value;


    return this.cancha.precioHora
      + extras
      * (
        this.cancha.precioHora / 2
      );
  }


  crearReserva(): void {

    if (
      this.form.invalid
      || !this.cancha
    ) {

      this.form.markAllAsTouched();

      return;
    }


    this.guardando = true;

    this.errorMensaje = '';


    const datos =
      this.form.getRawValue();


    const request:
      ReservaWebRequest = {

      nombreCompleto:
        datos.nombreCompleto.trim(),

      tipoDocumento:
        datos.tipoDocumento,

      numDocumento:
        datos.numDocumento.trim(),

      email:
        datos.email.trim(),

      telefono:
        datos.telefono.trim(),

      idCancha:
        this.cancha.idCancha,

      fechaTurno:
        this.fecha,

      horaInicio:
        this.horaInicio,

      cantidadExtras:
        datos.cantidadExtras
    };


    this.reservaService
      .crearWeb(request)
      .subscribe({

        next: reserva => {

          this.guardando =
            false;

          this.reservaCreada =
            reserva;

          this.cdRef.markForCheck();

        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.guardando =
            false;


          if (
            error.error?.message
          ) {

            this.errorMensaje =
              error.error.message;

          } else {
            this.errorMensaje =
              'No se pudo registrar la reserva.';

            this.cdRef.markForCheck();
          }



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