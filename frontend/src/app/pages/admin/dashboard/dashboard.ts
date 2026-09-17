import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  RouterLink
} from '@angular/router';

import {
  DashboardResumen
} from '../../../core/models/dashboard.model';

import {
  DashboardService
} from '../../../core/services/dashboard.service';


@Component({
  selector: 'app-dashboard',

  imports: [
    RouterLink
  ],

  templateUrl: './dashboard.html',

  styleUrl: './dashboard.scss'
})
export class Dashboard
  implements OnInit {

  private readonly dashboardService =
    inject(DashboardService);

  private readonly cdr =
    inject(ChangeDetectorRef);


  resumen:
    DashboardResumen | null = null;


  cargando = false;

  errorMensaje = '';


  ngOnInit(): void {

    this.cargarDashboard();
  }


  cargarDashboard(): void {

    this.cargando =
      true;

    this.errorMensaje = '';


    this.dashboardService
      .obtenerResumen()
      .subscribe({

        next: resumen => {

          this.resumen =
            resumen;

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
            ?? 'No se pudo cargar el dashboard.';

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


  formatearDinero(
    monto: number
  ): string {

    return Number(
      monto
    ).toFixed(
      2
    );
  }

}