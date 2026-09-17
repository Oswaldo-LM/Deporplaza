import { Injectable } from '@angular/core';

import { LoginResponse } from '../models/auth.model';


@Injectable({
  providedIn: 'root'
})
export class SessionService {

  private readonly TOKEN_KEY =
    'deporplaza_access_token';

  private readonly USER_KEY =
    'deporplaza_user';


  guardarSesion(
    response: LoginResponse
  ): void {

    localStorage.setItem(
      this.TOKEN_KEY,
      response.accessToken
    );

    localStorage.setItem(
      this.USER_KEY,
      JSON.stringify(response)
    );
  }


  obtenerToken(): string | null {

    const token =
      localStorage.getItem(
        this.TOKEN_KEY
      );


    if (!token) {
      return null;
    }


    if (this.tokenExpirado(token)) {

      this.limpiarSesion();

      return null;
    }


    return token;
  }


  obtenerUsuario():
    LoginResponse | null {

    const raw =
      localStorage.getItem(
        this.USER_KEY
      );


    if (!raw) {
      return null;
    }


    try {

      return JSON.parse(
        raw
      ) as LoginResponse;

    } catch {

      this.limpiarSesion();

      return null;
    }
  }


  estaAutenticado(): boolean {

    return this.obtenerToken()
      !== null;
  }


  esAdmin(): boolean {

    if (
      !this.estaAutenticado()
    ) {

      return false;
    }


    return this.obtenerUsuario()?.rol
      === 'ADMIN';
  }


  esCliente(): boolean {

    if (
      !this.estaAutenticado()
    ) {

      return false;
    }


    return this.obtenerUsuario()?.rol
      === 'CLIENTE';
  }


  limpiarSesion(): void {

    localStorage.removeItem(
      this.TOKEN_KEY
    );

    localStorage.removeItem(
      this.USER_KEY
    );
  }


  private tokenExpirado(
    token: string
  ): boolean {

    try {

      const partes =
        token.split('.');


      if (
        partes.length !== 3
      ) {

        return true;
      }


      const payload =
        JSON.parse(
          this.decodificarBase64Url(
            partes[1]
          )
        ) as {
          exp?: number;
        };


      if (
        !payload.exp
      ) {

        return true;
      }


      const expiracion =
        payload.exp * 1000;


      return Date.now()
        >= expiracion;

    } catch {

      return true;
    }
  }


  private decodificarBase64Url(
    valor: string
  ): string {

    let base64 =
      valor
        .replace(/-/g, '+')
        .replace(/_/g, '/');


    while (
      base64.length % 4 !== 0
    ) {

      base64 += '=';
    }


    const binario =
      atob(base64);


    const bytes =
      Uint8Array.from(
        binario,
        caracter =>
          caracter.charCodeAt(0)
      );


    return new TextDecoder()
      .decode(bytes);
  }

}