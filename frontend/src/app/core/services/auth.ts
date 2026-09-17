import {
  Injectable,
  inject
} from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  Observable,
  tap
} from 'rxjs';

import {
  environment
} from '../../../environments/environment';

import {
  ClienteRegistroRequest,
  LoginRequest,
  LoginResponse
} from '../models/auth.model';

import {
  SessionService
} from './session.service';


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly http =
    inject(HttpClient);

  private readonly sessionService =
    inject(SessionService);


  private readonly apiUrl =
    `${environment.apiUrl}/auth`;


  /*
   * LOGIN ANTIGUO
   *
   * Lo dejamos temporalmente mientras
   * terminamos de migrar el proyecto.
   */
  login(
    request: LoginRequest
  ): Observable<LoginResponse> {

    return this.http
      .post<LoginResponse>(
        `${this.apiUrl}/login`,
        request
      )
      .pipe(

        tap(response => {

          this.sessionService
            .guardarSesion(
              response
            );

        })

      );
  }


  /*
   * LOGIN ADMINISTRADOR
   *
   * POST /api/auth/admin/login
   */
  loginAdmin(
    request: LoginRequest
  ): Observable<LoginResponse> {

    return this.http
      .post<LoginResponse>(
        `${this.apiUrl}/admin/login`,
        request
      )
      .pipe(

        tap(response => {

          this.sessionService
            .guardarSesion(
              response
            );

        })

      );
  }


  /*
   * LOGIN CLIENTE
   *
   * POST /api/auth/cliente/login
   */
  loginCliente(
    request: LoginRequest
  ): Observable<LoginResponse> {

    return this.http
      .post<LoginResponse>(
        `${this.apiUrl}/cliente/login`,
        request
      )
      .pipe(

        tap(response => {

          this.sessionService
            .guardarSesion(
              response
            );

        })

      );
  }


  /*
   * REGISTRO CLIENTE
   *
   * POST /api/auth/cliente/registro
   */
  registrarCliente(
    request: ClienteRegistroRequest
  ): Observable<LoginResponse> {

    return this.http
      .post<LoginResponse>(
        `${this.apiUrl}/cliente/registro`,
        request
      )
      .pipe(

        tap(response => {

          this.sessionService
            .guardarSesion(
              response
            );

        })

      );
  }


  /*
   * CERRAR SESIÓN
   */
  logout(): void {

    this.sessionService
      .limpiarSesion();
  }


  /*
   * TOKEN JWT
   */
  obtenerToken(): string | null {

    return this.sessionService
      .obtenerToken();
  }


  /*
   * USUARIO ACTUAL
   */
  obtenerUsuario():
    LoginResponse | null {

    return this.sessionService
      .obtenerUsuario();
  }


  /*
   * ¿HAY UNA SESIÓN VÁLIDA?
   */
  estaAutenticado(): boolean {

    return this.sessionService
      .estaAutenticado();
  }


  /*
   * ¿EL USUARIO ES ADMIN?
   */
  esAdmin(): boolean {

    return this.sessionService
      .esAdmin();
  }


  /*
   * ¿EL USUARIO ES CLIENTE?
   */
  esCliente(): boolean {

    return this.sessionService
      .esCliente();
  }

}