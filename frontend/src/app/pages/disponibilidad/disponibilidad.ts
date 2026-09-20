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
  CommonModule
} from '@angular/common';

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


interface DiaTab {
  iso: string;
  diaSemana: string;
  dia: string;
  mes: string;
  esHoy: boolean;
}


interface Seleccion {
  cancha: CanchaDisponibilidad;
  bloque: BloqueDisponibilidad;
}


@Component({
  selector: 'app-disponibilidad',

  imports: [
    ReactiveFormsModule,
    CommonModule
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

  private readonly cdRef =
    inject(ChangeDetectorRef);

  sedes: SedeResponse[] = [];

  resultado: DisponibilidadResponse | null = null;

  horas: string[] = [];

  cargandoSedes = false;

  consultando = false;

  errorMensaje = '';

  ultimaActualizacion: Date | null = null;

  diasSemana: DiaTab[] = [];

  seleccion: Seleccion | null = null;

  sedeDropdownAbierto = false;


  readonly fechaMin =
    this.formatearFecha(new Date());

  readonly fechaMax =
    this.calcularFechaMaxima();


  form =
    this.fb.nonNullable.group({

      sedeId: [
        0,
        [Validators.required, Validators.min(1)]
      ],

      fecha: [
        this.fechaMin,
        [Validators.required]
      ]

    });


  ngOnInit(): void {
    this.diasSemana = this.generarDiasSemana();
    this.cargarSedes();
  }


  cargarSedes(): void {

    this.cargandoSedes = true;

    this.sedeService
      .listarActivas()
      .subscribe({

        next: sedes => {
          this.sedes = sedes;
          this.cargandoSedes = false;
          this.cdRef.markForCheck();
        },

        error: () => {
          this.cargandoSedes = false;
          this.errorMensaje = 'No se pudo cargar la lista de sedes.';
          this.cdRef.markForCheck();
        }

      });

  }


  toggleSedeDropdown(): void {
    this.sedeDropdownAbierto = !this.sedeDropdownAbierto;
  }


  seleccionarSede(idSede: number): void {
    this.form.controls.sedeId.setValue(idSede);
    this.sedeDropdownAbierto = false;
    this.onCambioSede();
  }


  obtenerNombreSedeSeleccionada(): string {

    const idSede = this.form.controls.sedeId.value;

    if (!idSede) {
      return 'Selecciona una sede';
    }

    const sede = this.sedes.find(s => s.idSede === idSede);

    return sede ? sede.nombre : 'Selecciona una sede';
  }


  onFechaManualCambiada(event: Event): void {

    const input = event.target as HTMLInputElement;
    const fecha = input.value;

    if (fecha) {
      this.seleccionarFecha(fecha);
    }
  }


  get fechaMinSemana(): string {
    return this.diasSemana[0]?.iso ?? this.fechaMin;
  }

  get fechaMaxSemana(): string {
    return this.diasSemana[this.diasSemana.length - 1]?.iso ?? this.fechaMax;
  }


  onCambioSede(): void {
    if (this.form.controls.sedeId.valid) {
      this.consultar();
    }
  }


  seleccionarFecha(iso: string): void {
    this.form.controls.fecha.setValue(iso);

    if (this.form.controls.sedeId.valid) {
      this.consultar();
    }
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
    this.seleccion = null;

    const { sedeId, fecha } = this.form.getRawValue();

    this.disponibilidadService
      .consultar(sedeId, fecha)
      .subscribe({

        next: resultado => {
          this.resultado = resultado;
          this.horas = this.obtenerHoras(resultado);
          this.consultando = false;
          this.ultimaActualizacion = new Date();
          this.cdRef.markForCheck();
        },

        error: (error: HttpErrorResponse) => {
          this.consultando = false;

          this.errorMensaje = error.error?.message
            ?? 'No se pudo consultar la disponibilidad.';

          this.cdRef.markForCheck();
        }

      });

  }


  obtenerBloque(
    cancha: CanchaDisponibilidad,
    hora: string
  ): BloqueDisponibilidad | null {

    return cancha.bloques.find(
      bloque => bloque.horaInicio === hora
    ) ?? null;
  }


  formatearHora(hora: string): string {
    return hora.substring(0, 5);
  }


  formatearDuracion(minutos: number | null): string {

    if (minutos === null) {
      return '';
    }

    const horas = Math.floor(minutos / 60);
    const minutosRestantes = minutos % 60;

    if (minutosRestantes === 0) {
      return `${horas} h`;
    }

    return `${horas} h ${minutosRestantes} min`;
  }


  seleccionarHorario(
    cancha: CanchaDisponibilidad,
    bloque: BloqueDisponibilidad
  ): void {

    if (!bloque.disponible || bloque.maxExtrasDisponibles === null) {
      return;
    }

    this.seleccion = { cancha, bloque };
    this.cdRef.markForCheck();
  }


  esSeleccionado(
    cancha: CanchaDisponibilidad,
    bloque: BloqueDisponibilidad
  ): boolean {

    return this.seleccion !== null
      && this.seleccion.cancha.idCancha === cancha.idCancha
      && this.seleccion.bloque.horaInicio === bloque.horaInicio;
  }


  obtenerTotalSeleccion(): number {

    if (!this.seleccion) {
      return 0;
    }

    return this.seleccion.cancha.precioHora;
  }


  cambiarSeleccion(): void {
    this.seleccion = null;
  }


  continuarConReserva(): void {

    if (!this.seleccion) {
      return;
    }

    const { cancha, bloque } = this.seleccion;
    const fecha = this.form.controls.fecha.value;

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


  private obtenerHoras(resultado: DisponibilidadResponse): string[] {

    const horas = resultado.canchas
      .flatMap(cancha => cancha.bloques.map(bloque => bloque.horaInicio));

    return [...new Set(horas)].sort();
  }


  private generarDiasSemana(): DiaTab[] {

    const dias: DiaTab[] = [];
    const hoy = new Date();

    const formatoDia = new Intl.DateTimeFormat('es-PE', { weekday: 'short' });
    const formatoMes = new Intl.DateTimeFormat('es-PE', { month: 'short' });

    for (let i = 0; i < 7; i++) {

      const fecha = new Date(hoy);
      fecha.setDate(hoy.getDate() + i);

      dias.push({
        iso: this.formatearFecha(fecha),
        diaSemana: formatoDia.format(fecha).replace('.', '').toUpperCase(),
        dia: String(fecha.getDate()),
        mes: formatoMes.format(fecha).replace('.', ''),
        esHoy: i === 0
      });
    }

    return dias;
  }


  private formatearFecha(fecha: Date): string {

    const year = fecha.getFullYear();
    const month = String(fecha.getMonth() + 1).padStart(2, '0');
    const day = String(fecha.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }


  private calcularFechaMaxima(): string {

    const hoy = new Date();
    const year = hoy.getFullYear();
    const month = hoy.getMonth();
    const day = hoy.getDate();

    const primerDiaMesDestino = new Date(year, month + 1, 1);

    const ultimoDiaMesDestino = new Date(
      primerDiaMesDestino.getFullYear(),
      primerDiaMesDestino.getMonth() + 1,
      0
    ).getDate();

    const diaDestino = Math.min(day, ultimoDiaMesDestino);

    const fechaMaxima = new Date(
      primerDiaMesDestino.getFullYear(),
      primerDiaMesDestino.getMonth(),
      diaDestino
    );

    return this.formatearFecha(fechaMaxima);
  }

}