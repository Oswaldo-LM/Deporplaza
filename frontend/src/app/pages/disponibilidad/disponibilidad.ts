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
  BloqueDisponibilidad,
  CanchaDisponibilidad,
  DisponibilidadResponse
} from '../../core/models/disponibilidad.model';

import {
  SedeResponse
} from '../../core/models/sede.model';

import {
  SedeService
} from '../../core/services/sede';

import {
  DisponibilidadService
} from '../../core/services/disponibilidad';

import { Router } from '@angular/router';


@Component({
  selector: 'app-disponibilidad',

  imports: [
    ReactiveFormsModule
  ],

  templateUrl: './disponibilidad.html',

  styleUrl: './disponibilidad.scss'
})
export class Disponibilidad
  implements OnInit {

  private readonly fb =
    inject(FormBuilder);

  private readonly sedeService =
    inject(SedeService);

  private readonly disponibilidadService =
    inject(DisponibilidadService);

  private readonly router =
    inject(Router);

  sedes: SedeResponse[] = [];

  private readonly cdRef =
    inject(ChangeDetectorRef);

  resultado:
    DisponibilidadResponse | null = null;

  horas: string[] = [];

  cargandoSedes = false;

  consultando = false;

  errorMensaje = '';


  readonly fechaMin =
    this.formatearFecha(
      new Date()
    );


  readonly fechaMax =
    this.calcularFechaMaxima();


  form =
    this.fb.nonNullable.group({

      sedeId: [
        0,
        [
          Validators.required,
          Validators.min(1)
        ]
      ],

      fecha: [
        this.fechaMin,
        [
          Validators.required
        ]
      ]

    });


  ngOnInit(): void {

    this.cargarSedes();
  }


  cargarSedes(): void {

    this.cargandoSedes = true;


    this.sedeService
      .listarActivas()
      .subscribe({

        next: sedes => {

          this.sedes =
            sedes;

          this.cargandoSedes = false;

          this.cdRef.markForCheck();
        },

        error: () => {

          this.cargandoSedes = false;

          this.errorMensaje =
            'No se pudo cargar la lista de sedes.';

          this.cdRef.markForCheck();
        }
          
      });

  }
  consultar(): void {

    if (this.form.invalid) {

      this.form.markAllAsTouched();

      return;
    }


    this.consultando = true;

    this.errorMensaje = '';

    this.resultado = null;

    this.horas = [];


    const {
      sedeId,
      fecha
    } =
      this.form.getRawValue();


    this.disponibilidadService
      .consultar(
        sedeId,
        fecha
      )
      .subscribe({

        next: resultado => {

          this.resultado =
            resultado;

          this.horas =
            this.obtenerHoras(
              resultado
            );

          this.consultando = false;

          this.cdRef.markForCheck();

        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.consultando = false;


          if (
            error.error?.message
          ) {

            this.errorMensaje =
              error.error.message;

          } else {

            this.errorMensaje =
              'No se pudo consultar la disponibilidad.';
          }

          this.cdRef.markForCheck();

        }
        

      });

  }


  obtenerBloque(
    cancha: CanchaDisponibilidad,
    hora: string
  ): BloqueDisponibilidad | null {

    return cancha.bloques.find(
      bloque =>
        bloque.horaInicio === hora
    ) ?? null;
  }


  formatearHora(
    hora: string
  ): string {

    return hora.substring(
      0,
      5
    );
  }


  formatearDuracion(
    minutos: number | null
  ): string {

    if (minutos === null) {
      return '';
    }


    const horas =
      Math.floor(
        minutos / 60
      );

    const minutosRestantes =
      minutos % 60;


    if (minutosRestantes === 0) {

      return `${horas} h`;
    }


    return `${horas} h ${minutosRestantes} min`;
  }


  private obtenerHoras(
    resultado: DisponibilidadResponse
  ): string[] {

    const horas =
      resultado.canchas
        .flatMap(
          cancha =>
            cancha.bloques.map(
              bloque =>
                bloque.horaInicio
            )
        );


    return [
      ...new Set(horas)
    ].sort();
  }


  private formatearFecha(
    fecha: Date
  ): string {

    const year =
      fecha.getFullYear();

    const month =
      String(
        fecha.getMonth() + 1
      ).padStart(
        2,
        '0'
      );

    const day =
      String(
        fecha.getDate()
      ).padStart(
        2,
        '0'
      );


    return `${year}-${month}-${day}`;
  }


  private calcularFechaMaxima():
    string {

    const hoy =
      new Date();


    const year =
      hoy.getFullYear();

    const month =
      hoy.getMonth();

    const day =
      hoy.getDate();


    /*
     * Calculamos el próximo mes
     * manteniendo el comportamiento
     * similar a LocalDate.plusMonths(1).
     */
    const primerDiaMesDestino =
      new Date(
        year,
        month + 1,
        1
      );


    const ultimoDiaMesDestino =
      new Date(
        primerDiaMesDestino.getFullYear(),
        primerDiaMesDestino.getMonth() + 1,
        0
      ).getDate();


    const diaDestino =
      Math.min(
        day,
        ultimoDiaMesDestino
      );


    const fechaMaxima =
      new Date(
        primerDiaMesDestino.getFullYear(),
        primerDiaMesDestino.getMonth(),
        diaDestino
      );


    return this.formatearFecha(
      fechaMaxima
    );
  }

  seleccionarHorario(
  cancha: CanchaDisponibilidad,
  bloque: BloqueDisponibilidad
  ): void {

  if (
    !bloque.disponible
    || bloque.maxExtrasDisponibles === null
  ) {
    return;
  }


  const fecha =
    this.form.controls.fecha.value;


  this.router.navigate(
    ['/reserva'],
    {
      queryParams: {
        idCancha: cancha.idCancha,
        fecha: fecha,
        horaInicio: bloque.horaInicio,
        maxExtras: bloque.maxExtrasDisponibles
      }
    }
  );
}

}