import {
  Component,
  inject
} from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  Router
} from '@angular/router';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  AuthService
} from '../../core/services/auth';


@Component({
  selector: 'app-login',

  imports: [
    ReactiveFormsModule
  ],

  templateUrl: './login.html',

  styleUrl: './login.scss'
})
export class Login {

  private readonly fb =
    inject(FormBuilder);

  private readonly authService =
    inject(AuthService);

  private readonly router =
    inject(Router);


  cargando = false;

  errorMensaje = '';


  form = this.fb.nonNullable.group({

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

    if (this.form.invalid) {

      this.form.markAllAsTouched();

      return;
    }


    this.cargando = true;

    this.errorMensaje = '';


    this.authService
      .login(
        this.form.getRawValue()
      )
      .subscribe({

        next: () => {

          this.cargando = false;

          this.router.navigate([
            '/admin/dashboard'
          ]);

        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.cargando = false;


          if (error.status === 401) {

            this.errorMensaje =
              'Correo o contraseña incorrectos.';

            return;
          }


          this.errorMensaje =
            'No se pudo iniciar sesión. Intenta nuevamente.';

        }

      });

  }

}