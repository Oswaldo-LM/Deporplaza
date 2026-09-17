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


@Component({
  selector: 'app-cliente-login',

  imports: [
    ReactiveFormsModule,
    RouterLink
  ],

  templateUrl: './cliente-login.html',

  styleUrl: './cliente-login.scss'
})
export class ClienteLogin {

  private readonly fb =
    inject(FormBuilder);

  private readonly authService =
    inject(AuthService);

  private readonly router =
    inject(Router);

  private readonly cdr =
    inject(ChangeDetectorRef);


  cargando =
    false;

  errorMensaje =
    '';


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


    this.authService
      .loginCliente(
        this.form.getRawValue()
      )
      .subscribe({

        next: respuesta => {

          this.cargando =
            false;


          if (
            respuesta.rol
            !== 'CLIENTE'
          ) {

            this.authService
              .logout();


            this.errorMensaje =
              'Esta cuenta no corresponde a un cliente.';


            this.cdr
              .markForCheck();

            return;
          }


          /*
           * Por ahora volvemos al inicio.
           *
           * Más adelante lo cambiaremos por:
           *
           * /cliente/mis-reservas
           */
          this.router.navigate([
            '/cliente/mis-reservas'
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


          this.cdr
            .markForCheck();
        }

      });
  }

}