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
  HttpErrorResponse
} from '@angular/common/http';

import {
  SedeResponse
} from '../../../core/models/sede.model';

import {
  CanchaResponse
} from '../../../core/models/cancha.model';

import {
  HorarioRequest,
  HorarioResponse
} from '../../../core/models/horario.model';

import {
  DiaSemana,
  EstadoHorario
} from '../../../core/models/enums';

import {
  SedeService
} from '../../../core/services/sede';

import {
  CanchaService
} from '../../../core/services/cancha';

import {
  HorarioService
} from '../../../core/services/horario';


@Component({
  selector: 'app-horarios',

  imports: [
    ReactiveFormsModule
  ],

  templateUrl: './horarios.html',

  styleUrl: './horarios.scss'
})
export class Horarios
  implements OnInit {

  private readonly fb =
    inject(FormBuilder);

  private readonly sedeService =
    inject(SedeService);

  private readonly canchaService =
    inject(CanchaService);

  private readonly horarioService =
    inject(HorarioService);

  private readonly cdr =
    inject(ChangeDetectorRef);


  sedes:
    SedeResponse[] = [];


  canchas:
    CanchaResponse[] = [];


  horarios:
    HorarioResponse[] = [];


  horarioEditando:
    HorarioResponse | null = null;


  cargandoSedes = false;

  cargandoCanchas = false;

  cargandoHorarios = false;

  guardando = false;

  cambiandoEstadoId:
    number | null = null;


  errorMensaje = '';

  exitoMensaje = '';


  readonly dias:
    DiaSemana[] = [

      'LUNES',
      'MARTES',
      'MIERCOLES',
      'JUEVES',
      'VIERNES',
      'SABADO',
      'DOMINGO'

    ];


  readonly estados:
    EstadoHorario[] = [
      'ACTIVO',
      'INACTIVO'
    ];


  selectorForm =
    this.fb.nonNullable.group({

      sedeId: [
        0,
        [
          Validators.required,
          Validators.min(1)
        ]
      ],

      idCancha: [
        0,
        [
          Validators.required,
          Validators.min(1)
        ]
      ]

    });


  form =
    this.fb.nonNullable.group({

      diaSemana: [
        'LUNES' as DiaSemana,
        [
          Validators.required
        ]
      ],

      horaApertura: [
        '08:00',
        [
          Validators.required
        ]
      ],

      horaCierre: [
        '22:00',
        [
          Validators.required
        ]
      ],

      estado: [
        'ACTIVO' as EstadoHorario,
        [
          Validators.required
        ]
      ]

    });


  ngOnInit(): void {

    this.cargarSedes();


    this.selectorForm.controls
      .sedeId
      .valueChanges
      .subscribe(
        idSede => {

          this.selectorForm.controls
            .idCancha
            .setValue(
              0,
              {
                emitEvent: false
              }
            );


          this.canchas = [];

          this.horarios = [];

          this.horarioEditando =
            null;


          if (
            idSede > 0
          ) {

            this.cargarCanchas(
              idSede
            );
          }


          this.cdr.markForCheck();
        }
      );


    this.selectorForm.controls
      .idCancha
      .valueChanges
      .subscribe(
        idCancha => {

          this.horarioEditando =
            null;

          this.horarios = [];


          if (
            idCancha > 0
          ) {

            this.cargarHorarios(
              idCancha
            );
          }


          this.cdr.markForCheck();
        }
      );
  }


  cargarSedes(): void {

    this.cargandoSedes =
      true;


    this.sedeService
      .listar()
      .subscribe({

        next: sedes => {

          this.sedes =
            sedes;

          this.cargandoSedes =
            false;

          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.cargandoSedes =
            false;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudieron cargar las sedes.';

          this.cdr.markForCheck();
        }

      });
  }


  cargarCanchas(
    idSede: number
  ): void {

    this.cargandoCanchas =
      true;


    this.canchaService
      .listarPorSede(
        idSede
      )
      .subscribe({

        next: canchas => {

          this.canchas =
            canchas;

          this.cargandoCanchas =
            false;

          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.cargandoCanchas =
            false;

          this.canchas = [];

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudieron cargar las canchas.';

          this.cdr.markForCheck();
        }

      });
  }


  cargarHorarios(
    idCancha?: number
  ): void {

    const cancha =
      idCancha
      ?? this.selectorForm.controls
        .idCancha.value;


    if (
      cancha <= 0
    ) {

      this.horarios = [];

      return;
    }


    this.cargandoHorarios =
      true;

    this.errorMensaje = '';


    this.horarioService
      .listarPorCancha(
        cancha
      )
      .subscribe({

        next: horarios => {

          this.horarios =
            this.ordenarHorarios(
              horarios
            );

          this.cargandoHorarios =
            false;

          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.cargandoHorarios =
            false;

          this.horarios = [];

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudieron cargar los horarios.';

          this.cdr.markForCheck();
        }

      });
  }


  nuevoHorario(): void {

    this.horarioEditando =
      null;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    this.form.reset({

      diaSemana:
        'LUNES',

      horaApertura:
        '08:00',

      horaCierre:
        '22:00',

      estado:
        'ACTIVO'

    });
  }


  editar(
    horario: HorarioResponse
  ): void {

    this.horarioEditando =
      horario;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    this.form.setValue({

      diaSemana:
        horario.diaSemana,

      horaApertura:
        this.formatearHora(
          horario.horaApertura
        ),

      horaCierre:
        this.formatearHora(
          horario.horaCierre
        ),

      estado:
        horario.estado

    });


    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  }


  cancelarEdicion(): void {

    this.nuevoHorario();
  }


  guardar(): void {

    const idCancha =
      this.selectorForm.controls
        .idCancha.value;


    if (
      idCancha <= 0
    ) {

      this.errorMensaje =
        'Selecciona una cancha antes de registrar el horario.';

      return;
    }


    if (
      this.form.invalid
    ) {

      this.form.markAllAsTouched();

      return;
    }


    const datos =
      this.form
        .getRawValue();


    /*
     * Validación sencilla antes de
     * enviar al backend.
     */
    if (
      datos.horaApertura
        >= datos.horaCierre
    ) {

      this.errorMensaje =
        'La hora de cierre debe ser posterior a la hora de apertura.';

      return;
    }


    const request:
      HorarioRequest = {

      idCancha,

      diaSemana:
        datos.diaSemana,

      horaApertura:
        datos.horaApertura,

      horaCierre:
        datos.horaCierre,

      estado:
        datos.estado

    };


    this.guardando =
      true;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    if (
      this.horarioEditando
    ) {

      this.actualizarHorario(
        this.horarioEditando
          .idHorarioCancha,
        request
      );

    } else {

      this.crearHorario(
        request
      );

    }
  }


  private crearHorario(
    request: HorarioRequest
  ): void {

    this.horarioService
      .crear(
        request
      )
      .subscribe({

        next: horario => {

          this.guardando =
            false;


          this.horarios =
            this.ordenarHorarios([
              ...this.horarios,
              horario
            ]);


          this.form.reset({

            diaSemana:
              'LUNES',

            horaApertura:
              '08:00',

            horaCierre:
              '22:00',

            estado:
              'ACTIVO'

          });


          this.exitoMensaje =
            `Horario de ${horario.diaSemana} registrado correctamente.`;


          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.guardando =
            false;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo registrar el horario.';

          this.cdr.markForCheck();
        }

      });
  }


  private actualizarHorario(
    idHorario: number,
    request: HorarioRequest
  ): void {

    this.horarioService
      .actualizar(
        idHorario,
        request
      )
      .subscribe({

        next: horario => {

          this.guardando =
            false;

          this.horarioEditando =
            null;


          this.horarios =
            this.ordenarHorarios(
              this.horarios.map(
                item =>
                  item.idHorarioCancha
                    === horario.idHorarioCancha
                    ? horario
                    : item
              )
            );


          this.form.reset({

            diaSemana:
              'LUNES',

            horaApertura:
              '08:00',

            horaCierre:
              '22:00',

            estado:
              'ACTIVO'

          });


          this.exitoMensaje =
            `Horario de ${horario.diaSemana} actualizado correctamente.`;


          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.guardando =
            false;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo actualizar el horario.';

          this.cdr.markForCheck();
        }

      });
  }


  cambiarEstado(
    horario: HorarioResponse
  ): void {

    const nuevoEstado:
      EstadoHorario =
        horario.estado === 'ACTIVO'
          ? 'INACTIVO'
          : 'ACTIVO';


    const confirmar =
      window.confirm(
        `¿Deseas cambiar el horario de ${horario.diaSemana} a ${nuevoEstado}?`
      );


    if (!confirmar) {
      return;
    }


    const request:
      HorarioRequest = {

      idCancha:
        horario.idCancha,

      diaSemana:
        horario.diaSemana,

      horaApertura:
        this.formatearHora(
          horario.horaApertura
        ),

      horaCierre:
        this.formatearHora(
          horario.horaCierre
        ),

      estado:
        nuevoEstado

    };


    this.cambiandoEstadoId =
      horario.idHorarioCancha;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    this.horarioService
      .actualizar(
        horario.idHorarioCancha,
        request
      )
      .subscribe({

        next: actualizado => {

          this.cambiandoEstadoId =
            null;


          this.horarios =
            this.ordenarHorarios(
              this.horarios.map(
                item =>
                  item.idHorarioCancha
                    === actualizado.idHorarioCancha
                    ? actualizado
                    : item
              )
            );


          this.exitoMensaje =
            `Horario de ${actualizado.diaSemana} cambiado a ${actualizado.estado}.`;


          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.cambiandoEstadoId =
            null;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo cambiar el estado del horario.';

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


  private ordenarHorarios(
    horarios: HorarioResponse[]
  ): HorarioResponse[] {

    const orden:
      Record<DiaSemana, number> = {

      LUNES: 1,
      MARTES: 2,
      MIERCOLES: 3,
      JUEVES: 4,
      VIERNES: 5,
      SABADO: 6,
      DOMINGO: 7

    };


    return [
      ...horarios
    ].sort(
      (
        a,
        b
      ) =>
        orden[a.diaSemana]
        -
        orden[b.diaSemana]
    );
  }

}