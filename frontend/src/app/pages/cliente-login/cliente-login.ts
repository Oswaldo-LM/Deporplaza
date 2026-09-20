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

  private readonly fb = inject(FormBuilder);

  private readonly authService = inject(AuthService);

  private readonly router = inject(Router);

  private readonly cdr = inject(ChangeDetectorRef);


  cargando = false;

  errorMensaje = '';

  mostrarPassword = false;


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
    ],

    // Solo UI por ahora: no se envía al backend.
    recordarme: [false]

  });


  alternarPassword(): void {

    this.mostrarPassword = !this.mostrarPassword;
  }


  continuarConGoogle(): void {

    // TODO: conectar con el flujo de Google cuando exista en el backend.
    this.errorMensaje = 'El acceso con Google aún no está disponible.';
  }


  iniciarSesion(): void {

    if (this.form.invalid) {

      this.form.markAllAsTouched();

      return;
    }


    this.cargando = true;

    this.errorMensaje = '';


    // Solo se envían email y password (recordarme no va al backend).
    const { email, password } = this.form.getRawValue();


    this.authService
      .loginCliente({ email, password })
      .subscribe({

        next: respuesta => {

          this.cargando = false;


          if (respuesta.rol !== 'CLIENTE') {

            this.authService.logout();

            this.errorMensaje =
              'Esta cuenta no corresponde a un cliente.';

            this.cdr.markForCheck();

            return;
          }


          this.router.navigate([
            '/cliente/mis-reservas'
          ]);

        },


        error: (error: HttpErrorResponse) => {

          this.cargando = false;


          if (error.status === 401) {

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