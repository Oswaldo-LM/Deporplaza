import {
  Routes
} from '@angular/router';


/*
 * LAYOUTS
 */
import {
  PublicLayout
} from './layout/public-layout/public-layout';

import {
  AdminLayout
} from './layout/admin-layout/admin-layout';


/*
 * PÁGINAS PÚBLICAS
 */
import {
  Home
} from './pages/home/home';

import {
  Disponibilidad
} from './pages/disponibilidad/disponibilidad';

import {
  Reserva
} from './pages/reserva/reserva';

import {
  Pago
} from './pages/pago/pago';


/*
 * AUTENTICACIÓN CLIENTE
 */
import {
  ClienteLogin
} from './pages/cliente-login/cliente-login';

import {
  ClienteRegistro
} from './pages/cliente-registro/cliente-registro';


/*
 * ÁREA CLIENTE
 */
import {
  ClienteMisReservas
} from './pages/cliente-mis-reservas/cliente-mis-reservas';


/*
 * LOGIN ADMINISTRATIVO
 */
import {
  AdminLogin
} from './pages/admin-login/admin-login';


/*
 * PÁGINAS ADMINISTRATIVAS
 */
import {
  Dashboard
} from './pages/admin/dashboard/dashboard';

import {
  Reservas
} from './pages/admin/reservas/reservas';

import {
  ReservaPresencial
} from './pages/admin/reserva-presencial/reserva-presencial';

import {
  Pagos
} from './pages/admin/pagos/pagos';

import {
  Sedes
} from './pages/admin/sedes/sedes';

import {
  Canchas
} from './pages/admin/canchas/canchas';

import {
  Horarios
} from './pages/admin/horarios/horarios';


/*
 * GUARDS
 */
import {
  adminGuard
} from './core/guards/admin.guard';

import {
  clienteGuard
} from './core/guards/cliente.guard';


export const routes: Routes = [

  /*
   * =====================================================
   * LOGIN ADMINISTRATIVO
   * =====================================================
   *
   * Está fuera del PublicLayout
   * y fuera del AdminLayout.
   */
  {
    path: 'admin/login',
    component: AdminLogin
  },


  /*
   * =====================================================
   * PANEL ADMINISTRATIVO
   * =====================================================
   */
  {
    path: 'admin',

    component: AdminLayout,

    canActivate: [
      adminGuard
    ],

    children: [

      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard'
      },


      {
        path: 'dashboard',
        component: Dashboard
      },


      {
        path: 'reservas',
        component: Reservas
      },


      {
        path: 'reserva-presencial',
        component: ReservaPresencial
      },


      {
        path: 'pagos',
        component: Pagos
      },


      {
        path: 'sedes',
        component: Sedes
      },


      {
        path: 'canchas',
        component: Canchas
      },


      {
        path: 'horarios',
        component: Horarios
      }

    ]
  },


  /*
   * =====================================================
   * SITIO PÚBLICO / CLIENTE
   * =====================================================
   */
  {
    path: '',

    component: PublicLayout,

    children: [

      /*
       * HOME
       */
      {
        path: '',
        pathMatch: 'full',
        component: Home
      },


      /*
       * DISPONIBILIDAD
       */
      {
        path: 'disponibilidad',
        component: Disponibilidad
      },


      /*
       * RESERVA WEB
       */
      {
        path: 'reserva',
        component: Reserva
      },


      /*
       * PAGO
       */
      {
        path: 'pago/:idReserva',
        component: Pago
      },


      /*
       * LOGIN CLIENTE
       */
      {
        path: 'cliente/login',
        component: ClienteLogin
      },


      /*
       * REGISTRO CLIENTE
       */
      {
        path: 'cliente/registro',
        component: ClienteRegistro
      },


      /*
       * MIS RESERVAS
       *
       * Requiere sesión CLIENTE.
       */
      {
        path: 'cliente/mis-reservas',

        component: ClienteMisReservas,

        canActivate: [
          clienteGuard
        ]
      }

    ]
  },


  /*
   * =====================================================
   * RUTA NO EXISTENTE
   * =====================================================
   */
  {
    path: '**',
    redirectTo: ''
  }

];