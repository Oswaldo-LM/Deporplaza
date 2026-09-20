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
  HttpErrorResponse,
  HttpResponse
} from '@angular/common/http';

import {
  ReporteService
} from '../../../core/services/reporte.service';

import {
  SedeService
} from '../../../core/services/sede';

import {
  ReporteReserva,
  ReporteReservaFiltros
} from '../../../core/models/reporte.model';

import {
  SedeResponse
} from '../../../core/models/sede.model';

import {
  EstadoReserva
} from '../../../core/models/enums';


@Component({
  selector: 'app-reportes',

  imports: [
    ReactiveFormsModule
  ],

  templateUrl: './reportes.html',

  styleUrl: './reportes.scss'
})
export class Reportes
  implements OnInit {

  private readonly fb =
    inject(FormBuilder);

  private readonly reporteService =
    inject(ReporteService);

  private readonly sedeService =
    inject(SedeService);

  private readonly cdr =
    inject(ChangeDetectorRef);


  reservas:
    ReporteReserva[] = [];


  sedes:
    SedeResponse[] = [];


  cargando =
    false;


  exportando =
    false;


  errorMensaje =
    '';


  resumen = {

    totalReservas: 0,

    confirmadas: 0,

    pendientes: 0,

    ingresos: 0

  };


  readonly estadosReserva:
    EstadoReserva[] = [

      'PENDIENTE_PAGO',

      'PENDIENTE_CONFIRMACION',

      'CONFIRMADA',

      'CANCELADA',

      'EXPIRADA',

      'COMPLETADA'

    ];


 form =
  this.fb.nonNullable.group({

    desde:
      this.fb.nonNullable.control<string>(
        ''
      ),

    hasta:
      this.fb.nonNullable.control<string>(
        ''
      ),

    idSede:
      this.fb.nonNullable.control<number | ''>(
        ''
      ),

    estado:
      this.fb.nonNullable.control<EstadoReserva | ''>(
        ''
      )

  });


  ngOnInit(): void {

    this.cargarSedes();

    this.consultar();
  }


  // =========================================================
  // SEDES
  // =========================================================

  private cargarSedes(): void {

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


  // =========================================================
  // CONSULTAR
  // =========================================================

  consultar(): void {

    this.errorMensaje =
      '';


    if (
      !this.fechasValidas()
    ) {

      return;
    }


    this.cargando =
      true;


    const filtros =
      this.obtenerFiltros();


    this.reporteService
      .obtenerReservas(
        filtros
      )
      .subscribe({

        next: reservas => {

          this.reservas =
            reservas;

          this.actualizarResumen();


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
            ?? 'No se pudo generar el reporte.';


          this.cdr.markForCheck();
        }

      });
  }


  // =========================================================
  // LIMPIAR FILTROS
  // =========================================================

  limpiarFiltros(): void {

  this.form.reset({

    desde: '',

    hasta: '',

    idSede: '',

    estado: ''

  });


  this.consultar();
}


  // =========================================================
  // EXPORTAR CSV
  // =========================================================

  exportarCsv(): void {

    this.errorMensaje =
      '';


    if (
      !this.fechasValidas()
    ) {

      return;
    }


    this.exportando =
      true;


    const filtros =
      this.obtenerFiltros();


    this.reporteService
      .exportarReservas(
        filtros
      )
      .subscribe({

        next: response => {

          this.descargarArchivo(
            response
          );


          this.exportando =
            false;

          this.cdr.markForCheck();
        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.exportando =
            false;


          this.errorMensaje =
            error.error?.message
            ?? 'No se pudo exportar el reporte.';


          this.cdr.markForCheck();
        }

      });
  }


  // =========================================================
  // DESCARGAR ARCHIVO
  // =========================================================

  private descargarArchivo(
    response: HttpResponse<Blob>
  ): void {

    if (!response.body) {

      this.errorMensaje =
        'El archivo exportado está vacío.';

      return;
    }


    let nombreArchivo =
      'reporte_reservas.csv';


    const contentDisposition =
      response.headers.get(
        'Content-Disposition'
      );


    if (contentDisposition) {

      const resultado =
        /filename="?([^"]+)"?/i.exec(
          contentDisposition
        );


      if (
        resultado
        &&
        resultado[1]
      ) {

        nombreArchivo =
          resultado[1];
      }
    }


    const url =
      URL.createObjectURL(
        response.body
      );


    const enlace =
      document.createElement(
        'a'
      );


    enlace.href =
      url;


    enlace.download =
      nombreArchivo;


    document.body.appendChild(
      enlace
    );


    enlace.click();


    enlace.remove();


    URL.revokeObjectURL(
      url
    );
  }


  // =========================================================
  // FILTROS
  // =========================================================

  private obtenerFiltros():
  ReporteReservaFiltros {

  const datos =
    this.form.getRawValue();


  const filtros:
    ReporteReservaFiltros = {};


  if (datos.desde) {

    filtros.desde =
      datos.desde;
  }


  if (datos.hasta) {

    filtros.hasta =
      datos.hasta;
  }


  if (datos.idSede !== '') {

    filtros.idSede =
      datos.idSede;
  }


  if (datos.estado !== '') {

    filtros.estado =
      datos.estado;
  }


  return filtros;
}


  // =========================================================
  // VALIDACIÓN DE FECHAS
  // =========================================================

  private fechasValidas(): boolean {

    const {
      desde,
      hasta
    } =
      this.form.getRawValue();


    if (
      desde
      &&
      hasta
      &&
      desde > hasta
    ) {

      this.errorMensaje =
        'La fecha desde no puede ser posterior a la fecha hasta.';


      return false;
    }


    return true;
  }


  // =========================================================
  // RESUMEN
  // =========================================================

  private actualizarResumen(): void {

    const totalReservas =
      this.reservas.length;


    const confirmadas =
      this.reservas
        .filter(
          reserva =>
            reserva.estadoReserva
            === 'CONFIRMADA'
        )
        .length;


    const pendientes =
      this.reservas
        .filter(
          reserva =>

            reserva.estadoReserva
              === 'PENDIENTE_PAGO'

            ||

            reserva.estadoReserva
              === 'PENDIENTE_CONFIRMACION'
        )
        .length;


    const ingresos =
      this.reservas

        .filter(
          reserva =>
            reserva.estadoPago
            === 'APROBADO'
        )

        .reduce(
          (
            acumulado,
            reserva
          ) =>

            acumulado
            + Number(
              reserva.total
            ),

          0
        );


    this.resumen = {

      totalReservas,

      confirmadas,

      pendientes,

      ingresos

    };
  }


  // =========================================================
  // FORMATOS
  // =========================================================

  formatearFecha(
    fecha: string
  ): string {

    if (!fecha) {

      return '-';
    }


    const partes =
      fecha.split('-');


    if (
      partes.length !== 3
    ) {

      return fecha;
    }


    return `${partes[2]}/${partes[1]}/${partes[0]}`;
  }


  formatearHora(
    hora: string
  ): string {

    if (!hora) {

      return '-';
    }


    return hora.substring(
      0,
      5
    );
  }


  formatearMonto(
    monto: number
  ): string {

    return new Intl.NumberFormat(
      'es-PE',
      {
        style: 'currency',
        currency: 'PEN'
      }
    ).format(
      monto
    );
  }


  estadoTexto(
    estado: EstadoReserva
  ): string {

    switch (estado) {

      case 'PENDIENTE_PAGO':
        return 'Pendiente de pago';

      case 'PENDIENTE_CONFIRMACION':
        return 'Pago en validación';

      case 'CONFIRMADA':
        return 'Confirmada';

      case 'CANCELADA':
        return 'Cancelada';

      case 'EXPIRADA':
        return 'Expirada';

      case 'COMPLETADA':
        return 'Completada';

      default:
        return estado;
    }
  }


  estadoClase(
    estado: EstadoReserva
  ): string {

    switch (estado) {

      case 'CONFIRMADA':
        return 'text-bg-success';

      case 'COMPLETADA':
        return 'text-bg-primary';

      case 'PENDIENTE_PAGO':
        return 'text-bg-warning';

      case 'PENDIENTE_CONFIRMACION':
        return 'text-bg-info';

      case 'CANCELADA':
        return 'text-bg-danger';

      case 'EXPIRADA':
        return 'text-bg-secondary';

      default:
        return 'text-bg-light';
    }
  }

}