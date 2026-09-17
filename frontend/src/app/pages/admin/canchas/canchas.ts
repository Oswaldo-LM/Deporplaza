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
  CanchaRequest,
  CanchaResponse
} from '../../../core/models/cancha.model';

import {
  SedeResponse
} from '../../../core/models/sede.model';

import {
  EstadoCancha,
  SuperficieCancha
} from '../../../core/models/enums';

import {
  CanchaService
} from '../../../core/services/cancha';

import {
  SedeService
} from '../../../core/services/sede';


@Component({
  selector: 'app-canchas',

  imports: [
    ReactiveFormsModule
  ],

  templateUrl: './canchas.html',

  styleUrl: './canchas.scss'
})
export class Canchas
  implements OnInit {

  private readonly fb =
    inject(FormBuilder);

  private readonly canchaService =
    inject(CanchaService);

  private readonly sedeService =
    inject(SedeService);

  private readonly cdr =
    inject(ChangeDetectorRef);


  canchas:
    CanchaResponse[] = [];


  sedes:
    SedeResponse[] = [];


  canchaEditando:
    CanchaResponse | null = null;


  filtroSedeId = 0;


  cargando = false;

  cargandoSedes = false;

  guardando = false;

  cambiandoEstadoId:
    number | null = null;


  errorMensaje = '';

  exitoMensaje = '';


  readonly superficies:
    SuperficieCancha[] = [
      'GRASS',
      'LOSA'
    ];


  readonly estados:
    EstadoCancha[] = [
      'ACTIVA',
      'INACTIVA',
      'MANTENIMIENTO'
    ];


  form =
    this.fb.nonNullable.group({

      idSede: [
        0,
        [
          Validators.required,
          Validators.min(1)
        ]
      ],

      nombre: [
        '',
        [
          Validators.required,
          Validators.maxLength(100)
        ]
      ],

      superficie: [
        'GRASS' as SuperficieCancha,
        [
          Validators.required
        ]
      ],

      precioHora: [
        0,
        [
          Validators.required,
          Validators.min(0.01)
        ]
      ],

      estado: [
        'ACTIVA' as EstadoCancha,
        [
          Validators.required
        ]
      ]

    });


  ngOnInit(): void {

    this.cargarSedes();

    this.cargarCanchas();
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


  cargarCanchas(): void {

    this.cargando =
      true;

    this.errorMensaje = '';


    const consulta =
      this.filtroSedeId > 0

        ? this.canchaService
            .listarPorSede(
              this.filtroSedeId
            )

        : this.canchaService
            .listar();


    consulta.subscribe({

      next: canchas => {

        this.canchas =
          canchas;

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
          ?? 'No se pudieron cargar las canchas.';

        this.cdr.markForCheck();
      }

    });
  }


  cambiarFiltroSede(
    event: Event
  ): void {

    const select =
      event.target as HTMLSelectElement;


    this.filtroSedeId =
      Number(
        select.value
      );


    this.cargarCanchas();
  }


  nuevaCancha(): void {

    this.canchaEditando =
      null;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    this.form.reset({

      idSede: 0,

      nombre: '',

      superficie:
        'GRASS',

      precioHora: 0,

      estado:
        'ACTIVA'

    });
  }


  editar(
    cancha: CanchaResponse
  ): void {

    this.canchaEditando =
      cancha;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    this.form.setValue({

      idSede:
        cancha.idSede,

      nombre:
        cancha.nombre,

      superficie:
        cancha.superficie,

      precioHora:
        cancha.precioHora,

      estado:
        cancha.estado

    });


    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  }


  cancelarEdicion(): void {

    this.nuevaCancha();
  }


  guardar(): void {

    if (
      this.form.invalid
    ) {

      this.form
        .markAllAsTouched();

      return;
    }


    const datos =
      this.form
        .getRawValue();


    const request:
      CanchaRequest = {

      idSede:
        datos.idSede,

      nombre:
        datos.nombre.trim(),

      superficie:
        datos.superficie,

      precioHora:
        Number(
          datos.precioHora
        ),

      estado:
        datos.estado

    };


    this.guardando =
      true;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    if (
      this.canchaEditando
    ) {

      this.actualizarCancha(
        this.canchaEditando.idCancha,
        request
      );

    } else {

      this.crearCancha(
        request
      );

    }
  }


  private crearCancha(
    request: CanchaRequest
  ): void {

    this.canchaService
      .crear(
        request
      )
      .subscribe({

        next: cancha => {

          this.guardando =
            false;


          /*
           * Si no estamos filtrando,
           * la agregamos directamente.
           * Si hay filtro, recargamos.
           */
          if (
            this.filtroSedeId === 0
            ||
            this.filtroSedeId
              === cancha.idSede
          ) {

            this.canchas = [
              ...this.canchas,
              cancha
            ];
          }


          this.exitoMensaje =
            `Cancha "${cancha.nombre}" registrada correctamente.`;


          this.nuevaCancha();


          this.exitoMensaje =
            `Cancha "${cancha.nombre}" registrada correctamente.`;


          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.guardando =
            false;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo registrar la cancha.';

          this.cdr.markForCheck();
        }

      });
  }


  private actualizarCancha(
    idCancha: number,
    request: CanchaRequest
  ): void {

    this.canchaService
      .actualizar(
        idCancha,
        request
      )
      .subscribe({

        next: cancha => {

          this.guardando =
            false;


          this.canchaEditando =
            null;


          this.exitoMensaje =
            `Cancha "${cancha.nombre}" actualizada correctamente.`;


          this.form.reset({

            idSede: 0,

            nombre: '',

            superficie:
              'GRASS',

            precioHora: 0,

            estado:
              'ACTIVA'

          });


          /*
           * Es más seguro recargar porque
           * la cancha podría haber cambiado
           * de sede y dejar de coincidir con
           * el filtro actual.
           */
          this.cargarCanchas();

          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.guardando =
            false;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo actualizar la cancha.';

          this.cdr.markForCheck();
        }

      });
  }


  cambiarEstado(
    cancha: CanchaResponse,
    nuevoEstado: EstadoCancha
  ): void {

    if (
      cancha.estado === nuevoEstado
    ) {
      return;
    }


    const confirmar =
      window.confirm(
        `¿Deseas cambiar "${cancha.nombre}" de ${cancha.estado} a ${nuevoEstado}?`
      );


    if (!confirmar) {
      return;
    }


    const request:
      CanchaRequest = {

      idSede:
        cancha.idSede,

      nombre:
        cancha.nombre,

      superficie:
        cancha.superficie,

      precioHora:
        cancha.precioHora,

      estado:
        nuevoEstado

    };


    this.cambiandoEstadoId =
      cancha.idCancha;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    this.canchaService
      .actualizar(
        cancha.idCancha,
        request
      )
      .subscribe({

        next: actualizada => {

          this.cambiandoEstadoId =
            null;


          this.canchas =
            this.canchas.map(
              item =>
                item.idCancha
                  === actualizada.idCancha
                  ? actualizada
                  : item
            );


          this.exitoMensaje =
            `Cancha "${actualizada.nombre}" actualizada a ${actualizada.estado}.`;


          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.cambiandoEstadoId =
            null;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo cambiar el estado de la cancha.';

          this.cdr.markForCheck();
        }

      });
  }

}