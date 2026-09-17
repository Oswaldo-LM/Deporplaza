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


export const adminGuard:
  CanActivateFn = () => {

    const sessionService =
      inject(SessionService);

    const router =
      inject(Router);


    /*
     * No existe ninguna sesión.
     */
    if (
      !sessionService.estaAutenticado()
    ) {

      return router.createUrlTree([
        '/admin/login'
      ]);
    }


    /*
     * Existe sesión, pero corresponde
     * a otro rol.
     */
    if (
      !sessionService.esAdmin()
    ) {

      return router.createUrlTree([
        '/'
      ]);
    }


    return true;
  };