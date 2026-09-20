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

import {
  AuthService
} from '../../core/services/auth';

import {
  ClientePerfilService
} from '../../core/services/cliente-perfil';

import {
  ReservaPendienteService
} from '../../core/services/reserva-pendiente.service';


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

  private readonly authService =
    inject(AuthService);

  private readonly clientePerfilService =
    inject(ClientePerfilService);

  private readonly cdRef =
    inject(ChangeDetectorRef);

  private readonly reservaPendienteService =
  inject(ReservaPendienteService);  


  cancha:
    CanchaResponse | null = null;


  fecha = '';

  horaInicio = '';

  maxExtras = 0;


  cargando = true;

  guardando = false;

  errorMensaje = '';


  /*
   * Indica si la persona que está haciendo
   * la reserva inició sesión como CLIENTE.
   */
  clienteAutenticado = false;


  /*
   * Mientras obtenemos:
   *
   * GET /api/cliente/perfil
   */
  cargandoPerfilCliente = false;


  /*
   * Si existe sesión CLIENTE, no permitimos
   * enviar la reserva hasta tener cargados
   * correctamente sus datos.
   */
  perfilClienteCargado = false;


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

    /*
     * Primero verificamos si existe
     * una sesión CLIENTE válida.
     */
    this.clienteAutenticado =
      this.authService
        .esCliente();


    /*
     * Cargamos cancha, fecha y horario.
     */
    this.cargarSeleccion();


    /*
     * Si inició sesión como CLIENTE,
     * cargamos automáticamente sus datos.
     *
     * Si es invitado, simplemente dejamos
     * el formulario disponible.
     */
    if (
      this.clienteAutenticado
    ) {

      this.cargarPerfilCliente();

    } else {

      this.perfilClienteCargado =
        true;
    }
  }


  // =========================================================
  // CARGAR PERFIL DEL CLIENTE AUTENTICADO
  // =========================================================

  private cargarPerfilCliente(): void {

    this.cargandoPerfilCliente =
      true;


    this.clientePerfilService
      .obtenerPerfil()
      .subscribe({

        next: perfil => {

          /*
           * Cargamos los datos almacenados
           * en TB_CLIENTE.
           */
          this.form.patchValue({

            nombreCompleto:
              perfil.nombreCompleto,

            tipoDocumento:
              perfil.tipoDocumento,

            numDocumento:
              perfil.numDocumento,

            email:
              perfil.email,

            telefono:
              perfil.telefono

          });


          /*
           * Un usuario CLIENTE autenticado
           * no necesita escribir nuevamente
           * sus datos personales.
           *
           * Además evitamos inconsistencias
           * visuales con su cuenta.
           */
          this.form.controls
            .nombreCompleto
            .disable();

          this.form.controls
            .tipoDocumento
            .disable();

          this.form.controls
            .numDocumento
            .disable();

          this.form.controls
            .email
            .disable();

          this.form.controls
            .telefono
            .disable();


          this.cargandoPerfilCliente =
            false;

          this.perfilClienteCargado =
            true;


          this.cdRef.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.cargandoPerfilCliente =
            false;

          this.perfilClienteCargado =
            false;


          if (
            error.error?.message
          ) {

            this.errorMensaje =
              error.error.message;

          } else {

            this.errorMensaje =
              'No se pudieron cargar los datos de tu cuenta.';
          }


          this.cdRef.markForCheck();
        }

      });
  }


  // =========================================================
  // CARGAR CANCHA / FECHA / HORA
  // =========================================================

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


  // =========================================================
  // EXTRAS / DURACIÓN
  // =========================================================

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


  // =========================================================
  // TOTAL
  // =========================================================

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


  // =========================================================
  // CREAR RESERVA
  // =========================================================

  crearReserva(): void {

    /*
     * Si existe sesión CLIENTE pero no
     * conseguimos cargar su perfil,
     * no intentamos crear la reserva.
     */
    if (
      this.clienteAutenticado
      &&
      !this.perfilClienteCargado
    ) {

      this.errorMensaje =
        'No se pudieron cargar correctamente los datos de tu cuenta.';

      return;
    }


    if (
      this.form.invalid
      || !this.cancha
    ) {

      this.form.markAllAsTouched();

      return;
    }


    this.guardando =
      true;

    this.errorMensaje =
      '';


    /*
     * IMPORTANTE:
     *
     * getRawValue() incluye también
     * los controles deshabilitados.
     *
     * Por eso los datos del cliente
     * autenticado seguirán formando
     * parte del request.
     */
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

          /*
   * Guardamos temporalmente la reserva.
   *
   * Es especialmente importante para
   * usuarios invitados.
   */
  this.reservaPendienteService
    .guardar(
      reserva
    );

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
          }


          this.cdRef.markForCheck();
        }

      });
  }


  // =========================================================
  // FORMATO HORA
  // =========================================================

  formatearHora(
    hora: string
  ): string {

    return hora.substring(
      0,
      5
    );
  }


  // =========================================================
  // IMAGEN CANCHA
  // =========================================================

  obtenerImagenCancha(): string {

    if (!this.cancha) {

      return 'img/cancha-1.jpg';
    }


    const superficie =
      this.cancha.superficie
        ?.toUpperCase()
      ?? '';


    if (
      superficie.includes(
        'LOSA'
      )
    ) {

      return 'img/cancha-2.jpg';
    }


    return 'img/cancha-1.jpg';
  }

}