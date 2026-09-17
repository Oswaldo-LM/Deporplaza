import {
  inject
} from '@angular/core';

import {
  CanActivateFn,
  Router
} from '@angular/router';

import {
  SessionService
} from '../services/session.service';


export const clienteGuard:
  CanActivateFn = () => {

    const sessionService =
      inject(SessionService);

    const router =
      inject(Router);


    /*
     * Si no hay sesión iniciada,
     * enviamos al login del cliente.
     */
    if (
      !sessionService.estaAutenticado()
    ) {

      return router.createUrlTree([
        '/cliente/login'
      ]);
    }


    /*
     * Si existe sesión, pero no es CLIENTE,
     * no permitimos entrar al área del cliente.
     *
     * Por ejemplo:
     * un ADMIN no puede entrar como cliente.
     */
    if (
      !sessionService.esCliente()
    ) {

      return router.createUrlTree([
        '/'
      ]);
    }


    /*
     * Usuario autenticado
     * con rol CLIENTE.
     */
    return true;
  };