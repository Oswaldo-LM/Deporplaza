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
  EstadoSede
} from '../../../core/models/enums';

import {
  SedeRequest,
  SedeResponse
} from '../../../core/models/sede.model';

import {
  SedeService
} from '../../../core/services/sede';


@Component({
  selector: 'app-sedes',

  imports: [
    ReactiveFormsModule
  ],

  templateUrl: './sedes.html',

  styleUrl: './sedes.scss'
})
export class Sedes
  implements OnInit {

  private readonly fb =
    inject(FormBuilder);

  private readonly sedeService =
    inject(SedeService);

  private readonly cdr =
    inject(ChangeDetectorRef);


  sedes:
    SedeResponse[] = [];


  sedeEditando:
    SedeResponse | null = null;


  cargando = false;

  guardando = false;


  errorMensaje = '';

  exitoMensaje = '';


  readonly estados:
    EstadoSede[] = [
      'ACTIVA',
      'INACTIVA'
    ];


  form =
    this.fb.nonNullable.group({

      nombre: [
        '',
        [
          Validators.required,
          Validators.maxLength(100)
        ]
      ],

      direccion: [
        '',
        [
          Validators.required,
          Validators.maxLength(200)
        ]
      ],

      telefono: [
        '',
        [
          Validators.maxLength(20)
        ]
      ],

      estado: [
        'ACTIVA' as EstadoSede,
        [
          Validators.required
        ]
      ]

    });


  ngOnInit(): void {

    this.cargarSedes();
  }


  cargarSedes(): void {

    this.cargando =
      true;

    this.errorMensaje = '';


    this.sedeService
      .listar()
      .subscribe({

        next: sedes => {

          this.sedes =
            sedes;

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
            ?? 'No se pudieron cargar las sedes.';

          this.cdr.markForCheck();
        }

      });
  }


  nuevaSede(): void {

    this.sedeEditando =
      null;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    this.form.reset({

      nombre: '',

      direccion: '',

      telefono: '',

      estado:
        'ACTIVA'

    });
  }


  editar(
    sede: SedeResponse
  ): void {

    this.sedeEditando =
      sede;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    this.form.setValue({

      nombre:
        sede.nombre,

      direccion:
        sede.direccion,

      telefono:
        sede.telefono ?? '',

      estado:
        sede.estado

    });


    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  }


  cancelarEdicion(): void {

    this.nuevaSede();
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
      SedeRequest = {

      nombre:
        datos.nombre.trim(),

      direccion:
        datos.direccion.trim(),

      telefono:
        datos.telefono.trim(),

      estado:
        datos.estado

    };


    this.guardando =
      true;

    this.errorMensaje = '';

    this.exitoMensaje = '';


    if (
      this.sedeEditando
    ) {

      this.actualizarSede(
        this.sedeEditando.idSede,
        request
      );

    } else {

      this.crearSede(
        request
      );

    }
  }


  private crearSede(
    request: SedeRequest
  ): void {

    this.sedeService
      .crear(
        request
      )
      .subscribe({

        next: sede => {

          this.guardando =
            false;


          this.sedes = [
            ...this.sedes,
            sede
          ];


          this.exitoMensaje =
            `Sede "${sede.nombre}" registrada correctamente.`;


          this.form.reset({

            nombre: '',

            direccion: '',

            telefono: '',

            estado:
              'ACTIVA'

          });


          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.guardando =
            false;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo registrar la sede.';

          this.cdr.markForCheck();
        }

      });
  }


  private actualizarSede(
    idSede: number,
    request: SedeRequest
  ): void {

    this.sedeService
      .actualizar(
        idSede,
        request
      )
      .subscribe({

        next: sede => {

          this.guardando =
            false;


          this.sedes =
            this.sedes.map(
              item => {

                if (
                  item.idSede
                    === sede.idSede
                ) {

                  return sede;
                }


                return item;
              }
            );


          this.exitoMensaje =
            `Sede "${sede.nombre}" actualizada correctamente.`;


          this.sedeEditando =
            null;


          this.form.reset({

            nombre: '',

            direccion: '',

            telefono: '',

            estado:
              'ACTIVA'

          });


          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.guardando =
            false;

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo actualizar la sede.';

          this.cdr.markForCheck();
        }

      });
  }


  cambiarEstado(
    sede: SedeResponse
  ): void {

    const nuevoEstado:
      EstadoSede =
        sede.estado === 'ACTIVA'
          ? 'INACTIVA'
          : 'ACTIVA';


    const accion =
      nuevoEstado === 'ACTIVA'
        ? 'activar'
        : 'desactivar';


    const confirmar =
      window.confirm(
        `¿Deseas ${accion} la sede "${sede.nombre}"?`
      );


    if (!confirmar) {
      return;
    }


    const request:
      SedeRequest = {

      nombre:
        sede.nombre,

      direccion:
        sede.direccion,

      telefono:
        sede.telefono ?? '',

      estado:
        nuevoEstado

    };


    this.sedeService
      .actualizar(
        sede.idSede,
        request
      )
      .subscribe({

        next: actualizada => {

          this.sedes =
            this.sedes.map(
              item =>
                item.idSede
                  === actualizada.idSede
                  ? actualizada
                  : item
            );


          this.exitoMensaje =
            `Sede "${actualizada.nombre}" actualizada a ${actualizada.estado}.`;


          this.errorMensaje = '';

          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo cambiar el estado de la sede.';

          this.cdr.markForCheck();
        }

      });
  }

}