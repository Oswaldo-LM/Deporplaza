import {
  ChangeDetectorRef,
  Component,
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
  Router,
  RouterLink
} from '@angular/router';

import {
  AuthService
} from '../../core/services/auth';

import {
  SessionService
} from '../../core/services/session.service';


@Component({
  selector: 'app-admin-login',

  imports: [
    ReactiveFormsModule,
    RouterLink
  ],

  templateUrl: './admin-login.html',

  styleUrl: './admin-login.scss'
})
export class AdminLogin {

  private readonly fb =
    inject(FormBuilder);

  private readonly authService =
    inject(AuthService);

  private readonly sessionService =
    inject(SessionService);

  private readonly router =
    inject(Router);

  private readonly cdr =
    inject(ChangeDetectorRef);


  cargando = false;

  errorMensaje = '';


  form =
    this.fb.nonNullable.group({

      email: [
        '',
        [
          Validators.required,
          Validators.email
        ]
      ],

      password: [
        '',
        [
          Validators.required
        ]
      ]

    });


  iniciarSesion(): void {

    if (
      this.form.invalid
    ) {

      this.form.markAllAsTouched();

      return;
    }


    this.cargando =
      true;

    this.errorMensaje =
      '';


    const request =
      this.form.getRawValue();


    this.authService
      .loginAdmin(request)
      .subscribe({

        next: respuesta => {

          /*
           * Temporalmente el backend utiliza
           * /api/auth/login para todos.
           *
           * Por eso comprobamos también aquí
           * que quien entra por este portal
           * sea ADMIN.
           */
          if (
            respuesta.rol !== 'ADMIN'
          ) {

            this.sessionService
              .limpiarSesion();


            this.cargando =
              false;


            this.errorMensaje =
              'Esta cuenta no tiene permisos de administrador.';


            this.cdr.markForCheck();

            return;
          }


          this.cargando =
            false;


          this.router.navigate([
            '/admin/dashboard'
          ]);

        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.cargando =
            false;


          if (
            error.status === 401
          ) {

            this.errorMensaje =
              'Correo o contraseña incorrectos.';

          } else {

            this.errorMensaje =
              error.error?.message
              ?? 'No se pudo iniciar sesión.';
          }


          this.cdr.markForCheck();
        }

      });
  }

}