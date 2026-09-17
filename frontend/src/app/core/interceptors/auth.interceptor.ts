import {
  inject
} from '@angular/core';

import {
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';

import {
  Router
} from '@angular/router';

import {
  catchError,
  throwError
} from 'rxjs';

import {
  environment
} from '../../../environments/environment';

import {
  SessionService
} from '../services/session.service';


export const authInterceptor:
  HttpInterceptorFn =
  (request, next) => {

    const sessionService =
      inject(SessionService);

    const router =
      inject(Router);


    const token =
      sessionService.obtenerToken();


    /*
     * Verificamos si la solicitud
     * pertenece a nuestra API.
     */
    const esApi =
      request.url.startsWith(
        environment.apiUrl
      );


    /*
     * Todos los endpoints de autenticación
     * son públicos:
     *
     * /auth/admin/login
     * /auth/cliente/login
     * /auth/cliente/registro
     *
     * No enviamos JWT en ellos.
     */
    const esAuth =
      request.url.startsWith(
        `${environment.apiUrl}/auth/`
      );


    /*
     * Nos ayuda a decidir a qué login
     * redirigir si una sesión deja
     * de ser válida.
     */
    const esSolicitudAdmin =
      request.url.startsWith(
        `${environment.apiUrl}/admin/`
      )
      ||
      router.url.startsWith(
        '/admin'
      );


    let requestFinal =
      request;


    /*
     * Agregamos JWT solamente cuando:
     *
     * 1. Es nuestra API.
     * 2. No es un endpoint de autenticación.
     * 3. Existe un token válido.
     */
    if (
      esApi
      &&
      !esAuth
      &&
      token
    ) {

      requestFinal =
        request.clone({

          setHeaders: {

            Authorization:
              `Bearer ${token}`

          }

        });

    }


    return next(
      requestFinal
    ).pipe(

      catchError(
        (
          error: HttpErrorResponse
        ) => {

          /*
           * Si Spring responde 401 en una
           * petición autenticada, limpiamos
           * la sesión.
           *
           * NO hacemos esto para login o
           * registro, porque un 401 allí
           * simplemente significa
           * credenciales incorrectas.
           */
          if (
            error.status === 401
            &&
            !esAuth
          ) {

            sessionService
              .limpiarSesion();


            /*
             * Si estamos trabajando dentro
             * del área administrativa:
             *
             * /admin/login
             *
             * En cualquier otra área:
             *
             * /cliente/login
             */
            if (
              esSolicitudAdmin
            ) {

              router.navigate([
                '/admin/login'
              ]);

            } else {

              router.navigate([
                '/cliente/login'
              ]);

            }

          }


          return throwError(
            () => error
          );
        }
      )

    );

  };